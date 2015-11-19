package com.kchow2.tetris;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.example.kevin.tetris.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Kevin on 2015-10-04.
 */
public class GameActivity extends Activity implements Board.OnSavePieceListener, BoardView.FlashingAnimationListener, Dialog.OnDismissListener {
	//BoardView drawView;
    Board board = new Board();
	Score score = new Score();
	GameView gameView;
	Settings settings = new Settings();
    Timer timer;

	int dropDelayMs = 1000;

	boolean isPaused = false;
	boolean isGameOver = false;

	MusicPlayer musicPlayer;
	//MediaPlayer mediaPlayer;

	//variables used for touch screen input - moving and rotating the current piece
	float startX, startY;
	boolean isScrollingX = false, isScrollingY = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//drawView = new BoardView(this, this.board);

		board.addSavePieceListener(this);
		board.addSavePieceListener(score);

		//the main view for drawing the game board and the score, etc
		Point screenSize = new Point();
		getWindowManager().getDefaultDisplay().getSize(screenSize);
		gameView = new GameView(this, this.board, this.score, screenSize.x, screenSize.y);
		gameView.addFlashingAnimationListener(this);
		gameView.addFlashingAnimationListener(score);
		gameView.addFlashingAnimationListener(board);
		setContentView(gameView);
		newGame();

		//init music
		this.musicPlayer = new MusicPlayer(this);

		//load settings from shared prefs
		SharedPreferences sharedPrefs = this.getPreferences(Context.MODE_PRIVATE);
		settings.loadFromSharedPreferences(sharedPrefs);

		//music is started in onResume()
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		if (id == R.id.action_settings) {
			SettingsDialog dialog = new SettingsDialog();
			dialog.setSettings(this.settings);
			dialog.setMusicPlayer(this.musicPlayer);
			dialog.show(getFragmentManager(), "dialog");
			this.pauseGame();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDismiss(DialogInterface dialog){
		//when the settings dialog is closed, save the preferences and resume
		SharedPreferences sharedPrefs = this.getPreferences(Context.MODE_PRIVATE);
		settings.saveToSharedPreferences(sharedPrefs);
		this.unPauseGame();
	}

	@Override
	public void onPause(){
		super.onPause();
		musicPlayer.stop();
	}

	@Override
	public void onResume(){
		super.onResume();
		if(settings.isMusicOn()) {
			musicPlayer.setVolume(settings.getVolume());
			musicPlayer.play(settings.getMusicSelection());
		}
	}

	/*
	*	The main game loop. This gets called every game tick.
	 */
    public void onTick(){
        //System.out.println("TICK");
        if(isGameOver || isPaused)
			return;

		board.moveDown();
		redrawGameScreen();

		isGameOver = board.isGameOver();
        if(isGameOver)
			onGameOver();
        //System.out.println("TOCK");
    }

	/*
	* In case there is a keyboard connected, we can use it to control movement as well
	 */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
		//user shouldn't be able to move piece while the game is paused.
		if(isGameOver || isPaused){
			return super.onKeyDown(keyCode, event);
		}

		boolean needRedraw = false;

		switch (keyCode) {
            case KeyEvent.KEYCODE_W:
			case KeyEvent.KEYCODE_DPAD_UP:
                board.rotate();
				needRedraw = true;
                break;
            case KeyEvent.KEYCODE_S:
			case KeyEvent.KEYCODE_DPAD_DOWN:
                board.moveDown();
				needRedraw = true;
                break;
            case KeyEvent.KEYCODE_A:
			case KeyEvent.KEYCODE_DPAD_LEFT:
                board.moveLeft();
				needRedraw = true;
                break;
            case KeyEvent.KEYCODE_D:
			case KeyEvent.KEYCODE_DPAD_RIGHT:
                board.moveRight();
				needRedraw = true;
                break;
            case KeyEvent.KEYCODE_SPACE:
				while(board.moveDown()){}	//hard down.
				needRedraw = true;
                break;
            default:
                return super.onKeyDown(keyCode, event);
        }
		if(needRedraw)
        	redrawGameScreen();

		isGameOver = board.isGameOver();
		if(isGameOver)
			onGameOver();
        return true;
    }

	/*
	* Handles user gestures on the screen and interprets them as move/rotate commands.
	* Controls: hold and drag to move the piece, and tap to rotate.
	 */
	public boolean onTouchEvent(MotionEvent event){
		//user shouldn't be able to move piece while the game is paused
		if(isGameOver || isPaused)
			return super.onTouchEvent(event);

		boolean needRedraw = false;

		int actionCode = event.getAction();
		if(actionCode == MotionEvent.ACTION_DOWN) {
			startX = event.getRawX();
			startY = event.getRawY();
		}
		if(actionCode==MotionEvent.ACTION_MOVE){
			float x = event.getRawX();
			float y = event.getRawY();

			float dx = x - startX;
			float dy = y - startY;

			boolean lockScrollingToAxis = settings.isDragLock();
			float xThreshold = 10 + (100-settings.getDragSensitivity()); //50.0f;	//the sensitivity for moving piece left/right. Higher value means it is less sensitive.
			float yThreshold = 10 + (100-settings.getDragSensitivity()); //80.0f;	//the sensitivity for moving piece down. Higher value means it is less sensitive.
			if((lockScrollingToAxis && !isScrollingY) && Math.abs(dx) > xThreshold){
				if(dx < 0){
					board.moveLeft();
				}
				else{
					board.moveRight();
				}
				startX = x;
				isScrollingX = true;
			}
			if((lockScrollingToAxis && !isScrollingX) && Math.abs(dy) > yThreshold){
				if(dy > 0){
					board.moveDown();
				}
				startY = y;
				isScrollingY = true;
			}
			needRedraw = true;
		}
		if(actionCode == MotionEvent.ACTION_UP) {
			//time in milliseconds between ACTION_DOWN and ACTION_UP events.
			//If it is less than a certain value, assume it was a tap and meant to rotate the piece.
			long dt = event.getEventTime() - event.getDownTime();
			if(dt < 300 && !isScrollingX && !isScrollingY){
				board.rotate();
				needRedraw = true;
			}

			isScrollingX = false;
			isScrollingY = false;
		}
		if(needRedraw)
			redrawGameScreen();

		isGameOver = board.isGameOver();
		if(isGameOver)
			onGameOver();

		return super.onTouchEvent(event);
	}

	public void onGameOver(){
        pauseGame();

		redrawGameScreen();

		runOnUiThread(new Runnable() {
			public void run() {
				AlertDialog.Builder dlgAlert = new AlertDialog.Builder(GameActivity.this);
				dlgAlert.setMessage(
						"Score: "+score.getScore() +
						"\nLines: "+score.getLines()
				);
				dlgAlert.setTitle(R.string.game_over);
				dlgAlert.setNegativeButton("Quit", new Dialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int buttonId) {
						finish();
					}
				});
				dlgAlert.setPositiveButton("New Game", new Dialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int buttonId) {
						newGame();
					}
				});
				dlgAlert.setCancelable(true);
				dlgAlert.create().show();
			}
		});

    }

	public void redrawGameScreen(){
		runOnUiThread(new Runnable() {
			public void run() {
				gameView.redrawEverything();
			}
		});
	}

	public void newGame(){
		board.clear();
		score.clear();
		startTimer();
		isPaused = false;
		isGameOver = false;
	}

	public void pauseGame(){
		if(isPaused)
			return;

		stopTimer();
		this.isPaused = true;
	}

	public void unPauseGame(){
		if(!isPaused)
			return;

		startTimer();
		isPaused = false;
	}

	private void startTimer(){
		if(timer != null){
			throw new RuntimeException("startTimer(): Error. timer already running!");
		}

		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				onTick();
			}
		}, 0, dropDelayMs);
	}

	private void stopTimer(){
		if(timer != null) {
			timer.cancel();
			timer = null;    //for some reason, we can't reuse timers. We need to new another one to restart it.
		}
	}

	/*
	* When the back button is pressed, pause the game and go back to the main menu
	 */
	@Override
	 public void onBackPressed() {
		stopTimer();
		finish();
	}

	@Override
	public void onSavePiece(int pieceType, int pieceRot, int x, int y){

	}

	@Override
	public void onFullLines(ArrayList<Integer> fullLines, int pieceType, int pieceRot, int x, int y){
		//System.out.println("Full lines listener called.");
		pauseGame();
		gameView.flashLines(fullLines);
	}

	@Override
	public void onFlashingAnimationStart(ArrayList<Integer> fullLines){

	}

	@Override
	public void onFlashingAnimationFinish(ArrayList<Integer> fullLines){
		for (int i: fullLines){
			board.deleteLine(i);
		}
		unPauseGame();
		redrawGameScreen();
	}
}

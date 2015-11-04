package com.kchow2.tetris;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Kevin on 2015-10-04.
 */
public class BoardView extends View {

    Context context;
	Board board;
    Paint paint = new Paint();

    int widthPx, heightPx;

	private boolean isFlashing;
    Timer lineFlashTimer;
	private final int flashPeriodMs = 200;
	private int flashCount;
	private final int maxFlashCount = 6;
	private ArrayList<Integer> flashLines;
	private ArrayList<FlashingAnimationListener> flashListeners = new ArrayList<FlashingAnimationListener>();


    public BoardView(Context context, Board board) {
        super(context);
		this.context = context;
        setWillNotDraw(false);
        this.board = board;

        this.widthPx = this.getWidth();
        this.heightPx = this.getHeight();
    }

	public void flashLines(ArrayList<Integer> lines){
		isFlashing = true;
		lineFlashTimer = new Timer();
		flashLines = lines;
		flashCount = 0;
		for(FlashingAnimationListener listener : this.flashListeners){
			listener.onFlashingAnimationStart(flashLines);
		}
		lineFlashTimer.scheduleAtFixedRate(new TimerTask(){
			public void run(){
				((Activity)context).runOnUiThread(new Runnable() {
					public void run() {
						invalidate();
						flashCount++;
						if(flashCount == maxFlashCount){
							lineFlashTimer.cancel();
							lineFlashTimer = null;
							isFlashing = false;
							for(FlashingAnimationListener listener : BoardView.this.flashListeners){
								listener.onFlashingAnimationFinish(flashLines);
							}
						}
					}
				});
			}
		}, 0, flashPeriodMs);
	}

	public int getFlashAnimationTimeMs(){
		return maxFlashCount*flashPeriodMs;
	}

    @Override
    public void onDraw(Canvas canvas) {
		paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        canvas.drawRect(0, 0, widthPx, heightPx, paint);    //fill with black background, then do any drawing over this.

        for(int x = 0; x < Board.WIDTH; x++){
            for(int y = 0; y < Board.HEIGHT; y++){
                int tileColor = board.getTileOrPieceColor(x, y);
                int xPx = x * widthPx / Board.WIDTH;
                int yPx = y * heightPx / Board.HEIGHT;
                if(tileColor != 0) {
                    //System.out.println("xPx=" + xPx + " yPx=" + yPx + " color=" + tileColor + "\n");
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(Color.rgb(tileColor >> 16 & 0xFF, tileColor >> 8 & 0xFF, tileColor & 0xFF));
                    canvas.drawRect(xPx, yPx, xPx + widthPx / Board.WIDTH, yPx + heightPx / Board.HEIGHT, paint);
                }
                //border
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(2);
                paint.setColor(Color.rgb(0xFF, 0xFF, 0xFF));
                canvas.drawRect(xPx, yPx, xPx+widthPx / Board.WIDTH, yPx + heightPx / Board.HEIGHT, paint);
            }
        }

		if(isFlashing){
			//flash the lines between black and white every flashPeriodMs
			int color = 0x000000;
			if(flashCount % 2 == 1){
				color = 0xFFFFFF;
			}

			for(int y : flashLines){
				int yPx = y * heightPx / Board.HEIGHT;
				paint.setStyle(Paint.Style.FILL);
				paint.setColor(Color.rgb(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF));
				canvas.drawRect(0, yPx, widthPx, yPx + heightPx / Board.HEIGHT, paint);
			}
		}
    }

    protected void onSizeChanged (int w, int h, int oldw, int oldh){
        this.widthPx = w;
        this.heightPx = h;
    }

	public interface FlashingAnimationListener{
		public void onFlashingAnimationStart(ArrayList<Integer> lines);
		public void onFlashingAnimationFinish(ArrayList<Integer> lines);
	}

	public void addFlashingAnimationListener(FlashingAnimationListener listener){
		this.flashListeners.add(listener);
	}
}

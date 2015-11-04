package com.kchow2.tetris;

import android.content.Context;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by Kevin on 2015-10-15.
 */
public class GameView extends LinearLayout {
	Context context;
	BoardView boardView;
	ScoreView scoreView;
	int screenWidth;
	int screenHeight;

	public GameView(Context context, Board board, Score score, int screenWidth, int screenHeight) {
		super(context);
		this.context = context;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;

		boardView = new BoardView(context, board);
		this.addView(boardView);
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) boardView.getLayoutParams();
		params.height = (int)(screenHeight * 0.85f);
		boardView.setLayoutParams(params);

		scoreView = new ScoreView(context, score, board);
		this.addView(scoreView);

		this.setOrientation(LinearLayout.VERTICAL);
	}

	public void flashLines(ArrayList<Integer> lines){
		boardView.flashLines(lines);
	}

	public void addFlashingAnimationListener(BoardView.FlashingAnimationListener listener){
		boardView.addFlashingAnimationListener(listener);
	}

	public void redrawEverything(){
		boardView.invalidate();
		scoreView.invalidate();
	}
}

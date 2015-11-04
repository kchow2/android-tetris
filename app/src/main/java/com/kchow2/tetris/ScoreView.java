package com.kchow2.tetris;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by Kevin on 2015-10-15.
 */
public class ScoreView extends View {

	private Paint paint = new Paint();
	private Score score;
	private Board board;
	private Piece previewPiece;
	private Context context;

	public ScoreView(Context context, Score score, Board board) {
		super(context);
		this.context = context;
		setWillNotDraw(false);
		this.score = score;
		this.board = board;
		previewPiece = board.getNextPiece();
	}

	@Override
	public void onDraw(Canvas canvas) {
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.rgb(0xBB, 0xBB, 0xBB));
		canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), paint);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(4);
		paint.setColor(Color.rgb(0x33, 0x33, 0x33));
		canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), paint);

		paint.setColor(Color.BLACK);
		paint.setTextSize(20);
		canvas.drawText("Score: " + score.getScore(), 10, 25, paint);
		canvas.drawText("Lines: " + score.getLines(), 10, 55, paint);
		canvas.drawText("Level: " + score.getLevel(), 10, 85, paint);

		//next piece preview
		int previewWidthPx = 100;
		int previewHeightPx = 100;
		int previewX = this.getWidth() - previewWidthPx - 20;
		int previewY = 25;

		paint.setTextSize(14);

		for(int x = 0; x < Piece.SIZE; x++){
			for(int y = 0; y < Piece.SIZE; y++){

				int xPx = previewX + x * previewWidthPx / Piece.SIZE;
				int yPx = previewY + y * previewHeightPx / Piece.SIZE;
				if(previewPiece.testCollision(x,y)) {
					//System.out.println("xPx=" + xPx + " yPx=" + yPx + " color=" + tileColor + "\n");
					paint.setStyle(Paint.Style.FILL);
					paint.setColor(Color.rgb(previewPiece.color >> 16 & 0xFF, previewPiece.color >> 8 & 0xFF, previewPiece.color & 0xFF));
					canvas.drawRect(xPx, yPx, xPx + previewWidthPx / Piece.SIZE, yPx + previewHeightPx / Piece.SIZE, paint);
				}
				//border
				paint.setStyle(Paint.Style.STROKE);
				paint.setStrokeWidth(2);
				paint.setColor(Color.rgb(0xFF, 0xFF, 0xFF));
				canvas.drawRect(xPx, yPx, xPx+previewWidthPx / Piece.SIZE, yPx + previewHeightPx / Piece.SIZE, paint);
			}
		}
		canvas.drawText("Next Piece", previewX+5, previewY-5, paint);
	}
}

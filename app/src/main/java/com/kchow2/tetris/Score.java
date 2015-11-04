package com.kchow2.tetris;

import java.util.ArrayList;

/**
 * Created by Kevin on 2015-10-15.
 */
public class Score implements Board.OnSavePieceListener, BoardView.FlashingAnimationListener{
	int score, lines, level;

	boolean hasPreviousTetris;

	public Score(){
		clear();
	}

	public void clear(){
		score=lines=level=0;
		hasPreviousTetris = false;
	}

	public int getScore(){
		return score;
	}

	public int getLines(){
		return lines;
	}

	public int getLevel(){
		return level;
	}

	public void addScore(int val){
		score+=val;
	}

	public void addLines(int val){
		lines += val;
		level = lines / 10;
	}

	public void onSavePiece(int pieceType, int pieceRot, int x, int y){
		score+=y*(level+1);
	}
	public void onFullLines(ArrayList<Integer> fullLines, int pieceType, int pieceRot, int x, int y){

	}

	public void onFlashingAnimationStart(ArrayList<Integer> lines){}

	public void onFlashingAnimationFinish(ArrayList<Integer> lines){
		//add the score for full lines when the flashing animation ends
		int numLines = lines.size();
		switch(numLines){
			case 1:
				addScore(100*(level+1));
				hasPreviousTetris = false;
				break;
			case 2:
				addScore(300*(level+1));
				hasPreviousTetris = false;
				break;
			case 3:
				addScore(500*(level+1));
				hasPreviousTetris = false;
				break;
			case 4:
				if(hasPreviousTetris){
					addScore(1200*(level+1));
				}
				else {
					addScore(800*(level+1));
				}
				hasPreviousTetris = true;
				break;
			default:	//wtf? how can we have the number of full lines not be from 1-4?
				throw new RuntimeException("Score.onFlashingAnimationFinish(): lines.size() is out of bounds:"+numLines);
		}
		addLines(numLines);
	}
}

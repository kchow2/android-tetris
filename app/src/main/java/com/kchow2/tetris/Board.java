package com.kchow2.tetris;

import java.util.ArrayList;

/**
 * Created by Kevin on 2015-10-04.
 */
public class Board implements BoardView.FlashingAnimationListener{
    public static final int WIDTH = 10;
    public static final int HEIGHT = 20;

    private int grid[] = new int[WIDTH*HEIGHT];
    private int pieceXPos, pieceYPos;

    private boolean gameOver = false;

    private Piece currentPiece = new Piece(0, 0);
    private Piece nextPiece = new Piece(0, 0);

	private ArrayList<OnSavePieceListener> savePieceListeners = new ArrayList<OnSavePieceListener>();

    public Board(){
        this.pieceXPos = WIDTH/2-2;
        this.pieceYPos = -3;
        this.clear();
    }

    public void clear(){
        for(int y = 0; y < HEIGHT; y++){
            for(int x = 0; x < WIDTH; x++){
                grid[x+y*WIDTH] = 0;
            }
        }

		this.pieceXPos = WIDTH/2-2;
		this.pieceYPos = -3;
        gameOver = false;
    }

    public boolean isGameOver(){
        return this.gameOver;
    }

    public void setTile(int x, int y, int color){
		if(y >= HEIGHT || x < 0 || x >= WIDTH){
			throw new RuntimeException("Board.setTile(): coordinates out of bounds! x="+x+" y="+y);
		}
		grid[x+y*WIDTH] = color;
    }
    public int getTile(int x, int y) {
        //allow reads that look beyond the top of the board. They will simply return 0.
		if(y < 0)
            return 0;
		if(y >= HEIGHT || x < 0 || x >= WIDTH){
			throw new RuntimeException("Board.getTile(): coordinates out of bounds! x="+x+" y="+y);
		}

        return grid[x+y*WIDTH];
    }

    //for rendering. If the tile on the board is occupied, returns the color of that tile.
    //If the current piece is occupying the coordinate, return the color of the current piece.
    public int getTileOrPieceColor(int x, int y) {
        //translate board coordinates to piece coordinates
        int xp = x - pieceXPos;
        int yp = y - pieceYPos;
        if(getTile(x,y) != 0)
            return getTile(x,y);
        else if(xp >= 0 && xp < Piece.SIZE && yp >= 0 && yp < Piece.SIZE && currentPiece.testCollision(xp, yp)) {
            return currentPiece.getColor();
        }
        return 0;
    }

    public synchronized boolean moveLeft() {
        pieceXPos--;
        if(testCollision()){
            pieceXPos++;
            return false;
        }
        return true;
    }

    public synchronized boolean moveRight() {
        pieceXPos++;
        if(testCollision()){
            pieceXPos--;
            return false;
        }
        return true;
    }

    public synchronized boolean moveDown() {
        pieceYPos++;
        if(testCollision()){
            pieceYPos--;
            savePiece();
            return false;
        }
        return true;
    }

    public synchronized boolean rotate() {
        currentPiece.rotate();
        if(testCollision()){
            currentPiece.rotateBack();
        }
        return true;
    }

    //returns true if the current piece at its current coordinates collides with either the edges of the board
    //or occupied tiles on the board.
    private boolean testCollision(){
        for(int y = 0; y < Piece.SIZE; y++){
            for(int x = 0; x < Piece.SIZE; x++){
                if(currentPiece.testCollision(x,y)){
                    if(pieceXPos+x < 0 || pieceXPos+x >= WIDTH)     //collision with either left or right of board
                        return true;
                    if(pieceYPos+y >= HEIGHT)   //collision with bottom of the board
                        return true;
                    if(getTile(pieceXPos+x, pieceYPos+y) != 0){
                        //There is a collision between the piece and an occupied tile
                        return true;
                    }
                }
            }
        }

        return false;
    }

    //saves the current piece at the current position
    private synchronized void savePiece(){
        for(int y = 0; y < Piece.SIZE; y++) {
            for (int x = 0; x < Piece.SIZE; x++) {
                if(currentPiece.testCollision(x, y)) {
                    if(pieceYPos + y < 0){  //the game has reached a point where the stack has reached the top of the board and the piece can't be fully saved anymore.
                        setGameOver();
						break;
                    }
                    setTile(pieceXPos + x, pieceYPos + y, currentPiece.getColor());
                }
            }
        }
		for(OnSavePieceListener listener : savePieceListeners){
			listener.onSavePiece(currentPiece.type, currentPiece.rotation, pieceXPos, pieceYPos);
		}

		//check for full lines
		int numFullLines = 0;
		ArrayList<Integer> fullLines = new ArrayList<Integer>();
		for(int y = 0; y < HEIGHT; y++){
			if(isLineFull(y)){
				//deleteLine(y);	//we need to wait until after the animation finishes to delete the lines
				numFullLines++;
				fullLines.add(y);
			}
		}
		if(numFullLines > 0) {
			//call the full line listeners whenever there is a full line.
			//If a single piece causes multiple full lines, only a single message is sent.
			for (OnSavePieceListener listener : savePieceListeners) {
				listener.onFullLines(fullLines, currentPiece.type, currentPiece.rotation, pieceXPos, pieceYPos);
			}

			//we will create a new piece when the flashing animation finishes playing
		}
		else{
			//next piece becomes current piece
			//and next piece becomes a new random piece
			currentPiece.copyFrom(nextPiece);
			nextPiece.randomizeTypeAndRotation();
			pieceXPos = WIDTH/2-2;
			pieceYPos = calculateStartHeight(currentPiece);

			//if the new piece we just created collides with something, then game over
			if(testCollision())
				setGameOver();
		}




    }

	public synchronized boolean isLineFull(int y){
		for(int x = 0; x < WIDTH; x++){
			if(getTile(x,y) == 0){
				return false;
			}
		}
		return true;
	}

	public synchronized void deleteLine(int y){
		//To delete the line, all lines at height < y must be shifted up one
		for(int h = y; h >= 0; h--){
			for(int x = 0; x < WIDTH; x++){
				setTile(x, h, getTile(x, h-1));
			}
		}
	}

	//calculates the height the piece should be spawned in at.
	//want the piece to be spawned in just out of sight such that the piece will become visible on the next tick when moved down.
    private int calculateStartHeight(Piece piece){
		for(int y = Piece.SIZE-1; y >= 0; y--){
			for(int x = 0; x < Piece.SIZE; x++){
				if(piece.testCollision(x,y)){
					return -(y+1);
				}
			}
		}
		return -3;
	}

    private void setGameOver(){
        this.gameOver = true;
    }

	public Piece getCurrentPiece(){
		return this.currentPiece;
	}

	public Piece getNextPiece(){
		return this.nextPiece;
	}

	public void onFlashingAnimationStart(ArrayList<Integer> lines){}
	public void onFlashingAnimationFinish(ArrayList<Integer> lines){
		//next piece becomes current piece
		//and next piece becomes a new random piece
		currentPiece.copyFrom(nextPiece);
		nextPiece.randomizeTypeAndRotation();
		pieceXPos = WIDTH/2-2;
		pieceYPos = calculateStartHeight(currentPiece);

		//if the new piece we just created collides with something, then game over
		if(testCollision())
			setGameOver();
	}


	public interface OnSavePieceListener{
		public void onSavePiece(int pieceType, int pieceRot, int x, int y);
		public void onFullLines(ArrayList<Integer> fullLines, int pieceType, int pieceRot, int x, int y);
	}

	public void addSavePieceListener(OnSavePieceListener listener) {
		this.savePieceListeners.add(listener);
	}
}

package com.kchow2.tetris;

import java.util.Random;

/**
 * Created by Kevin on 2015-10-04.
 * A tetris piece
 */
public class Piece {
    public static final int SIZE = 5;   //uses a 5x5 grid to represent the pieces
    public static final int NUM_PIECES = 7;
    int type;
    int rotation;
    int color;

    Random random = new Random();

    public Piece(int type, int rotation){
        this.setType(type, rotation);
    }

    public void randomizeTypeAndRotation(){
        int type = random.nextInt(NUM_PIECES);
        int rotation = random.nextInt(4);
        this.setType(type, rotation);
    }

    public int getColor(){
        return this.color;
    }

    public void setType(int type, int rotation){
        if(type < 0 || type >= NUM_PIECES || rotation < 0 || rotation >= 4)
            throw new RuntimeException("Piece.setType(): invalid parameters: type="+type+" rotation="+rotation);
        this.type = type;
        this.rotation = rotation;
        this.color = pieceColors[type];
    }

    public boolean testCollision(int x, int y){
        if(x < 0 || x >= SIZE || y < 0 || y >= SIZE)
            throw new RuntimeException("Piece.testCollision(): coordinates out of bounds: x="+x+" y="+y);
        return pieceData[type][rotation][y][x] != 0;
    }

    public void rotate(){
        this.rotation = (this.rotation + 1) % 4;
    }

    public void rotateBack(){
        //adding 3 to the rotation is the same as subtracting 1 (mod 4)
        //This avoids negative values for rotation
        this.rotation = (this.rotation + 3) % 4;
    }

    public void copyFrom(Piece p){
        this.setType(p.type, p.rotation);
    }


    //PIECE DATA/////////////
    //[type][rotation][y][x]
    private static int pieceData[][][][] = new int[][][][] {
            //I-PIECE
            {
                    {
                            { 0,0,0,0,0 },
                            { 0,0,1,0,0 },
                            { 0,0,1,0,0 },
                            { 0,0,1,0,0 },
                            { 0,0,1,0,0 },
                    },
                    {
                            { 0,0,0,0,0 },
                            { 0,0,0,0,0 },
                            { 1,1,1,1,0 },
                            { 0,0,0,0,0 },
                            { 0,0,0,0,0 },
                    },
                    {
                            { 0,0,0,0,0 },
                            { 0,0,1,0,0 },
                            { 0,0,1,0,0 },
                            { 0,0,1,0,0 },
                            { 0,0,1,0,0 },
                    },
                    {
                            { 0,0,0,0,0 },
                            { 0,0,0,0,0 },
                            { 1,1,1,1,0 },
                            { 0,0,0,0,0 },
                            { 0,0,0,0,0 },
                    },
            },
            //J-PIECE
            {
                    {
                            { 0,0,0,0,0 },
                            { 0,0,1,0,0 },
                            { 0,0,1,0,0 },
                            { 0,1,1,0,0 },
                            { 0,0,0,0,0 },
                    },
                    {
                            { 0,0,0,0,0 },
                            { 0,1,0,0,0 },
                            { 0,1,1,1,0 },
                            { 0,0,0,0,0 },
                            { 0,0,0,0,0 },
                    },
                    {
                            { 0,0,0,0,0 },
                            { 0,0,1,1,0 },
                            { 0,0,1,0,0 },
                            { 0,0,1,0,0 },
                            { 0,0,0,0,0 },
                    },
                    {
                            { 0,0,0,0,0 },
                            { 0,0,0,0,0 },
                            { 0,1,1,1,0 },
                            { 0,0,0,1,0 },
                            { 0,0,0,0,0 },
                    },
            },
            //L-PIECE
            {
                    {
                            { 0,0,0,0,0 },
                            { 0,0,1,0,0 },
                            { 0,0,1,0,0 },
                            { 0,0,1,1,0 },
                            { 0,0,0,0,0 },
                    },
                    {
                            { 0,0,0,0,0 },
                            { 0,0,0,0,0 },
                            { 0,1,1,1,0 },
                            { 0,1,0,0,0 },
                            { 0,0,0,0,0 },
                    },
                    {
                            { 0,0,0,0,0 },
                            { 0,1,1,0,0 },
                            { 0,0,1,0,0 },
                            { 0,0,1,0,0 },
                            { 0,0,0,0,0 },
                    },
                    {
                            { 0,0,0,0,0 },
                            { 0,0,0,1,0 },
                            { 0,1,1,1,0 },
                            { 0,0,0,0,0 },
                            { 0,0,0,0,0 },
                    },
            },
            //S-PIECE
            {
                    {
                            { 0,0,0,0,0 },
                            { 0,0,1,1,0 },
                            { 0,1,1,0,0 },
                            { 0,0,0,0,0 },
                            { 0,0,0,0,0 },
                    },
                    {
                            { 0,0,0,0,0 },
                            { 0,0,1,0,0 },
                            { 0,0,1,1,0 },
                            { 0,0,0,1,0 },
                            { 0,0,0,0,0 },
                    },
                    {
                            { 0,0,0,0,0 },
                            { 0,0,1,1,0 },
                            { 0,1,1,0,0 },
                            { 0,0,0,0,0 },
                            { 0,0,0,0,0 },
                    },
                    {
                            { 0,0,0,0,0 },
                            { 0,0,1,0,0 },
                            { 0,0,1,1,0 },
                            { 0,0,0,1,0 },
                            { 0,0,0,0,0 },
                    },
            },
            //Z-PIECE
            {
                    {
                            { 0,0,0,0,0 },
                            { 0,1,1,0,0 },
                            { 0,0,1,1,0 },
                            { 0,0,0,0,0 },
                            { 0,0,0,0,0 },
                    },
                    {
                            { 0,0,0,0,0 },
                            { 0,0,0,1,0 },
                            { 0,0,1,1,0 },
                            { 0,0,1,0,0 },
                            { 0,0,0,0,0 },
                    },
                    {
                            { 0,0,0,0,0 },
                            { 0,1,1,0,0 },
                            { 0,0,1,1,0 },
                            { 0,0,0,0,0 },
                            { 0,0,0,0,0 },
                    },
                    {
                            { 0,0,0,0,0 },
                            { 0,0,0,1,0 },
                            { 0,0,1,1,0 },
                            { 0,0,1,0,0 },
                            { 0,0,0,0,0 },
                    },
            },
            //O-PIECE
            {
                    {
                            { 0,0,0,0,0 },
                            { 0,1,1,0,0 },
                            { 0,1,1,0,0 },
                            { 0,0,0,0,0 },
                            { 0,0,0,0,0 },
                    },
                    {
                            { 0,0,0,0,0 },
                            { 0,1,1,0,0 },
                            { 0,1,1,0,0 },
                            { 0,0,0,0,0 },
                            { 0,0,0,0,0 },
                    },
                    {
                            { 0,0,0,0,0 },
                            { 0,1,1,0,0 },
                            { 0,1,1,0,0 },
                            { 0,0,0,0,0 },
                            { 0,0,0,0,0 },
                    },
                    {
                            { 0,0,0,0,0 },
                            { 0,1,1,0,0 },
                            { 0,1,1,0,0 },
                            { 0,0,0,0,0 },
                            { 0,0,0,0,0 },
                    },
            },
            //T-PIECE
            {
                    {
                            { 0,0,0,0,0 },
                            { 0,0,1,0,0 },
                            { 0,1,1,1,0 },
                            { 0,0,0,0,0 },
                            { 0,0,0,0,0 },
                    },
                    {
                            { 0,0,0,0,0 },
                            { 0,0,1,0,0 },
                            { 0,0,1,1,0 },
                            { 0,0,1,0,0 },
                            { 0,0,0,0,0 },
                    },
                    {
                            { 0,0,0,0,0 },
                            { 0,0,0,0,0 },
                            { 0,1,1,1,0 },
                            { 0,0,1,0,0 },
                            { 0,0,0,0,0 },
                    },
                    {
                            { 0,0,0,0,0 },
                            { 0,0,1,0,0 },
                            { 0,1,1,0,0 },
                            { 0,0,1,0,0 },
                            { 0,0,0,0,0 },
                    },
            },

    };

    private static int pieceColors[] = new int[]{
            0xFFFF00,   //I-PIECE   -   yellow
            0x00FF00,   //J-PIECE   -   green
            0xFF0000,   //L-PIECE   -   red
            0x0000FF,   //S-PIECE   -   blue
            0x880088,   //Z-PIECE   -   purple
            0x441122,   //O-PIECE   -   brown
			0x008888,   //T-PIECE   -   cyan
    };

}

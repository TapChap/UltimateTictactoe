package com.example.ultimatetictactoe.Tictactoe;

import com.example.ultimatetictactoe.R;

public enum Piece {
    X('X'), O('O'), EMPTY('-');

    private char piece;

    Piece(char piece) {
        this.piece = piece;
    }

    public char getPiece() {
        return piece;
    }

    public int getImg(){
        switch(this.piece){
            case 'X':
                return R.drawable.x;
            case 'O':
                return R.drawable.o;
            default:
                return R.drawable.empty;
        }
    }

    public boolean equals(Piece other){
        return this.piece == other.piece;
    }
}
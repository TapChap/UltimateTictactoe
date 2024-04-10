package com.example.ultimatetictactoe20.Tictactoe;

import com.example.ultimatetictactoe20.R;

public enum Piece {
    X('X'), O('O'), EMPTY('_');

    private char piece;

    Piece(char piece) {
        this.piece = piece;
    }

    public char getChar() {
        return piece;
    }

    public static Piece getPiece(char chr){
        if (chr == 'X') return Piece.X;
        if (chr == 'O') return Piece.O;
        return Piece.EMPTY;
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
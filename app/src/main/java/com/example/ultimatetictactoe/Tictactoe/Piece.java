package com.example.ultimatetictactoe.Tictactoe;

public enum Piece {
    X('X'), O('O'), EMPTY('-');

    private char piece;

    Piece(char piece) {
        this.piece = piece;
    }

    public char getPiece() {
        return piece;
    }

    public boolean equals(Piece other){
        return this.piece == other.piece;
    }
}
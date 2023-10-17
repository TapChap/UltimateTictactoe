package com.example.ultimatetictactoe.Tictactoe;

import android.util.Log;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Board {
    private Piece[][] board = new Piece[3][3];
    private boolean hasWon = false;
    private Piece winner = Piece.EMPTY;

    private final String TAG = "Board";

    public Board(){
        foreach((i, j) -> board[i][j] = Piece.EMPTY);

        Log.d(TAG, getBoard());
    }

    public Piece get(int i, int j){
        return board[i][j];
    }

    public void set(int i, int j, Piece piece){
        board[i][j] = piece;
    }

    public boolean hasWon(){
        return hasWon;
    }

    public Piece getWinner() {
        return winner;
    }

    private String getBoard(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < board.length; i++) {
            int finalI = i; // affectedly final variable inside lambda
            foreach(j -> sb.append(get(finalI, j).getPiece()).append(" "));
            sb.append("\n");
        }

        return sb.toString();
    }


    // foreach methods
    /**
     * supplies all the indexes in the board
     * @param consumer the consumer the accepts the indexes
     */
    private void foreach(BiConsumer<Integer, Integer> consumer){
        for (int i = 0; i < board.length; i++) {
            int finalI = i; // affectedly final variable inside lambda
            foreach(j -> consumer.accept(finalI, j));
        }
    }

    /**
     * supplies all the indexes in a single line of the board
     * @param consumer the consumer the accepts the indexes
     */
    private void foreach(Consumer<Integer> consumer){
        for (int i = 0; i < board[0].length; i++) {
            consumer.accept(i);
        }
    }
}

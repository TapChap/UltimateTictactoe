package com.example.ultimatetictactoe.Tictactoe;

import android.util.Log;
import android.widget.ImageView;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Board extends TictactoeManager {
    private ImageView[][] board = new ImageView[3][3];
    private Piece winnerPiece = Piece.EMPTY;

    private final String TAG = "Board";

    public Board(ImageView[][] imageViews) {
        foreach((i, j) -> board[i][j] = imageViews[i][j]);
    }

    public Board(){}

    @Override
    void update(){
        Log.d(TAG, "updated");
        foreach((i, j) -> {
            Piece piece = get(i, j);
            board[i][j].setImageResource(piece.getImg());
        });
    }

    public void setImage(int i, int j, ImageView image){
        this.board[i][j] = image;
    }

    // foreach methods
    /**
     * supplies all the indexes in the board
     *
     * @param consumer the consumer the accepts the indexes
     */
    private void foreach(BiConsumer<Integer, Integer> consumer) {
        for (int i = 0; i < board.length; i++) {
            int finalI = i; // affectedly final variable inside lambda
            foreach(j -> consumer.accept(finalI, j));
        }
    }

    /**
     * supplies all the indexes in a single line of the board
     *
     * @param consumer the consumer the accepts the indexes
     */
    private void foreach(Consumer<Integer> consumer) {
        for (int i = 0; i < board[0].length; i++) {
            consumer.accept(i);
        }
    }
}

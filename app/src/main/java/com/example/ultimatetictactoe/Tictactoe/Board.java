package com.example.ultimatetictactoe.Tictactoe;

import android.widget.ImageView;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Board extends TictactoeManager {
    private ImageView[][] board = new ImageView[3][3];
    private int i, j;

    private final String TAG = "Board";

    public Board(ImageView[][] imageViews, Pose2d pose) {
        foreach((i, j) -> board[i][j] = imageViews[i][j]);
        this.i = pose.i;
        this.j = pose.j;
    }

    public Board(){}

    public void update(){
        foreach((i, j) -> {
            Piece piece = get(new Pose2d(i, j));
            board[i][j].setImageResource(piece.getImg());
        });
    }

    public void setImage(Pose2d pose, ImageView image){
        this.board[pose.i][pose.j] = image;
    }

    public int row(){
        return i;
    }

    public int col(){
        return j;
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

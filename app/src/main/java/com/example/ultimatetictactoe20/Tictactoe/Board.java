package com.example.ultimatetictactoe20.Tictactoe;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.function.BiConsumer;

public class Board extends TictactoeManager {
    private ImageView[][] board = new ImageView[3][3];

    private ConstraintLayout boardLayout;
    private Pose2d pose;

    private final String TAG = "Board";

    public Board(ImageView[][] imageViews, Pose2d pose) {
        foreach((i, j) -> board[i][j] = imageViews[i][j]);
        this.pose = pose;
    }
    public void addLayout(ConstraintLayout layout){
        this.boardLayout = layout;
    }

    public Board(){}

    public void update(){
        foreach((i, j) -> {
            Piece piece = get(new Pose2d(i, j));
            board[i][j].setImageResource(piece.getImg());
            if (!piece.equals(Piece.EMPTY)) board[i][j].setTag("image");
            if (this.hasWon()) boardLayout.setVisibility(View.INVISIBLE);
        });
    }

    public void setImage(Pose2d pose, ImageView image){
        this.board[pose.i][pose.j] = image;
    }

    public Pose2d getPose(){
        return pose;
    }

    public ImageView getBoardImage(Pose2d imgPose){
        return board[imgPose.i][imgPose.j];
    }

    /**
     * supplies all the indexes in the board
     *
     * @param consumer the consumer the accepts the indexes
     */
    private void foreach(BiConsumer<Integer, Integer> consumer) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                consumer.accept(i, j);
            }
        }
    }
}

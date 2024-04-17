package com.example.ultimatetictactoe20.Tictactoe;

import android.view.View;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.function.BiConsumer;

/**
 * Represents a board for the Tic-Tac-Toe game.
 */
public class Board extends TictactoeManager {

    /**
     * The 2D array of ImageViews representing the board.
     */
    private ImageView[][] board;

    /**
     * The layout containing the board.
     */
    private ConstraintLayout boardLayout;

    /**
     * The pose of the board.
     */
    private Pose2d pose;

    /**
     * Creates a new Board object.
     *
     * @param imageViews the 2D array of ImageViews representing the board
     * @param pose       the pose of the board
     */
    public Board(ImageView[][] imageViews, Pose2d pose) {
        this.board = imageViews;
        this.pose = pose;
    }

    /**
     * Creates a new Board object with no initial values.
     */
    public Board() {
    }

    /**
     * Updates the board with the current game state.
     */
    public void update() {
        // Iterate over the board and update the ImageViews accordingly
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                Piece piece = get(new Pose2d(i, j));
                board[i][j].setImageResource(piece.getImg());
                if (!piece.equals(Piece.EMPTY)) {
                    board[i][j].setTag("image");
                }
                if (this.hasWon()) {
                    setBoardVisibility(false);
                }
            }
        }
    }

    public void addLayout(ConstraintLayout layout){
        this.boardLayout = layout;
    }

    /**
     * Sets the visibility of the board.
     *
     * @param visible true to make the board visible, false to make it invisible
     */
    public void setBoardVisibility(boolean visible) {
        boardLayout.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * Sets the ImageView at the given pose.
     *
     * @param pose  the pose of the ImageView
     * @param image the ImageView to set
     */
    public void setImage(Pose2d pose, ImageView image) {
        this.board[pose.i][pose.j] = image;
    }

    /**
     * Gets the pose of the board.
     *
     * @return the pose of the board
     */
    public Pose2d getPose() {
        return pose;
    }

    /**
     * Gets the ImageView at the given pose.
     *
     * @param imgPose the pose of the ImageView
     * @return the ImageView at the given pose
     */
    public ImageView getBoardImage(Pose2d imgPose) {
        return board[imgPose.i][imgPose.j];
    }

    /**
     * Supplies all the indexes in the board to the given consumer.
     *
     * @param consumer the consumer that accepts the indexes
     */
    private void foreach(BiConsumer<Integer, Integer> consumer) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                consumer.accept(i, j);
            }
        }
    }
}
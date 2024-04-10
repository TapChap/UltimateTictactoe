package com.example.ultimatetictactoe20.Tictactoe;

import android.util.Log;

import com.example.ultimatetictactoe20.Tictactoe.Piece;
import com.example.ultimatetictactoe20.Tictactoe.Pose2d;

public class TictactoeManager {

    private Piece[][] board = new Piece[3][3];

    private boolean isX = true;
    private Piece winner = Piece.EMPTY;

    public TictactoeManager() {
        reset();
    }

    public void reset() {
        isX = true;

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j] = Piece.EMPTY;
            }
        }
    }

    public void setPiece(Pose2d pose) {
        setPiece(pose, isX ? Piece.X : Piece.O);
    }

    public void setPiece(Pose2d pose, Piece piece) {
        this.board[pose.i][pose.j] = piece;
    }

    public void loadBoard(String boardStr){
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Pose2d pose = new Pose2d(i, j);
                setPiece(pose, Piece.getPiece(boardStr.charAt(pose.getPoseIndex())));
            }
        }
    }

    public Piece get(Pose2d pose) {
        return board[pose.i][pose.j];
    }

    public Piece getTurn() {
        return isX ? Piece.X : Piece.O;
    }

    public void next() {
        isX = !isX;
    }

    public boolean hasWon(Piece turn) {
        if (checkCol(0) || checkCol(1) || checkCol(2) ||
            checkRow(0) || checkRow(1) || checkRow(2) ||
            checkDig(true) || checkDig(false)) {
            if (winner == Piece.EMPTY) winner = turn;
            return true;
        }
        return false;
    }

    public boolean hasWon(){
        return hasWon(getTurn());
    }

    public Piece getWinner(){
        return winner;
    }

//    void update() {}

    private boolean checkRow(int row) {
        Piece p = board[0][row];

        for (int i = 0; i < 3; i++) {
            if (board[i][row] != p || board[i][row] == Piece.EMPTY)
                return false;
        }
        return true;
    }

    private boolean checkCol(int col) {
        Piece p = board[col][0];

        for (int i = 0; i < board.length; i++) {
            if (board[col][i] != p || board[col][i] == Piece.EMPTY)
                return false;
        }
        return true;
    }

    private boolean checkDig(boolean n) {
        int k = 0;
        if (n) k = 2;

        Piece c = board[0][k];

        for (int i = 0; i < board.length; i++) {
            if (board[i][k] != c || board[i][k] == Piece.EMPTY)
                return false;
            if (n) k--;
            else k++;
        }
        return true;
    }

    public boolean isTie() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == Piece.EMPTY) return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                string.append(board[i][j].getChar());
            }
        }

        Log.d("BoardTostring", "toString: " + string);

        return string.toString();
    }
}


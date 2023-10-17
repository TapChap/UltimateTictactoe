package com.example.ultimatetictactoe.Tictactoe;

public class TictactoeManager {

    private char[][] board = new char[3][3];

    private boolean isX = true;

    public TictactoeManager(){
        restart();
    }

    public void restart(){
        isX = true;

        for (int i = 0; i < board.length; i++){
            for (int j = 0; j < board[i].length; j++) {
                board[i][j] = ' ';
            }
        }
    }

    public String updateBoard(int i, int j){
        this.board[i][j] = isX? 'x' : 'o';

        return String.valueOf(board[i][j]);
    }

    public void nextTurn(){
        isX = !isX;
    }

    public String getTurn(){
        return isX? "X" : "O";
    }

    public boolean hasWon() {
        return checkCol(0) || checkCol(1) || checkCol(2) ||
                checkRow(0) || checkRow(1) || checkRow(2) ||
                checkDig(true) || checkDig(false);
    }

    public boolean checkRow(int row) {
        char c = board[0][row];

        for (int i = 0; i < 3; i++) {
            if (board[i][row] != c || board[i][row] == ' ')
                return false;
        }
        return true;
    }

    public boolean checkCol(int col) {
        char c = board[col][0];

        for (int i = 0; i < board.length; i++) {
            if (board[col][i] != c || board[col][i] == ' ')
                return false;
        }
        return true;
    }

    public boolean boardFull(){
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == ' ') return false;
            }
        }
        return true;
    }

    public boolean checkDig(boolean n) {
        int k = 0;
        if (n) k = 2;

        char c = board[0][k];

        for (int i = 0; i < board.length; i++) {
            if (board[i][k] != c || board[i][k] == ' ')
                return false;
            if (n) k --;
            else k ++;
        }
        return true;
    }
}


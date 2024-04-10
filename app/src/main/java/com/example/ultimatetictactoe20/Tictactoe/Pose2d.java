package com.example.ultimatetictactoe20.Tictactoe;

import androidx.annotation.NonNull;

public class Pose2d {
    public int i, j; // row, col

    public Pose2d(int i, int j) {
        this.i = i % 3;
        this.j = j;
    }

    public int getPoseIndex(){
        return i * 3 + j;
    }

    @NonNull
    @Override
    public String toString() {
        return "i: " + i + ", j: " + j;
    }
}

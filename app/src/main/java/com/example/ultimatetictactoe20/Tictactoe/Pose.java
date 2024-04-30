package com.example.ultimatetictactoe20.Tictactoe;

import androidx.annotation.NonNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Pose {
    public int i, j; // row, col

    public Pose(int i, int j) { // creates a two dimensional point in the given position
        this.i = i % 3;
        this.j = j;
    }

    public Pose(int index){ // creates a two dimensional pose when given a one dimensional index
        this(index / 3, index % 3);
    }

    public int getPoseIndex(){ // converts a two dimensional pose to a one dimensional
        return i * 3 + j;
    }

    public static void forEach(BiConsumer<Integer, Integer> index){
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                index.accept(i, j);
            }
        }
    }

    public static void forEach(Consumer<Pose> pose){
        forEach((i, j)-> pose.accept(new Pose(i, j)));
    }

    @NonNull
    @Override
    public String toString() {
        return "i: " + i + ", j: " + j;
    }
}

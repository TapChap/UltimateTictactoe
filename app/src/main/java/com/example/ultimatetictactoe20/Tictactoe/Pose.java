package com.example.ultimatetictactoe20.Tictactoe;

import androidx.annotation.NonNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Pose {
    public int i, j; // row, col

    // creates a two dimensional point in the given position
    public Pose(int i, int j) {
        this.i = i;
        this.j = j;
    }

    // creates a two dimensional pose from an index (left=top)
    public Pose(int index){ // creates a two dimensional pose when given a one dimensional index
        this(index / 3, index % 3);
    }

    // returns this pose as a one dimensional index
    public int getPoseIndex(){ // converts a two dimensional pose to a one dimensional
        return i * 3 + j;
    }

    // supplies i, j values to all poses in a 3X3 board
    public static void forEach(BiConsumer<Integer, Integer> index){
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                index.accept(i, j);
            }
        }
    }

    // supplies Pose values to all poses in a 3X3 board
    public static void forEach(Consumer<Pose> pose){
        forEach((i, j)-> pose.accept(new Pose(i, j)));
    }

    @NonNull
    @Override
    public String toString() {
        return "i: " + i + ", j: " + j;
    }
}

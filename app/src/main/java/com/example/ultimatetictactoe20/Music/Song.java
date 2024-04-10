package com.example.ultimatetictactoe20.Music;

public class Song {
    private long ID;
    private String title;

    public Song(long id, String title) {
        this.ID = id;
        this.title = title;
    }

    public long getId() {
        return this.ID;
    }

    public String getTitle() {
        return this.title;
    }

    public String toSring() {
        return (this.ID + ", " + this.title);
    }
}
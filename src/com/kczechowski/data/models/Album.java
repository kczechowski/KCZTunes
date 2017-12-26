package com.kczechowski.data.models;

import java.util.ArrayList;
import java.util.List;

public class Album {
    private String albumName;
    private List<Song> songs;

    public Album() {
        songs = new ArrayList<Song>();
    }

    @Override
    public String toString() {
        return "Album{" +
                "albumName='" + albumName + '\'' +
                '}';
    }
}

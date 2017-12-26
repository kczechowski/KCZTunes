package com.kczechowski.data.models;

import java.util.ArrayList;
import java.util.List;

public class Artist {
    private String artistName;
    private List<Album> albums;

    public Artist() {
        albums = new ArrayList<Album>();
    }

    @Override
    public String toString() {
        return "Artist{" +
                "artistName='" + artistName + '\'' +
                '}';
    }
}

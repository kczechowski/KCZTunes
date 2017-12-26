package com.kczechowski.data.models;

import java.util.ArrayList;
import java.util.List;

public class ArtistsWrapper {
    private List<Artist> artists;

    public ArtistsWrapper() {
        artists = new ArrayList<Artist>();
    }

    public List<Artist> getArtists() {
        return artists;
    }
}

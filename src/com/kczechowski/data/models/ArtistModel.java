package com.kczechowski.data.models;

public class ArtistModel {
    private String artistID;
    private String artistName;

    public String getArtistID() {
        return artistID;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public void setArtistID(String artistID) {

        this.artistID = artistID;
    }

    @Override
    public String toString() {
        return "ArtistModel{" +
                "artistID='" + artistID + '\'' +
                ", artistName='" + artistName + '\'' +
                '}';
    }
}

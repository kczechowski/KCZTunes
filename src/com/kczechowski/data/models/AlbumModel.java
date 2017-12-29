package com.kczechowski.data.models;

public class AlbumModel {
    private String albumID;
    private String albumName;

    public void setAlbumID(String albumID) {
        this.albumID = albumID;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getAlbumID() {

        return albumID;
    }

    @Override
    public String toString() {
        return "AlbumModel{" +
                "albumID='" + albumID + '\'' +
                ", albumName='" + albumName + '\'' +
                '}';
    }
}

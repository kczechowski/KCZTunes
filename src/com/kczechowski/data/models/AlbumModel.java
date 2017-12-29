package com.kczechowski.data.models;

public class AlbumModel {
    private String albumID;
    private String albumName;
    private byte[] albumImage;

    public String getAlbumID() {
        return albumID;
    }

    public void setAlbumID(String albumID) {
        this.albumID = albumID;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public byte[] getAlbumImage() {
        return albumImage;
    }

    public void setAlbumImage(byte[] albumImage) {
        this.albumImage = albumImage;
    }

    @Override
    public String toString() {
        return "AlbumModel{" +
                "albumID='" + albumID + '\'' +
                ", albumName='" + albumName + '\'' +
                '}';
    }
}

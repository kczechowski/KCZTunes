package com.kczechowski.data.models;

public class SongModel {
    private String songID;
    private String songName;
    private String songPath;

    public String getSongID() {
        return songID;
    }

    public String getSongName() {
        return songName;
    }

    public String getSongPath() {
        return songPath;
    }

    public void setSongID(String songID) {
        this.songID = songID;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public void setSongPath(String songPath) {
        this.songPath = songPath;
    }

    @Override
    public String toString() {
        return "SongModel{" +
                "songID='" + songID + '\'' +
                ", songName='" + songName + '\'' +
                ", songPath='" + songPath + '\'' +
                '}';
    }
}

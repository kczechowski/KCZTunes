package com.kczechowski.data.models;

public class SongModel {
    private String songID;
    private String songName;
    private String songPath;
    private String songTrack;
    private String songDuration;

    public String getSongID() {
        return songID;
    }

    public void setSongID(String songID) {
        this.songID = songID;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongPath() {
        return songPath;
    }

    public void setSongPath(String songPath) {
        this.songPath = songPath;
    }

    public String getSongTrack() {
        return songTrack;
    }

    public void setSongTrack(String songTrack) {
        this.songTrack = songTrack;
    }

    public String getSongDuration() {
        return songDuration;
    }

    public void setSongDuration(String songDuration) {
        this.songDuration = songDuration;
    }

    @Override
    public String toString() {
        return "SongModel{" +
                "songID='" + songID + '\'' +
                ", songName='" + songName + '\'' +
                ", songPath='" + songPath + '\'' +
                ", songTrack='" + songTrack + '\'' +
                ", songDuration='" + songDuration + '\'' +
                '}';
    }
}

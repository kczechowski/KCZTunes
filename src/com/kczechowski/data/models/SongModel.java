package com.kczechowski.data.models;

public class SongModel {
    private String songID;
    private String songName;
    private String songPath;
    private String songTrack;
    private String songDuration;

    private ArtistModel artist;
    private AlbumModel album;

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

    public ArtistModel getArtist() {
        return artist;
    }

    public void setArtist(ArtistModel artist) {
        this.artist = artist;
    }

    public AlbumModel getAlbum() {
        return album;
    }

    public void setAlbum(AlbumModel album) {
        this.album = album;
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

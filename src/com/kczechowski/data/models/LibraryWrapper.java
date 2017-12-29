package com.kczechowski.data.models;

import java.util.List;

public class LibraryWrapper {
    private List<ArtistModel> artists;
    private List<AlbumModel> albums;
    private List<SongModel> songs;

    public List<ArtistModel> getArtists() {
        return artists;
    }

    public List<AlbumModel> getAlbums() {
        return albums;
    }

    public List<SongModel> getSongs() {
        return songs;
    }

    public void setArtists(List<ArtistModel> artists) {
        this.artists = artists;
    }

    public void setAlbums(List<AlbumModel> albums) {
        this.albums = albums;
    }

    public void setSongs(List<SongModel> songs) {
        this.songs = songs;
    }
}

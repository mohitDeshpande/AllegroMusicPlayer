package com.allegromusicplayer.com.allegromusicplayer.classes;

/**
 * Created by Mohit Deshpande on 11/22/15.
 */
public class Song {
    private long id;
    private String title;
    private String artist;
    private String album;
    private String albumArt;

    @Override
    public String toString() {
        return "Song{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                '}';
    }

    public Song(long id, String albumArt, String artist, String title, String album) {
        this.id = id;
        this.albumArt = albumArt;
        this.artist = artist;
        this.title = title;
        this.album = album;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getAlbumArt() {
        return albumArt;
    }
}

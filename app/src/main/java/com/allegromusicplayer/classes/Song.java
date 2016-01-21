package com.allegromusicplayer.classes;

import android.content.ContentUris;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.FileDescriptor;

/**
 * Created by Mohit Deshpande on 11/22/15.
 */
public class Song {
    private long id;
    private String title;
    private String artist;
    private String album;
    //private Bitmap albumArt;
    private long albumID;

    @Override
    public String toString() {
        return "Song{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                '}';
    }

    public long getAlbumID() {
        return albumID;
    }

    public Song(long id, String title, String artist, String album,long albumID) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.albumID = albumID;
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
}

package com.allegromusicplayer.classes;

import android.content.ContentUris;
import android.net.Uri;
import android.util.Log;

import java.io.Serializable;

/**
 * Created by Mohit Deshpande on 11/22/15.
 */
public class Song implements Serializable {
    private long id;
    private String title;
    private String artist;
    private String album;
    private long albumID;
    private String path;

    @Override
    public String toString() {
        return "Song{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", albumID=" + albumID +
                ", path=" + path +
                '}';
    }

    public Song(long id, String title, String artist, String album, long albumID, String path) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.albumID = albumID;
        this.path = path;
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

    public long getAlbumID() {
        return albumID;
    }

    public String getPath() {
        return path;
    }

    /**
     * Gets album art work URI for the given album id
     *
     * @return A Uri of the album artwork bitmap
     */
    public Uri getAlbumArtUri() {
        Uri uri = null;
        try {
            final Uri sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart");

            uri = ContentUris.withAppendedId(sArtworkUri, albumID);


        } catch (Exception e) {
            Log.e("AMP stackTrace", e.toString());
        }
        return uri;
    }
}

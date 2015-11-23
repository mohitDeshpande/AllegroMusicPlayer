package com.allegromusicplayer.com.allegromusicplayer.classes;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.allegromusicplayer.R;

import java.io.FileDescriptor;
import java.util.List;

/**
 * use an Adapter to map the songs to the list view.
 * pass the song list from the main Activity class and use the LayoutInflater to map
 * the title and artist strings to the TextViews in the song layout(song_list_item).
 *
 * Created by Mohit on 11/22/15.
 */
public class SongAdapter extends BaseAdapter {
    private List<Song> songList;
    private LayoutInflater songInflater;
    Context context;

    public SongAdapter(List<Song> songList, Context context) {
        this.songList = songList;
        this.context = context;
        songInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return songList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // map to song layout
        RelativeLayout songListItemLayout = (RelativeLayout) songInflater.inflate(R.layout.song_list_item, viewGroup, false);

        // get title and artist and album art views
        TextView titleTextView = (TextView) songListItemLayout.findViewById(R.id.song_title);
        TextView artistTextView = (TextView) songListItemLayout.findViewById(R.id.song_artist);
        ImageView albumArtView = (ImageView) songListItemLayout.findViewById(R.id.album_art);

        // get song using position
        Song currentSong = songList.get(i);

        // get title and artist strings and set it
        String title = currentSong.getTitle();
        String artist = currentSong.getArtist();
        long albumId = currentSong.getAlbumID();
        titleTextView.setText(title);
        artistTextView.setText(artist);
        albumArtView.setImageBitmap(getAlbumArt(albumId,context));

        //set position as tag
        songListItemLayout.setTag(i);

        return songListItemLayout;
    }

    /**
     * Gets album art work given the album id
     * @param album_id The id of the Album whose artwork to find
     * @return A Bitmap of that Album Art
     */
    private Bitmap getAlbumArt(Long album_id, Context context) {
        Bitmap bm = null;
        try {
            final Uri sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart");

            Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);

            ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");

            if (pfd != null) {
                FileDescriptor fd = pfd.getFileDescriptor();
                bm = BitmapFactory.decodeFileDescriptor(fd);
            }
        } catch (Exception e) {
            Log.e("AMP stackTrace", e.toString());
        }
        return bm;
    }
}

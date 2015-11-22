package com.allegromusicplayer.com.allegromusicplayer.classes;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allegromusicplayer.R;

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

    public SongAdapter(List<Song> songList, Context context) {
        this.songList = songList;
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
        LinearLayout songListItemLayout = (LinearLayout) songInflater.inflate(R.layout.song_list_item, viewGroup, false);

        // get title and artist views
        TextView titleTextView = (TextView) songListItemLayout.findViewById(R.id.song_title);
        TextView artistTextView = (TextView) songListItemLayout.findViewById(R.id.song_artist);

        // get song using position
        Song currentSong = songList.get(i);

        // get title and artist strings and set it
        String title = currentSong.getTitle();
        String artist = currentSong.getArtist();
        titleTextView.setText(title);
        artistTextView.setText(artist);

        //set position as tag
        songListItemLayout.setTag(i);

        return songListItemLayout;
    }
}

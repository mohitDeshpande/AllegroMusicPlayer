package com.allegromusicplayer.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.allegromusicplayer.R;
import com.allegromusicplayer.classes.Song;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MusicPlayerActivity extends AppCompatActivity {

    private Intent intent;
    private Song song;
    ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        RelativeLayout musicPlayerLayout = (RelativeLayout) findViewById(R.id.musicPlayerLayout);

        intent = getIntent();
        song = (Song)intent.getSerializableExtra(MainActivity.EXTRA_SONG);

        // get items from the view
        TextView titleTextView = (TextView) musicPlayerLayout.findViewById(R.id.song_title);
        TextView artistTextView = (TextView) musicPlayerLayout.findViewById(R.id.song_artist);
        ImageView albumArtImageView = (ImageView) musicPlayerLayout.findViewById(R.id.album_art);
        if (imageLoader == null) {
            imageLoader = ImageLoader.getInstance();
        }

        titleTextView.setText(song.getTitle());
        artistTextView.setText(song.getArtist());
        imageLoader.displayImage(song.getAlbumArtUri().toString(),albumArtImageView);
    }
}

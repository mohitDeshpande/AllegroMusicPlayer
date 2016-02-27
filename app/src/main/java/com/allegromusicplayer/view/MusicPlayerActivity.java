package com.allegromusicplayer.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.allegromusicplayer.R;
import com.allegromusicplayer.classes.Song;
import com.allegromusicplayer.service.MusicPlaybackService;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class MusicPlayerActivity extends AppCompatActivity {

    private ImageLoader imageLoader;
    private MusicPlaybackService musicPlaybackService;
    private Intent playMusicIntent;
    private boolean isMusicPlaybackServiceBound = false;
    private Handler seekHandler;
    private Runnable seekBarUpdateRunnable;

    // UI components in this activity
    private SeekBar songSeekBar;
    private TextView titleTextView;
    private TextView artistTextView;
    private TextView songMaxDurationTextView;
    private TextView songCurrentDurationTextView;
    private ImageView albumArtImageView;



    /**
     * Create a connection to the MusicPlaybackService
     */
    private ServiceConnection musicPlaybackServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicPlaybackService.MusicPlaybackServiceBinder binder = (MusicPlaybackService.MusicPlaybackServiceBinder) service;
            musicPlaybackService = binder.getService();
            isMusicPlaybackServiceBound = true;

            setViewFromService();

            Log.e("Service", "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isMusicPlaybackServiceBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        if (playMusicIntent == null) {
            playMusicIntent = new Intent(this, MusicPlaybackService.class);
            bindService(playMusicIntent, musicPlaybackServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    /**
     * Dispatch onStart() to all fragments.  Ensure any created loaders are
     * now started.
     */
    @Override
    protected void onStart() {
        super.onStart();

        seekHandler = new Handler();
        seekBarUpdateRunnable = new Runnable() {
            Integer currentDuration = null;
            Integer maxDuration = null;

            SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
            Date currentDurationDate = null;
            Date maxDurationDate = null;

            @Override
            public void run() {
                if (isMusicPlaybackServiceBound && songSeekBar != null && musicPlaybackService.isSongPlaying()) {
                    currentDuration = musicPlaybackService.getSongCurrentPosition();
                    maxDuration = musicPlaybackService.getSongDuration();
                    songSeekBar.setMax(maxDuration);
                    songSeekBar.setProgress(currentDuration);
                    currentDurationDate = new Date(currentDuration);
                    maxDurationDate = new Date(maxDuration);
                    formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
                    songMaxDurationTextView.setText(formatter.format(maxDurationDate));
                    songCurrentDurationTextView.setText(formatter.format(currentDurationDate));
                    Log.e("Date", currentDurationDate.toString());
                }
                seekHandler.postDelayed(this,1000);
            }
        };
        seekBarUpdateRunnable.run();

        Log.e("MusicPlayerActivity", "onStart()");
    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.  This means
     * that in some cases the previous state may still be saved, not allowing
     * fragment transactions that modify the state.  To correctly interact
     * with fragments in their proper state, you should instead override
     * {@link #onResumeFragments()}.
     */
    @Override
    protected void onResume() {
        super.onResume();

        Log.e("MusicPlayerActivity", "onResume()");
    }


    private void setViewFromService() {
        RelativeLayout musicPlayerLayout = (RelativeLayout) findViewById(R.id.musicPlayerLayout);

        Song song = musicPlaybackService.getCurrentPlayingSong();

        // get items from the view
        titleTextView = (TextView) musicPlayerLayout.findViewById(R.id.song_title);
        artistTextView = (TextView) musicPlayerLayout.findViewById(R.id.song_artist);
        albumArtImageView = (ImageView) musicPlayerLayout.findViewById(R.id.album_art);
        songSeekBar = (SeekBar) musicPlayerLayout.findViewById(R.id.song_seek_bar);
        songCurrentDurationTextView = (TextView) musicPlayerLayout.findViewById(R.id.song_current_duration);
        songMaxDurationTextView = (TextView) musicPlayerLayout.findViewById(R.id.song_total_duration);

        if (imageLoader == null) {
            imageLoader = ImageLoader.getInstance();
        }

        titleTextView.setText(song.getTitle());
        artistTextView.setText(song.getArtist());
        imageLoader.displayImage(song.getAlbumArtUri().toString(), albumArtImageView);
        songSeekBar.setProgress(0);
        songSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (musicPlaybackService != null && isMusicPlaybackServiceBound) {
                    if (fromUser) {
                        musicPlaybackService.seek(progress);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                musicPlaybackService.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                musicPlaybackService.play();
            }
        });

    }

    /**
     * Calls the player service to pause or play
     *
     * @param view The view calling the method
     */
    public void playToggle(View view) {
        if (musicPlaybackService != null && isMusicPlaybackServiceBound) {
            if (musicPlaybackService.isSongPlaying()) {
                musicPlaybackService.pause();
            } else {
                musicPlaybackService.play();
            }
        }
    }

    public void skipNext(View view) {
        if (musicPlaybackService != null && isMusicPlaybackServiceBound) {
            musicPlaybackService.playNext();
            setViewFromService();
        }
    }

    public void skipPrev(View view) {
        if (musicPlaybackService != null && isMusicPlaybackServiceBound) {
            musicPlaybackService.playPrev();
            setViewFromService();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(musicPlaybackServiceConnection);
    }
}

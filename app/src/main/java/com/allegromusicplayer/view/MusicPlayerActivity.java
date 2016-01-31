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

public class MusicPlayerActivity extends AppCompatActivity {

    private Intent intent;
    private ImageLoader imageLoader;
    private MusicPlaybackService musicPlaybackService;
    private Intent playMusicIntent;
    private boolean isMusicPlaybackServiceBound = false;
    SeekBar songSeekBar;
    Handler seekHandler;
    Runnable seekBarUpdateRunnable;


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

        seekHandler = new Handler();
//        seekBarUpdateRunnable = new Runnable() {
//            @Override
//            public void run() {
//                updateSeekBar();
//                Log.e("RUN", "in run");
//            }
//        };

        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
            int i = 0;

            @Override
            public void run() {
                if (songSeekBar != null) {
                    songSeekBar.setMax(100);
                    songSeekBar.setProgress(i++);
                    seekHandler.postDelayed(this, 1000);
                    Log.e("UI Thread", "in run");
                }
            }
        });

        Log.e("MusicPlayerActivity", "onCreate()");

//        Log.i("Service exists?", musicPlaybackService.getSongList().toString());
    }

    /**
     * Dispatch onStart() to all fragments.  Ensure any created loaders are
     * now started.
     */
    @Override
    protected void onStart() {
        super.onStart();


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
        TextView titleTextView = (TextView) musicPlayerLayout.findViewById(R.id.song_title);
        TextView artistTextView = (TextView) musicPlayerLayout.findViewById(R.id.song_artist);
        ImageView albumArtImageView = (ImageView) musicPlayerLayout.findViewById(R.id.album_art);
        songSeekBar = (SeekBar) musicPlayerLayout.findViewById(R.id.song_seek_bar);

        if (imageLoader == null) {
            imageLoader = ImageLoader.getInstance();
        }

        titleTextView.setText(song.getTitle());
        artistTextView.setText(song.getArtist());
        imageLoader.displayImage(song.getAlbumArtUri().toString(), albumArtImageView);

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


//    public void updateSeekBar() {
//        if (isMusicPlaybackServiceBound && musicPlaybackService != null && musicPlaybackService.isSongPlaying()) {
//            if(songSeekBar.getMax() != musicPlaybackService.getSongDuration()) {
//                songSeekBar.setMax(musicPlaybackService.getSongDuration());
//            }
//            songSeekBar.setProgress(musicPlaybackService.getSongCurrentPosition());
//            seekHandler.postDelayed(seekBarUpdateRunnable, 1000);
//        }
//    }


    /**
     * Sets the seekbar listener according to the song
     * @param seekBar The seekbar to implement
     */
//    public void setSongSeekBar(final SeekBar seekBar) {
//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (fromUser) {
//                    if (musicPlaybackService != null && isMusicPlaybackServiceBound) {
//                        musicPlaybackService.seek(progress);
//                    }
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
//
//        if(musicPlaybackService != null && isMusicPlaybackServiceBound) {
//            seekBar.setMax(musicPlaybackService.getSongDuration());
//            final Handler handler = new Handler();
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    seekBar.setProgress(musicPlaybackService.getSongCurrentPosition());
//                    handler.postDelayed(this,500);
//                }
//            });
//        }
//    }

}

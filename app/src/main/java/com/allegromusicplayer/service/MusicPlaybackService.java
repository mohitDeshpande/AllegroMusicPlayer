package com.allegromusicplayer.service;

import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;

import com.allegromusicplayer.classes.Song;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public class MusicPlaybackService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {

    private MediaPlayer player;
    private List<Song> songList;
    private Integer currentSongIndex; //the index of the current song playing
    private IBinder musicBinder;

    public void setCurrentSongIndex(int currentSongIndex) {
        this.currentSongIndex = currentSongIndex;
    }

    private Context context;


    public void setSongList(List<Song> songList) {
        this.songList = songList;
    }

    public List<Song> getSongList() {
        return songList;
    }

    public class MusicPlaybackServiceBinder extends Binder {
        public MusicPlaybackService getService() {
            return MusicPlaybackService.this;
        }
    }


    /**
     * Called on the listener to notify it the audio focus for this listener has been changed.
     * The focusChange value indicates whether the focus was gained,
     * whether the focus was lost, and whether that loss is transient, or whether the new focus
     * holder will hold it for an unknown amount of time.
     * When losing focus, listeners can use the focus change information to decide what
     * behavior to adopt when losing focus. A music player could for instance elect to lower
     * the volume of its music stream (duck) for transient focus losses, and pause otherwise.
     *
     * @param focusChange the type of focus change, one of {@link AudioManager#AUDIOFOCUS_GAIN},
     *                    {@link AudioManager#AUDIOFOCUS_LOSS}, {@link AudioManager#AUDIOFOCUS_LOSS_TRANSIENT}
     *                    and {@link AudioManager#AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK}.
     */
    @Override
    public void onAudioFocusChange(int focusChange) {
        // TODO implement what player does on audio focus change http://developer.android.com/guide/topics/media/mediaplayer.html#audiofocus
    }

    /**
     * Called by the system when the service is first created.  Do not call this method directly.
     */
    @Override
    public void onCreate() {
        super.onCreate();

        // Get audio focus
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // TODO implement what to do when app is unable to get audio focus <link>http://developer.android.com/guide/topics/media/mediaplayer.html#audiofocus</link>
        }


        currentSongIndex = 0;

        musicBinder = new MusicPlaybackServiceBinder();
        context = getApplicationContext();

        // initialize the music player
        player = new MediaPlayer();
        player.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }


    /**
     * Return the communication channel to the service.  May return null if
     * clients can not bind to the service.  The returned
     * {@link IBinder} is usually for a complex interface
     * that has been <a href="{@docRoot}guide/components/aidl.html">described using
     * aidl</a>.
     * <p/>
     * <p><em>Note that unlike other application components, calls on to the
     * IBinder interface returned here may not happen on the main thread
     * of the process</em>.  More information about the main thread can be found in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html">Processes and
     * Threads</a>.</p>
     *
     * @param intent The Intent that was used to bind to this service,
     *               as given to {@link Context#bindService
     *               Context.bindService}.  Note that any extras that were included with
     *               the Intent at that point will <em>not</em> be seen here.
     * @return Return an IBinder through which clients can call on to the
     * service.
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBinder;
    }

    /**
     * Called when all clients have disconnected from a particular interface
     * published by the service.  The default implementation does nothing and
     * returns false.
     *
     * @param intent The Intent that was used to bind to this service,
     *               as given to {@link Context#bindService
     *               Context.bindService}.  Note that any extras that were included with
     *               the Intent at that point will <em>not</em> be seen here.
     * @return Return true if you would like to have the service's
     * {@link #onRebind} method later called when new clients bind to it.
     */
    @Override
    public boolean onUnbind(Intent intent) {
        player.stop();
        player.release();
        Log.e("MusicPlaybackService", "onUnbind()");
        return false;
    }

    /**
     * Called when the end of a media source is reached during playback.
     *
     * @param mp the MediaPlayer that reached the end of the file
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        playNext();
    }

    /**
     * Called to indicate an error.
     *
     * @param mp    the MediaPlayer the error pertains to
     * @param what  the type of error that has occurred:
     * @param extra an extra code, specific to the error. Typically
     *              implementation dependent.
     * @return True if the method handled the error, false if it didn't.
     * Returning false, or not having an OnErrorListener at all, will
     * cause the OnCompletionListener to be called.
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    /**
     * Called when the media file is ready for playback.
     *
     * @param mp the MediaPlayer that is ready for playback
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        try {
            mp.start();
        } catch (IllegalStateException e) {
            Log.v("AMP", "Unable to start media player", e);
        }
    }

    /**
     * Resets the player and sets the song which is at currentSongIndex and plays it.
     */
    public void setAndPlaySong() {
        try {
            player.reset();
            Song songToPlay = songList.get(currentSongIndex);
            player.setDataSource(songToPlay.getPath());

            player.prepareAsync();
        } catch (IOException e) {
            Log.v("AMP", "unable to set datasource in media player", e);
        } catch (IllegalStateException e) {
            Log.v("AMP", "Unable to prepare player Async", e);
        }
    }

    /**
     * Pause the current playback
     */
    public void pause() {
        player.pause();
    }

    /**
     * Get the duration of the currently playing song
     *
     * @return The song duration in seconds. If duration is not available then -1 is returned
     */
    public int getSongDuration() {
        int duration = player.getDuration();

        if (duration >= 0) {
            duration *= 1000;
        }

        return duration;
    }

    /**
     * Check is a song is being played
     * @return true if currently playing, else false
     */
    public boolean isSongPlaying() {
        return player.isPlaying();
    }

    /**
     * Seek song to a given position
     * @param position in milliseconds
     */
    public void seek(int position) {
        player.seekTo(position);
    }

    /**
     * Resume playback
     */
    public void play() {
        player.start();
    }

    /**
     * Get the current playing song elapsed duration
     * @return The position of the current playing song in milliseconds
     */
    public int getSongCurrentPosition() {
        return player.getCurrentPosition();
    }

    /**
     * Play the next song in the list.
     * If the list has reached the end then the counter wil reset and go to the start of the list
     */
    public void playNext() {
        currentSongIndex++;
        if (currentSongIndex >= songList.size()) {
            currentSongIndex = 0;
        }
        setAndPlaySong();
    }

    /**
     * Play the previous song in the list
     * If the list has reached the beginning then the counter wil reset and go to the end of the list
     */
    public void playPrev() {
        currentSongIndex--;
        if (currentSongIndex < 0) {
            currentSongIndex = songList.size() - 1;
        }
        setAndPlaySong();
    }

    /**
     * Gets the song currently given to the media player
     *
     * @return The currently playing song or <code>null</code> is that song is not in the song list
     * @throws IllegalStateException if the current song is not set or the song list is empty
     */
    public Song getCurrentPlayingSong() throws IllegalStateException {
        if (currentSongIndex == null) {
            throw new IllegalStateException("No index for current playing song exists");
        }
        if (songList == null || songList.isEmpty()) {
            throw new IllegalStateException("The song list seems to be empty or doesn't exist at all");
        }
        Song song = songList.get(currentSongIndex);

        return song;
    }


}

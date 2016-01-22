package com.allegromusicplayer;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.MergeCursor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.allegromusicplayer.classes.Song;
import com.allegromusicplayer.service.MusicPlaybackService;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Song> songList;
    private ListView songListView;
    private MusicPlaybackService musicPlaybackService;
    private Intent playMusicIntent;
    private boolean isMusicPlaybackServiceBound = false;

    /**
     * Create a connection to the MusicPlaybackService
     */
    private ServiceConnection musicPlaybackServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicPlaybackService.MusicPlaybackServiceBinder binder = (MusicPlaybackService.MusicPlaybackServiceBinder)service;
            musicPlaybackService = binder.getService();
            musicPlaybackService.setSongList(songList);
            isMusicPlaybackServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isMusicPlaybackServiceBound = false;
        }
    };

    /**
     * Dispatch onStart() to all fragments.  Ensure any created loaders are
     * now started.
     */
    @Override
    protected void onStart() {
        super.onStart();

        if (playMusicIntent == null) {
            playMusicIntent = new Intent(this,MusicPlaybackService.class);
            bindService(playMusicIntent, musicPlaybackServiceConnection, Context.BIND_AUTO_CREATE);
            startService(playMusicIntent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songList = new ArrayList<Song>();
        songListView = (ListView) findViewById(R.id.song_list);

        populateSongList(songList);

        // sort the song list in ascending order according to title
        Collections.sort(songList, new Comparator<Song>() {
            @Override
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });

        //Log.d("AMP - song", songList.toString());

        // Configure image loader
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this).defaultDisplayImageOptions(defaultOptions).build();
        ImageLoader.getInstance().init(configuration);

        // prevent lag while scrolling
        boolean pauseOnScroll = false; // or true
        boolean pauseOnFling = false; // or false
        PauseOnScrollListener listener = new PauseOnScrollListener(ImageLoader.getInstance(), pauseOnScroll, pauseOnFling);
        songListView.setOnScrollListener(listener);

        // set the adapter for the list view to populate the items in it
        SongAdapter songAdapter = new SongAdapter(songList,this);
        songListView.setAdapter(songAdapter);



        /**/
//        Uri myUri = null;
//        MediaPlayer mediaPlayer = null;
//        try {
//            myUri = ContentUris.withAppendedId(
//                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songList.get(0).getId()); // initialize Uri here
//            mediaPlayer = new MediaPlayer();
//            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//
//            mediaPlayer.setDataSource(getApplicationContext(), myUri);
//            mediaPlayer.prepare();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (NullPointerException e) {
//            if (myUri == null) {
//                Log.e("AMP Null Pointer","uri is null");
//            } else {
//                Log.e("AMP Null Pointer","uri is NOT null");
//            }
//
//            e.printStackTrace();
//        } catch (Exception e) {
//            Log.e("Exception Stack Trace", e.toString());
//        }
//        mediaPlayer.start();
        /**/

    }

    /**
     * Retrieve the song info and put it in the song list
     *
     * @param songList The List in which the created song objects are to be put
     */
    public void populateSongList(List<Song> songList) {
        ContentResolver musicResolver = getContentResolver();
        Uri externalMusicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri internalMusicUri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;

        Cursor[] musicCursorArray = new Cursor[2];
        musicCursorArray[0] = musicResolver.query(externalMusicUri,null,null,null,null);
        musicCursorArray[1] = musicResolver.query(internalMusicUri,null,null,null,null);


        MergeCursor musicCursor = new MergeCursor(musicCursorArray);

        // iterate over the results in the Cursor
        if (musicCursor != null && musicCursor.moveToFirst()) {

            // only get audio files which are music
            if (musicCursor.getInt(musicCursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC)) == 0) {

                // get the columns indexes of the media in the cursor
                int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
                int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                int albumColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                int albumIDColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);

                // add songs to the songList
                do {
                    long id = musicCursor.getLong(idColumn);
                    String title = musicCursor.getString(titleColumn);
                    String album = musicCursor.getString(albumColumn);
                    String artist = musicCursor.getString(artistColumn);
                    long albumID = musicCursor.getLong(albumIDColumn);

                    // ignore duplicates
                    boolean songPresent = false;
                    for (int i = 0; i < songList.size(); i++) {
                        Song song = songList.get(i);
                        if (song.getTitle().equals(title) && song.getAlbum().equals(album) && song.getArtist().equals(artist)){
                            songPresent = true;
                            break;
                        }
                    }
                    if (!songPresent){
                        songList.add(new Song(id,title,artist,album,albumID));
                    }


                }while (musicCursor.moveToNext());
            }
        }
    }
}

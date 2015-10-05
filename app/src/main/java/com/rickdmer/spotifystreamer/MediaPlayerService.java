package com.rickdmer.spotifystreamer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Richard on 10/5/2015.
 */
public class MediaPlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private final IBinder binder = new LocalBinder();

    private MediaPlayer mediaPlayer;
    private ArrayList<CustomTrack> trackList = null;
    private int trackPosition = 0;
    private boolean isPrepared;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class LocalBinder extends Binder {
        public MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        initMediaPlayer();
    }

    private void initMediaPlayer() {
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        isPrepared = false;
        mp.reset();
        nextTrack();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        isPrepared = true;
        mp.start();
    }

    public void playTrack() {
        isPrepared = false;
        mediaPlayer.reset();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(trackList.get(trackPosition).trackPreviewUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.prepareAsync();
    }

    public void pause() {
        mediaPlayer.pause();
    }

    public void start() {
        mediaPlayer.start();
    }

    public void nextTrack() {
        trackPosition++;
        if (trackPosition >= trackList.size()) {
            trackPosition = 0;
        }
        playTrack();
    }

    public void previousTrack() {
        trackPosition--;
        if (trackPosition < 0) {
            trackPosition = trackList.size() - 1;
        }
        playTrack();
    }

    public void seekTo(int position) {
        mediaPlayer.seekTo(position);
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public void setTrackList(ArrayList<CustomTrack>_trackList) {
        trackList = _trackList;
    }

    public void setTrackPosition(int position) {
        trackPosition = position;
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public int getTrackPosition() {
        return trackPosition;
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public CustomTrack getCurrentTrack() {
        return trackList.get(trackPosition);
    }
}

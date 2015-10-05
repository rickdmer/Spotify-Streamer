package com.rickdmer.spotifystreamer;

import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Richard on 9/30/2015.
 */
public class TrackPlaybackFragment extends DialogFragment {

    //MediaPlayer mediaPlayer = new MediaPlayer();

    MediaPlayerService mediaPlayerService;
    boolean isServiceBound = false;

    ImageButton imageButtonPlayPause;
    ImageButton imageButtonNext;
    ImageButton imageButtonPrev;
    TextView textViewArtistName;
    TextView textViewAlbumName;
    TextView textViewTrackName;
    ImageView imageViewAlbumArt;
    TextView textViewSongLength;
    TextView textViewCurrentSongProgress;
    SeekBar seekBar;
    View rootView;
    Handler seekbarHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_player, container, false);
        textViewArtistName = (TextView) rootView.findViewById(R.id.textViewArtistName);
        textViewAlbumName = (TextView) rootView.findViewById(R.id.textViewAlbumName);
        textViewTrackName = (TextView) rootView.findViewById(R.id.textViewSong);
        imageViewAlbumArt = (ImageView) rootView.findViewById(R.id.imageViewAlbumArt);
        imageButtonPlayPause = (ImageButton) rootView.findViewById(R.id.imageButtonPlayPause);
        imageButtonNext = (ImageButton) rootView.findViewById(R.id.imageButtonNext);
        imageButtonPrev = (ImageButton) rootView.findViewById(R.id.imageButtonPrev);

        textViewCurrentSongProgress = (TextView) rootView.findViewById(R.id.textViewCurrentSongProgress);
        textViewSongLength = (TextView) rootView.findViewById((R.id.textViewSongLength));
        seekBar = (SeekBar) rootView.findViewById(R.id.seekBar);

        seekBar.setMax(30);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayerService != null && isServiceBound && fromUser) {
                    mediaPlayerService.seekTo(progress * 1000);
                }
            }
        });

        imageButtonPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayerService.isPlaying()) {
                    imageButtonPlayPause.setImageResource(android.R.drawable.ic_media_play);
                    mediaPlayerService.pause();
                } else {
                    imageButtonPlayPause.setImageResource(android.R.drawable.ic_media_pause);
                    mediaPlayerService.start();
                }
            }
        });

        imageButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayerService.nextTrack();
                loadCurrentTrack();
            }
        });

        imageButtonPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayerService.previousTrack();
                loadCurrentTrack();
            }
        });

        seekbarHandler = new Handler();
        if (getActivity() != null) {
            getActivity().runOnUiThread(seekBarRunnable);
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent serviceIntent = new Intent(getActivity(), MediaPlayerService.class);
        getActivity().startService(serviceIntent);
        getActivity().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void loadCurrentTrack() {
        CustomTrack currentTrack = mediaPlayerService.getCurrentTrack();

        textViewArtistName.setText(currentTrack.artistName);
        textViewAlbumName.setText(currentTrack.albumName);
        textViewTrackName.setText(currentTrack.trackName);
        textViewSongLength.setText("0:30");
        textViewCurrentSongProgress.setText("0:00");
        seekBar.setProgress(0);

        if (Patterns.WEB_URL.matcher(currentTrack.albumImageUrl).matches()) {
            Picasso.with(getActivity()).load(currentTrack.albumImageUrl).into(imageViewAlbumArt);
        } else {
            Toast.makeText(getActivity(), R.string.invalid_image_url, Toast.LENGTH_SHORT).show();
        }

    }

    Runnable seekBarRunnable = new Runnable() {

        @Override
        public void run() {
            if (mediaPlayerService != null && mediaPlayerService.isPlaying() && mediaPlayerService.getCurrentPosition() <= mediaPlayerService.getDuration()) {
                int currentTime = (mediaPlayerService.getCurrentPosition() / 1000) + 1;
                seekBar.setProgress(currentTime);
                textViewCurrentSongProgress.setText("0:" + String.format("%02d", currentTime));
            }
            seekbarHandler.postDelayed(this, 1000);
        }
    };

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            mediaPlayerService = binder.getService();
            isServiceBound = true;
            loadCurrentTrack();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceBound = false;
        }
    };
}

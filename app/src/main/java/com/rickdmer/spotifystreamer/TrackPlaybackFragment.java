package com.rickdmer.spotifystreamer;

import android.app.DialogFragment;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
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

    MediaPlayer mediaPlayer = new MediaPlayer();
    ImageButton imageButtonPlayPause;
    TextView textViewArtistName;
    TextView textViewAlbumName;
    TextView textViewTrackName;
    ImageView imageViewAlbumArt;
    TextView textViewSongLength;
    TextView textViewCurrentSongProgress;
    SeekBar seekBar;
    View rootView;
    Handler seekbarHandler;

    private void loadTrack(String trackUrl) throws IOException {
        mediaPlayer.reset();
        imageButtonPlayPause.setImageResource(android.R.drawable.ic_media_play);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setDataSource(trackUrl);

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer _mediaPlayer) {
                final long trackLength = mediaPlayer.getDuration();

                // method from http://stackoverflow.com/a/10874133
                int seconds = (int) (trackLength / 1000) % 60;
                int minutes = (int) ((trackLength / (1000 * 60)) % 60);

                String duration = String.valueOf(minutes) + ":" + String.valueOf(seconds);

                seekBar.setMax(seconds);
                seekbarHandler = new Handler();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(seekBarRunnable);
                }
                textViewSongLength.setText(duration);
            }
        });
        mediaPlayer.prepareAsync();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_player, container, false);
        textViewArtistName = (TextView) rootView.findViewById(R.id.textViewArtistName);
        textViewAlbumName = (TextView) rootView.findViewById(R.id.textViewAlbumName);
        textViewTrackName = (TextView) rootView.findViewById(R.id.textViewSong);
        imageViewAlbumArt = (ImageView) rootView.findViewById(R.id.imageViewAlbumArt);
        imageButtonPlayPause = (ImageButton) rootView.findViewById(R.id.imageButtonPlayPause);
        textViewCurrentSongProgress = (TextView) rootView.findViewById(R.id.textViewCurrentSongProgress);
        textViewSongLength = (TextView) rootView.findViewById((R.id.textViewSongLength));
        seekBar = (SeekBar) rootView.findViewById(R.id.seekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress * 1000);
                }
            }
        });

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("artistName")) {
            String artistName = intent.getStringExtra("artistName");
            textViewArtistName.setText(artistName);
        }

        if (intent != null && intent.hasExtra("albumName")) {
            String albumName = intent.getStringExtra("albumName");
            textViewAlbumName.setText(albumName);
        }

        if (intent != null && intent.hasExtra("trackName")) {
            String trackName = intent.getStringExtra("trackName");
            textViewTrackName.setText(trackName);
        }

        if (intent != null && intent.hasExtra("albumImageUrl")) {
            String albumImageUrl = intent.getStringExtra("albumImageUrl");
            if (Patterns.WEB_URL.matcher(albumImageUrl).matches()) {
                Picasso.with(getActivity()).load(albumImageUrl).into(imageViewAlbumArt);
            } else {
                Toast.makeText(getActivity(), R.string.invalid_image_url, Toast.LENGTH_SHORT).show();
            }
        }

        if (intent != null && intent.hasExtra("trackPreviewUrl")) {
            final String trackPreviewUrl = intent.getStringExtra("trackPreviewUrl");

            // load track
            try {
                loadTrack(trackPreviewUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }

            imageButtonPlayPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        playPauseTrack(trackPreviewUrl);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                private void playPauseTrack(String trackUrl) throws IOException {

                    if (mediaPlayer.isPlaying()) {
                        imageButtonPlayPause.setImageResource(android.R.drawable.ic_media_play);
                        mediaPlayer.pause();
                    } else {
                        imageButtonPlayPause.setImageResource(android.R.drawable.ic_media_pause);
                        mediaPlayer.start();
                    }
                }
            });
        }

        return rootView;
    }

    Runnable seekBarRunnable = new Runnable() {

        @Override
        public void run() {
            if (mediaPlayer.isPlaying() && mediaPlayer.getCurrentPosition() <= mediaPlayer.getDuration()) {
                int currentTime = (mediaPlayer.getCurrentPosition() / 1000) + 1;
                seekBar.setProgress(currentTime);
                textViewCurrentSongProgress.setText("0:" + String.format("%02d", currentTime));
            }
            seekbarHandler.postDelayed(this, 1000);
        }
    };
}

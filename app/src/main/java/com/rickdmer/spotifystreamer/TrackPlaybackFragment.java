package com.rickdmer.spotifystreamer;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;

/**
 * Created by Richard on 9/30/2015.
 */
public class TrackPlaybackFragment extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_player, container, false);
        TextView textViewArtistName;
        TextView textViewAlbumName;
        TextView textViewTrackName;
        TextView textViewSongLength;
        ImageView imageViewAlbumArt;
        ImageButton imageButtonPausePlay;

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("artistName")) {
            String artistName = intent.getStringExtra("artistName");
            textViewArtistName = (TextView) rootView.findViewById(R.id.textViewArtistName);
            textViewArtistName.setText(artistName);
        }

        if (intent != null && intent.hasExtra("albumName")) {
            String albumName = intent.getStringExtra("albumName");
            textViewAlbumName = (TextView) rootView.findViewById(R.id.textViewAlbumName);
            textViewAlbumName.setText(albumName);
        }

        if (intent != null && intent.hasExtra("trackName")) {
            String trackName = intent.getStringExtra("trackName");
            textViewTrackName = (TextView) rootView.findViewById(R.id.textViewSong);
            textViewTrackName.setText(trackName);
        }

        if (intent != null && intent.hasExtra("trackLength")) {
            long trackLength = intent.getLongExtra("trackLength", 0);

            // method from http://stackoverflow.com/a/10874133
            int seconds = (int) (trackLength / 1000) % 60 ;
            int minutes = (int) ((trackLength / (1000*60)) % 60);
            int hours   = (int) ((trackLength / (1000*60*60)) % 24);

            String duration = String.valueOf(minutes) + ":" + String.valueOf(seconds);
            textViewSongLength = (TextView) rootView.findViewById((R.id.textViewSongLength));
            textViewSongLength.setText(duration);
        }

        if (intent != null && intent.hasExtra("albumImageUrl")) {
            String albumImageUrl = intent.getStringExtra("albumImageUrl");
            if (Patterns.WEB_URL.matcher(albumImageUrl).matches()) {
                imageViewAlbumArt = (ImageView) rootView.findViewById(R.id.imageViewAlbumArt);
                Picasso.with(getActivity()).load(albumImageUrl).into(imageViewAlbumArt);
            } else {
                Toast.makeText(getActivity(), R.string.invalid_image_url, Toast.LENGTH_SHORT).show();
            }
        }

        if (intent != null && intent.hasExtra("trackPreviewUrl")) {
            final String trackPreviewUrl = intent.getStringExtra("trackPreviewUrl");
            imageButtonPausePlay = (ImageButton) rootView.findViewById(R.id.imageButtonPlayPause);
            imageButtonPausePlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        playTrack(trackPreviewUrl);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                MediaPlayer mediaPlayer = new MediaPlayer();

                private void playTrack(String trackUrl) throws IOException {
                    mediaPlayer.reset();
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.setDataSource(trackUrl);
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer _mediaPlayer) {
                            mediaPlayer.start();
                        }
                    });
                    mediaPlayer.prepareAsync();
                }
            });
        }

        return rootView;
    }
}

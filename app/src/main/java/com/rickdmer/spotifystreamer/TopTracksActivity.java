package com.rickdmer.spotifystreamer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;


public class TopTracksActivity extends ActionBarActivity {

    boolean isServiceBound = false;
    MediaPlayerService mediaPlayerService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_tracks);

        if (savedInstanceState == null) {

            Bundle args = new Bundle();
            args.putParcelable(TopTracksFragment.TRACKARTISTITEM_KEY,
                    getIntent().getParcelableExtra(TopTracksFragment.TRACKARTISTITEM_KEY));

            TopTracksFragment topTracksFragment = new TopTracksFragment();
            topTracksFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.top_tracks_container, topTracksFragment)
                    .commit();
        }

        Intent intent = this.getIntent();
        if (intent != null && intent.hasExtra("artistName")) {
            ActionBar actionBar = getSupportActionBar();
            CharSequence artistName = intent.getStringExtra("artistName");
            actionBar.setSubtitle(artistName);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent serviceIntent = new Intent(this, MediaPlayerService.class);
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isServiceBound = true;
            MediaPlayerService.LocalBinder localBinder = (MediaPlayerService.LocalBinder) service;
            mediaPlayerService = localBinder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceBound = false;
            mediaPlayerService = null;
        }
    };

    public MediaPlayerService getMediaPlayerService() {
        return mediaPlayerService;
    }

    public boolean isServiceBound() {
        return isServiceBound;
    }

    @Override
    public void onStop() {
        super.onStop();
        unbindService(serviceConnection);
    }
}

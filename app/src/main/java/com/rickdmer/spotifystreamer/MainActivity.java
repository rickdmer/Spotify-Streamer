package com.rickdmer.spotifystreamer;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;


public class MainActivity extends ActionBarActivity implements MainFragment.Callback {

    private boolean mTwoPane;
    private MainFragment mainFragment;
    private static final String TOPTRACKSFRAGMENT_TAG = "TTFTAG";
    private static final String SEARCHEDARTIST_KEY = "SEARCHED_ARTIST";

    boolean isServiceBound = false;
    MediaPlayerService mediaPlayerService;
    private String mSearched;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.top_tracks_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.top_tracks_container, new TopTracksFragment(), TOPTRACKSFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }

        if (savedInstanceState != null) {
            mSearched = savedInstanceState.getString(SEARCHEDARTIST_KEY);
        }

        mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(getIntent());
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            mainFragment.searchArtist(query);
            mSearched = query;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        if (mSearched != null) {
            savedState.putString(SEARCHEDARTIST_KEY, mSearched);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent serviceIntent = new Intent(this, MediaPlayerService.class);
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);
        startService(serviceIntent);
    }

    @Override
    public void onStop() {
        super.onStop();
        unbindService(serviceConnection);
    }

    @Override
    public void onArtistItemSelected(CustomArtist artistItem) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable(TopTracksFragment.TRACKARTISTITEM_KEY, artistItem);

            TopTracksFragment fragment = new TopTracksFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.top_tracks_container, fragment, TOPTRACKSFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, TopTracksActivity.class);
            Bundle extras = new Bundle();
            extras.putParcelable(TopTracksFragment.TRACKARTISTITEM_KEY, artistItem);
            intent.putExtras(extras);
            startActivity(intent);
        }
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
}

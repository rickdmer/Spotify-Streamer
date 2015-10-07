package com.rickdmer.spotifystreamer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by Richard on 9/30/2015.
 */
public class TrackPlaybackActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        if (savedInstanceState == null) {
            TrackPlaybackFragment fragment = TrackPlaybackFragment.newInstance(false);
            getSupportFragmentManager().beginTransaction().add(R.id.activity_player, fragment, "tag").commit();
        }

        Intent intent = this.getIntent();
        if (intent != null && intent.hasExtra("artistName")) {
            ActionBar actionBar = getSupportActionBar();
            CharSequence artistName = intent.getStringExtra("artistName");
            actionBar.setSubtitle(artistName);
        }
    }

}

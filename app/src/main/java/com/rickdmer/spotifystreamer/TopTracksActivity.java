package com.rickdmer.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;


public class TopTracksActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_tracks);

        Intent intent = this.getIntent();
        if (intent != null && intent.hasExtra("artistName")) {
            ActionBar actionBar = getSupportActionBar();
            CharSequence artistName = intent.getStringExtra("artistName");
            actionBar.setSubtitle(artistName);
        }
    }
}

package com.rickdmer.spotifystreamer;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by Richard on 9/30/2015.
 */
public class TrackPlaybackFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_player, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("artistName")) {
            String artistName = intent.getStringExtra("artistName");
        }

        return rootView;
    }
}

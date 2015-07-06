package com.rickdmer.spotifystreamer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.models.Track;


/**
 * A placeholder fragment containing a simple view.
 */
public class TopTracksFragment extends Fragment {

    public TopTracksFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        TrackListViewAdapter mTracksAdapter = new TrackListViewAdapter (
                getActivity(),
                R.layout.list_item_track,
                new ArrayList<Track>());

        View rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);

        ListView trackListView = (ListView) rootView.findViewById(R.id.listview_track);
        trackListView.setAdapter(mTracksAdapter);

        return rootView;
    }
}

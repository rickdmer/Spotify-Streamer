package com.rickdmer.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;


/**
 * A placeholder fragment containing a simple view.
 */
public class TopTracksFragment extends Fragment {

    private static final String LOG_TAG = TopTracksFragment.class.getSimpleName();
    private TrackListViewAdapter mTracksAdapter;
    private ArrayList<CustomTrack> trackList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey("tracks")) {
            trackList = savedInstanceState.getParcelableArrayList("tracks");
        } else {
            trackList = new ArrayList<CustomTrack>();
        }
    }

    public TopTracksFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("tracks", trackList);
        super.onSaveInstanceState(outState);
    }


    private String mArtistIdStr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mTracksAdapter = new TrackListViewAdapter (
                getActivity(),
                R.layout.list_item_track,
                trackList);

        View rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);

        ListView trackListView = (ListView) rootView.findViewById(R.id.listview_track);
        trackListView.setAdapter(mTracksAdapter);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("artistId")) {
            mArtistIdStr = intent.getStringExtra("artistId");
            GetTopTracksTask task = new GetTopTracksTask();
            task.execute(mArtistIdStr);
        }

        return rootView;
    }
    //
    public class GetTopTracksTask extends AsyncTask<String, Void, CustomTrack[]> {
        @Override
        protected CustomTrack[] doInBackground(String... params) {
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();

            //TODO: Use error handling

            Map<String, Object> options = new HashMap<>();
            options.put("country", "US");

            String artistId = params[0];

            Tracks tracksResults = spotify.getArtistTopTrack(artistId, options);
            List<Track> tracks = tracksResults.tracks;

            CustomTrack[] resultsTracks = new CustomTrack[tracks.size()];

            for (int i = 0; i < tracks.size(); i++) {
                Track track = tracks.get(i);
                String albumImgUrl = null;
                if (track.album.images.size() > 0) {
                    albumImgUrl = track.album.images.get(0).url;
                }
                resultsTracks[i] = new CustomTrack(track.name, track.album.name, albumImgUrl);
            }

            return resultsTracks;
        }

        @Override
        protected void onPostExecute(CustomTrack[] result) {
            if (result != null) {
                mTracksAdapter.clear();
                for (CustomTrack trackData : result) {
                    mTracksAdapter.add(trackData);
                }
            }
        }
    }
}

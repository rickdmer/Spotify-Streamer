package com.rickdmer.spotifystreamer;

import android.support.v4.app.DialogFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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
    private String mArtistName;
    private String mArtistSpotifyID;
    private int mPosition;
    private boolean mHasSavedInstance = false;

    private static final String TRACKITEMLIST_KEY = "TRACK_ITEM_LIST";
    private static final String TRACKPOSITION_KEY = "TRACK_POSITION";
    public static final String TRACKARTISTITEM_KEY = "TRACKARTISTITEM_KEY";
    private static final String TRACKPLAYBACKFRAGMENT_TAG = "TRACK_PLAYBACK_FRAGMENT";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.containsKey(TRACKITEMLIST_KEY)) {
            trackList = savedInstanceState.getParcelableArrayList(TRACKITEMLIST_KEY);
            mHasSavedInstance = true;
        } else {
            trackList = new ArrayList<CustomTrack>();
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(TRACKPOSITION_KEY)) {
            mPosition = savedInstanceState.getInt(TRACKPOSITION_KEY);
            mHasSavedInstance = true;
        } else {
            mPosition = ListView.INVALID_POSITION;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle args = getArguments();
        if (args != null) {
            CustomArtist artistItem = args.getParcelable(TRACKARTISTITEM_KEY);
            mArtistSpotifyID = artistItem.artistId;
            mArtistName = artistItem.artistName;
        }

        mTracksAdapter = new TrackListViewAdapter (
                getActivity(),
                R.layout.list_item_track,
                trackList);

        View rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);

        ListView trackListView = (ListView) rootView.findViewById(R.id.listview_track);
        trackListView.setAdapter(mTracksAdapter);

        trackListView.setOnItemClickListener(onTrackItemClickListener);

        return rootView;
    }

    private AdapterView.OnItemClickListener onTrackItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mPosition = position;
            String device = getString(R.string.device);

            if ("large".equalsIgnoreCase(device)) {
                MainActivity parentActivity = (MainActivity) getActivity();
                if (parentActivity.isServiceBound()) {
                    MediaPlayerService mediaPlayerService = parentActivity.getMediaPlayerService();
                    mediaPlayerService.setTrackList(trackList);
                    mediaPlayerService.setTrackPosition(position);
                    mediaPlayerService.playTrack();
                }
            } else {
                TopTracksActivity parentActivity = (TopTracksActivity) getActivity();
                if (parentActivity.isServiceBound()) {
                    MediaPlayerService mediaPlayerService = parentActivity.getMediaPlayerService();
                    mediaPlayerService.setTrackList(trackList);
                    mediaPlayerService.setTrackPosition(position);
                    mediaPlayerService.playTrack();
                }
            }

            DialogFragment trackPlaybackFragment = new TrackPlaybackFragment();
            trackPlaybackFragment.show(getActivity().getSupportFragmentManager(), TRACKPLAYBACKFRAGMENT_TAG);
        }
    };

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((ActionBarActivity) getActivity()).getSupportActionBar().setSubtitle(mArtistName);
        if (mArtistSpotifyID != null && !mHasSavedInstance) {
            searchTracks(mArtistSpotifyID);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(TRACKITEMLIST_KEY, trackList);
        outState.putInt(TRACKPOSITION_KEY, mPosition);
        super.onSaveInstanceState(outState);
    }

    private void searchTracks(String artist) {
        GetTopTracksTask getTopTracksTask = new GetTopTracksTask();
        getTopTracksTask.execute(artist);
    }


    private String mArtistIdStr;
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

            CustomTrack[] resultsTracks;

            if (Utils.isNetworkAvailable(getActivity())) {
                try {
                    Tracks tracksResults = spotify.getArtistTopTrack(artistId, options);
                    List<Track> tracks = tracksResults.tracks;

                    resultsTracks = new CustomTrack[tracks.size()];

                    if (tracks.size() > 0) {
                        for (int i = 0; i < tracks.size(); i++) {
                            Track track = tracks.get(i);
                            String albumImgUrl = null;
                            if (track.album.images.size() > 0) {
                                albumImgUrl = track.album.images.get(0).url;
                            }

                            String artistName = track.artists.get(0).name;
                            for (int j = 1; j < track.artists.size(); j++) {
                                artistName += ", " + track.artists.get(j).name;
                            }

                            long trackLength = track.duration_ms;

                            resultsTracks[i] = new CustomTrack(track.name, track.album.name, albumImgUrl, artistName, trackLength, track.preview_url);
                        }
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), getActivity().getString(R.string.no_tracks_for_artist), Toast.LENGTH_SHORT).show();
                            }
                        });
                        resultsTracks = null;
                    }

                } catch (Error error) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), getActivity().getString(R.string.api_error), Toast.LENGTH_SHORT).show();
                        }
                    });
                    resultsTracks = null;
                }
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), getActivity().getString(R.string.no_network_available), Toast.LENGTH_SHORT).show();
                    }
                });
                resultsTracks = null;
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

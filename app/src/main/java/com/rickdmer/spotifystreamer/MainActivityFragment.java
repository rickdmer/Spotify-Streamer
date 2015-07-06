package com.rickdmer.spotifystreamer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private ArrayAdapter<String> mArtistsAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mArtistsAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_artist,
                R.id.list_item_artist_textview,
                new ArrayList<String>());

        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        ListView artistListView = (ListView) rootView.findViewById(R.id.listview_artist);
        artistListView.setAdapter(mArtistsAdapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        SearchForArtistTask task = new SearchForArtistTask();
        task.execute();
    }

    //
    public class SearchForArtistTask extends AsyncTask<String, Void, String[]>
    {
        @Override
        protected String[] doInBackground(String... params) {
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();

            Map<String, Object> options = new HashMap<>();
            options.put("country", "US");

            //TODO: Use error handling

            // Get first artist result
            ArtistsPager artistsResults = spotify.searchArtists("Paul");
            List<Artist> artists = artistsResults.artists.items;
            Artist artist = artists.get(0);

            Tracks tracksResults = spotify.getArtistTopTrack(artist.id, options);
            List<Track> tracks = tracksResults.tracks;

            String[] resultsArtists = new String[tracks.size()];

            for (int i = 0; i < tracks.size(); i++) {
                Track track = tracks.get(i);
                Log.i(LOG_TAG, i + " " + track.name);
                resultsArtists[i] = track.name;
            }

            return resultsArtists;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                mArtistsAdapter.clear();
                for(String artistStr : result) {
                    mArtistsAdapter.add(artistStr);
                }
                // New data is back from the server.  Hooray!
            }
        }
    }

}

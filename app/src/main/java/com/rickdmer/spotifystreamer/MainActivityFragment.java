package com.rickdmer.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private ArtistListViewAdapter mArtistsAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mArtistsAdapter = new ArtistListViewAdapter (
                getActivity(),
                R.layout.list_item_artist,
                new ArrayList<Artist>());

        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        ListView artistListView = (ListView) rootView.findViewById(R.id.listview_artist);
        artistListView.setAdapter(mArtistsAdapter);

        // Search handling
        EditText artistSearch = (EditText) rootView.findViewById(R.id.edittext_artist_search);
        artistSearch.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    SearchForArtistTask task = new SearchForArtistTask();
                    task.execute(v.getText().toString());
                    return true;
                }
                return false;
            }
        });

        // Item click handling
        artistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Artist clickedArtist = mArtistsAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), TopTracksActivity.class)
                        .putExtra("artistId", clickedArtist.id)
                        .putExtra("artistName", clickedArtist.name);
                startActivity(intent);
            }
        });

        return rootView;
    }

    //
    public class SearchForArtistTask extends AsyncTask<String, Void, Artist[]>
    {
        @Override
        protected Artist[] doInBackground(String... params) {
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();

            //TODO: Use error handling

            // Get first artist result
            ArtistsPager artistsResults = spotify.searchArtists(params[0]);
            List<Artist> artists = artistsResults.artists.items;

            Artist[] resultsArtists = new Artist[artists.size()];

            for (int i = 0; i < artists.size(); i++) {
                Artist artist = artists.get(i);
                resultsArtists[i] = artist;
            }

            return resultsArtists;
        }

        @Override
        protected void onPostExecute(Artist[] result) {
            if (result != null) {
                mArtistsAdapter.clear();
                for(Artist artistData : result) {
                    mArtistsAdapter.add(artistData);
                }
            }
        }
    }

}

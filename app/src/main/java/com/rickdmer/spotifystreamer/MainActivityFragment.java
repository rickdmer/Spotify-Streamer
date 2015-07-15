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
import android.widget.Toast;

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
    private ArrayList<CustomArtist> artistList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey("artists")) {
            artistList = savedInstanceState.getParcelableArrayList("artists");
        } else {
            artistList = new ArrayList<CustomArtist>();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("artists", artistList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mArtistsAdapter = new ArtistListViewAdapter (
                getActivity(),
                R.layout.list_item_artist,
                artistList);

        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        ListView artistListView = (ListView) rootView.findViewById(R.id.listview_artist);
        artistListView.setAdapter(mArtistsAdapter);

        // Search handling
        EditText artistSearch = (EditText) rootView.findViewById(R.id.edittext_artist_search);
        artistSearch.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_SEARCH)) {
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
                CustomArtist clickedArtist = mArtistsAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), TopTracksActivity.class)
                        .putExtra("artistId", clickedArtist.artistId)
                        .putExtra("artistName", clickedArtist.artistName);
                startActivity(intent);
            }
        });

        return rootView;
    }

    //
    public class SearchForArtistTask extends AsyncTask<String, Void, CustomArtist[]>
    {
        @Override
        protected CustomArtist[] doInBackground(String... params) {
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();

            CustomArtist[] resultsArtists;

            if (Utils.isNetworkAvailable(getActivity())) {
                try {
                    ArtistsPager artistsResults = spotify.searchArtists(params[0]);

                    List<Artist> artists = artistsResults.artists.items;

                    resultsArtists = new CustomArtist[artists.size()];

                    for (int i = 0; i < artists.size(); i++) {
                        Artist artist = artists.get(i);
                        String imageUrl = null;
                        if (artist.images.size() > 0) {
                            imageUrl = artist.images.get(0).url;
                        }
                        resultsArtists[i] = new CustomArtist(artist.name, artist.id, imageUrl);
                    }
                } catch (Error error) {

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), R.string.api_error, Toast.LENGTH_SHORT).show();
                        }
                    });

                    resultsArtists = null;
                }
            } else {
                resultsArtists = null;

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), R.string.no_network_available, Toast.LENGTH_SHORT).show();
                    }
                });

            }

            return resultsArtists;
        }

        @Override
        protected void onPostExecute(CustomArtist[] result) {
            if (result != null) {
                mArtistsAdapter.clear();
                if (result.length > 0) {
                    for (CustomArtist artistData : result) {
                        mArtistsAdapter.add(artistData);
                    }
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), R.string.no_results_for_artist, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
    }

}

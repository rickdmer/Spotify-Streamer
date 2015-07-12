package com.rickdmer.spotifystreamer;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by richard.carhart on 7/6/2015.
 */
// Using http://theopentutorials.com/tutorials/android/listview/android-custom-listview-with-image-and-text-using-arrayadapter/
public class ArtistListViewAdapter extends ArrayAdapter<CustomArtist> {

    Context context;

    public ArtistListViewAdapter(Context context, int resourceId, List<CustomArtist> artists) {
        super(context, resourceId, artists);
        this.context = context;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView artistName;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        CustomArtist artist = getItem(position);
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        // reuse view if already exists
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_artist, null);
            holder = new ViewHolder();
            holder.artistName = (TextView) convertView.findViewById(R.id.list_item_artist_textview);
            holder.imageView = (ImageView) convertView.findViewById(R.id.list_item_artist_imageview);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.artistName.setText(artist.artistName);
        if (artist.artistImageUrl != null) {
            Picasso.with(context).load(artist.artistImageUrl).into(holder.imageView);
        }

        return convertView;
    }
}
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
public class TrackListViewAdapter extends ArrayAdapter<CustomTrack> {

    Context context;

    public TrackListViewAdapter(Context context, int resourceId, List<CustomTrack> tracks) {
        super(context, resourceId, tracks);
        this.context = context;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView trackName;
        TextView albumTitle;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        CustomTrack track = getItem(position);
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        // reuse view if already exists
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_track, null);
            holder = new ViewHolder();
            holder.trackName = (TextView) convertView.findViewById(R.id.list_item_track_title);
            holder.albumTitle = (TextView) convertView.findViewById(R.id.list_item_track_album);
            holder.imageView = (ImageView) convertView.findViewById(R.id.list_item_track_imageview);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.trackName.setText(track.trackName);
        holder.albumTitle.setText(track.albumName);
        Picasso.with(context).load(track.albumImageUrl).into(holder.imageView);

        return convertView;
    }
}
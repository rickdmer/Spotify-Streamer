package com.rickdmer.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by richard.carhart on 7/12/2015.
 */
public class CustomTrack implements Parcelable {

    String trackName;
    String albumName;
    String albumImageUrl;

    public CustomTrack (String name, String album, String imageUrl) {
        trackName = name;
        albumName = album;
        albumImageUrl = imageUrl;
    }

    private CustomTrack (Parcel in) {
        trackName = in.readString();
        albumName = in.readString();
        albumImageUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(trackName);
        dest.writeString(albumName);
        dest.writeString(albumImageUrl);
    }

    public final Parcelable.Creator<CustomTrack> CREATOR = new Parcelable.Creator<CustomTrack>() {

        @Override
        public CustomTrack createFromParcel(Parcel source) {
            return new CustomTrack(source);
        }

        @Override
        public CustomTrack[] newArray(int size) {
            return new CustomTrack[size];
        }
    };
}

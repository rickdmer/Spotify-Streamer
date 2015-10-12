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
    String artistName;
    Long trackLength;
    String trackPreviewUrl;
    int trackPosition;

    public CustomTrack (String _trackName, String _albumName, String _albumImageUrl, String _artistName, Long _trackLength, String _trackPreviewUrl) {
        trackName = _trackName;
        albumName = _albumName;
        albumImageUrl = _albumImageUrl;
        artistName = _artistName;
        trackLength = _trackLength;
        trackPreviewUrl = _trackPreviewUrl;
    }

    private CustomTrack (Parcel in) {
        trackName = in.readString();
        albumName = in.readString();
        albumImageUrl = in.readString();
        artistName = in.readString();
        trackLength = in.readLong();
        trackPreviewUrl = in.readString();
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
        dest.writeString(artistName);
        dest.writeLong(trackLength);
        dest.writeString(trackPreviewUrl);
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

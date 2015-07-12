package com.rickdmer.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by richard.carhart on 7/12/2015.
 */
public class CustomArtist implements Parcelable {

    String artistName;
    String artistId;
    String artistImageUrl;

    public CustomArtist (String name, String id, String imageUrl) {
        this.artistName = name;
        this.artistId = id;
        this.artistImageUrl = imageUrl;
    }

    private CustomArtist (Parcel in) {
        artistName = in.readString();
        artistId = in.readString();
        artistImageUrl = in.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(artistName);
        dest.writeString(artistId);
        dest.writeString(artistImageUrl);
    }

    public final Parcelable.Creator<CustomArtist> CREATOR = new Parcelable.Creator<CustomArtist>() {

        @Override
        public CustomArtist createFromParcel(Parcel source) {
            return new CustomArtist(source);
        }

        @Override
        public CustomArtist[] newArray(int size) {
            return new CustomArtist[size];
        }
    };
}

package com.rookieandroid.rookiemusicplayer;

import android.os.Parcel;
import android.os.Parcelable;

public class Artists implements Parcelable
{
    private String artist;
    private String artistKey;

    public Artists(String artist_name, String artist_key)
    {
        artist = artist_name;
        artistKey = artist_key;
    }

    protected Artists(Parcel in)
    {
        artist = in.readString();
        artistKey = in.readString();
    }

    public static final Creator<Artists> CREATOR = new Creator<Artists>() {
        @Override
        public Artists createFromParcel(Parcel in) {
            return new Artists(in);
        }

        @Override
        public Artists[] newArray(int size) {
            return new Artists[size];
        }
    };

    public String getArtist() {return artist;}

    public String getArtistKey() {return artistKey;}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(artist);
        parcel.writeString(artistKey);
    }
}

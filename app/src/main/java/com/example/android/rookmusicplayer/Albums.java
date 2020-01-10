package com.example.android.rookmusicplayer;

import android.os.Parcel;
import android.os.Parcelable;

public class Albums implements Parcelable
{
    private String album;
    private String key;
    private String art;
    private String artist;

    public Albums(String album_name, String album_key, String album_art, String album_artist)
    {
        album = album_name;
        key = album_key;
        art = album_art;
        artist = album_artist;
    }

    protected Albums(Parcel in) {
        album = in.readString();
        key = in.readString();
        art = in.readString();
        artist = in.readString();
    }

    public static final Creator<Albums> CREATOR = new Creator<Albums>() {
        @Override
        public Albums createFromParcel(Parcel in) {
            return new Albums(in);
        }

        @Override
        public Albums[] newArray(int size) {
            return new Albums[size];
        }
    };

    public String getAlbum() {return album;}

    public String getKey() {return key;}

    public String getArt() {return art;}

    public String getArtist() {return artist;}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(album);
        parcel.writeString(key);
        parcel.writeString(art);
        parcel.writeString(artist);
    }
}

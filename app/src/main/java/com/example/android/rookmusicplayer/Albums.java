package com.example.android.rookmusicplayer;

import android.os.Parcel;
import android.os.Parcelable;

public class Albums implements Parcelable
{
    private String id;
    private String album;
    private String key;
    private String art;
    private String artist;

    public Albums(String id, String album, String key, String art, String artist)
    {
        this.id = id;
        this.album = album;
        this.key = key;
        this.art = art;
        this.artist = artist;
    }

    protected Albums(Parcel in)
    {
        id = in.readString();
        album = in.readString();
        key = in.readString();
        art = in.readString();
        artist = in.readString();
    }

    public static final Creator<Albums> CREATOR = new Creator<Albums>()
    {
        @Override
        public Albums createFromParcel(Parcel in) { return new Albums(in); }

        @Override
        public Albums[] newArray(int size) { return new Albums[size]; }
    };

    public String getId() {return id;}

    public String getAlbum() {return album;}

    public String getKey() {return key;}

    public String getArt() {return art;}

    public String getArtist() {return artist;}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(id);
        parcel.writeString(album);
        parcel.writeString(key);
        parcel.writeString(art);
        parcel.writeString(artist);
    }
}

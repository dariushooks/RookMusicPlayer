package com.rookieandroid.rookiemusicplayer;

import android.os.Parcelable;
import android.os.Parcel;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "saved_queue")
public class Songs implements Parcelable
{
    @PrimaryKey(autoGenerate = true)
    private int rowId;

    private String id;
    private String title;
    private String album;
    private String albumKey;
    private String art;
    private String artist;
    private String artistKey;
    private Long duration;
    private String path;
    private int track;

    public Songs(String id, String title, String album, String albumKey, String art, String artist, String artistKey, Long duration, String path, int track)
    {
        this.id = id;
        this.title = title;
        this.album = album;
        this.albumKey = albumKey;
        this.art = art;
        this.artist = artist;
        this.artistKey = artistKey;
        this.duration = duration;
        this.path = path;
        this.track = track;
    }

    //GETTERS
    public int getRowId() { return rowId; }

    public String getId() {return id;}

    public String getTitle() {return title;}

    public String getAlbum() {return album;}

    public String getAlbumKey() {return albumKey;}

    public String getArt() {return art;}

    public String getArtist() {return artist;}

    public String getArtistKey() {return artistKey;}

    public String getPath() {return path;}

    public Long getDuration() {return duration;}

    public int getTrack() {return track;}

    //SETTERS
    public void setRowId(int rowId) { this.rowId = rowId; }

    public void setId(String id) { this.id = id; }

    public void setTitle(String title) { this.title = title; }

    public void setAlbum(String album) { this.album = album; }

    public void setAlbumKey(String albumKey) { this.albumKey = albumKey; }

    public void setArt(String art) { this.art = art; }

    public void setArtist(String artist) { this.artist = artist; }

    public void setArtistKey(String artistKey) { this.artistKey = artistKey; }

    public void setDuration(Long duration) { this.duration = duration; }

    public void setPath(String path) { this.path = path; }

    public void setTrack(int track) { this.track = track; }

    protected Songs(Parcel in)
    {
        id = in.readString();
        title = in.readString();
        album = in.readString();
        albumKey = in.readString();
        art = in.readString();
        artist = in.readString();
        artistKey = in.readString();
        path = in.readString();
        if (in.readByte() == 0) {
            duration = null;
        } else {
            duration = in.readLong();
        }
        track = in.readInt();
    }

    public static final Creator<Songs> CREATOR = new Creator<Songs>() {
        @Override
        public Songs createFromParcel(Parcel in) {
            return new Songs(in);
        }

        @Override
        public Songs[] newArray(int size) {
            return new Songs[size];
        }
    };

    @Override
    public int describeContents() {return 0;}

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(album);
        parcel.writeString(albumKey);
        parcel.writeString(art);
        parcel.writeString(artist);
        parcel.writeString(artistKey);
        parcel.writeString(path);
        if (duration == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(duration);
        }
        parcel.writeInt(track);
    }
}

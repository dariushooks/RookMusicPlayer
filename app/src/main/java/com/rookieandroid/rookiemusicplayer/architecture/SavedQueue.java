package com.rookieandroid.rookiemusicplayer.architecture;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "saved")
public class SavedQueue
{
    @PrimaryKey(autoGenerate = true)
    private int queue_id;

    private String id;
    private String title;
    private String album;
    private String albumKey;
    private String art;
    private String artist;
    private String artistKey;
    private long duration;
    private String path;
    private int track;

    public SavedQueue(String id, String title, String album, String albumKey, String art, String artist, String artistKey, long duration, String path, int track)
    {
        this.id = id;
        this.title = title;
        this.album = album;
        this.albumKey = albumKey;
        this.art = art;
        this.artist = artist;
        this.artistKey = artistKey;
        this.album = album;
        this.duration = duration;
        this.path = path;
        this.track = track;
    }


    public int getQueue_id() { return queue_id;}

    public void setQueue_id(int queue_id) { this.queue_id = queue_id;}

    public String getId() { return id; }

    public void setId(String  id) { this.id = id; }

    public String getPath() { return path; }

    public void setPath(String path) { this.path = path; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getArtist() { return artist; }

    public void setArtist(String artist) { this.artist = artist; }

    public String getArtistKey() { return artistKey; }

    public void setArtistKey(String artistKey) { this.artistKey = artistKey; }

    public String getAlbum() { return album; }

    public void setAlbum(String album) { this.album = album; }

    public String getAlbumKey() { return albumKey; }

    public void setAlbumKey(String albumKey) { this.albumKey = albumKey; }

    public long getDuration() { return duration; }

    public void setDuration(long duration) { this.duration = duration; }

    public String getArt() { return art; }

    public void setArt(String art) { this.art = art; }

    public int getTrack() { return track; }

    public void setTrack(int track) { this.track = track; }
}

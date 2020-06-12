package com.rookieandroid.rookiemusicplayer;

public class Playlists
{
    private String playlist;
    private String id;
    private String description;

    public Playlists(String playlist, String id, String description)
    {
        this.playlist = playlist;
        this.id = id;
        this.description = description;
    }

    public String getPlaylist() {return playlist;}
    public String getId() {return id;}
    public String getDescription() {return description;}
}

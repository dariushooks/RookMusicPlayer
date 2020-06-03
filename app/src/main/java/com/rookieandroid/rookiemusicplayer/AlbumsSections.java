package com.rookieandroid.rookiemusicplayer;

import com.rookieandroid.rookiemusicplayer.adapters.AlbumsAdapter;

import java.util.ArrayList;

public class AlbumsSections
{
    private ArrayList<Albums> sectionedAlbums;
    private AlbumsAdapter albumsAdapter;

    public AlbumsSections(ArrayList<Albums> sectionedAlbums, AlbumsAdapter albumsAdapter)
    {
        this.sectionedAlbums = sectionedAlbums;
        this.albumsAdapter = albumsAdapter;
    }

    public ArrayList<Albums> getSectionedAlbums() { return sectionedAlbums; }
    public AlbumsAdapter getAlbumsAdapter() {return albumsAdapter;}
    public void setAlbumsAdapter(AlbumsAdapter albumsAdapter) { this.albumsAdapter = albumsAdapter;}
}

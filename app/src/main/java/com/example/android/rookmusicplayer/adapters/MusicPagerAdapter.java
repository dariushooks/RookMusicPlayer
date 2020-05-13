package com.example.android.rookmusicplayer.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.android.rookmusicplayer.Albums;
import com.example.android.rookmusicplayer.fragments.AlbumsFragment;
import com.example.android.rookmusicplayer.AlbumsSections;
import com.example.android.rookmusicplayer.Artists;
import com.example.android.rookmusicplayer.fragments.ArtistsFragment;
import com.example.android.rookmusicplayer.helpers.MediaControlDialog;
import com.example.android.rookmusicplayer.Playlists;
import com.example.android.rookmusicplayer.fragments.PlaylistsFragment;
import com.example.android.rookmusicplayer.Songs;
import com.example.android.rookmusicplayer.fragments.SongsFragment;

import java.util.ArrayList;

public class MusicPagerAdapter extends FragmentStatePagerAdapter
{
    private ArrayList<Songs> songs;
    private ArrayList<Artists> artists;
    private ArrayList<Albums> albums;
    private ArrayList<AlbumsSections> albumsSections;
    private ArrayList<Playlists> playlists;
    private MediaControlDialog.UpdateLibrary updateLibrary;
    private int from;

    /*public MusicPagerAdapter(@NonNull FragmentManager fm, ArrayList<Songs> songs, ArrayList<Artists> artists, ArrayList<AlbumsSections> albumsSections, ArrayList<Playlists> playlists, int from, MediaControlDialog.UpdateLibrary updateLibrary)
    {
        super(fm);
        this.songs = songs;
        this.artists = artists;
        this.albumsSections = albumsSections;
        this.playlists = playlists;
        this.from = from;
        this.updateLibrary = updateLibrary;
    }*/

    public MusicPagerAdapter(@NonNull FragmentManager fm, ArrayList<Songs> songs, ArrayList<Artists> artists, ArrayList<Albums> albums, ArrayList<Playlists> playlists, int from, MediaControlDialog.UpdateLibrary updateLibrary)
    {
        super(fm);
        this.songs = songs;
        this.artists = artists;
        this.albums = albums;
        this.playlists = playlists;
        this.from = from;
        this.updateLibrary = updateLibrary;
    }

    @NonNull
    @Override
    public Fragment getItem(int position)
    {
        switch (position)
        {
            case 0: return new SongsFragment(songs, from, updateLibrary);
            case 1: return new ArtistsFragment(artists, updateLibrary);
            case 2: return new AlbumsFragment(albums, -1, updateLibrary);
            case 3: return new PlaylistsFragment(playlists);
            default: return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position)
    {
        switch (position)
        {
            case 0: return "Songs";
            case 1: return "Artists";
            case 2: return "Albums";
            case 3: return "Playlists";
            default: return null;
        }
    }

    @Override
    public int getItemPosition(@NonNull Object object)
    {
        return POSITION_NONE;
    }

    @Override
    public int getCount() { return 4; }

}

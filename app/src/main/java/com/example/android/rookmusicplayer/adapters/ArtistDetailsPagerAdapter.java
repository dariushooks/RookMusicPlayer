package com.example.android.rookmusicplayer.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.android.rookmusicplayer.Albums;
import com.example.android.rookmusicplayer.fragments.AlbumsFragment;
import com.example.android.rookmusicplayer.helpers.MediaControlDialog;
import com.example.android.rookmusicplayer.Songs;
import com.example.android.rookmusicplayer.fragments.SongsFragment;

import java.util.ArrayList;

import static com.example.android.rookmusicplayer.App.ARTIST_DETAIL;

public class ArtistDetailsPagerAdapter extends FragmentStatePagerAdapter
{
    private ArrayList<Songs> songs;
    private ArrayList<Albums> albums;
    private int from;
    private MediaControlDialog.UpdateLibrary updateLibrary;

    public ArtistDetailsPagerAdapter(@NonNull FragmentManager fm, ArrayList<Songs> songs, ArrayList<Albums> albums, int from, MediaControlDialog.UpdateLibrary updateLibrary)
    {
        super(fm);
        this.songs = songs;
        this.albums = albums;
        this.from = from;
        this.updateLibrary = updateLibrary;
    }

    @NonNull
    @Override
    public Fragment getItem(int position)
    {
        switch (position)
        {
            case 0: return new SongsFragment(songs, from, ARTIST_DETAIL, updateLibrary);
            case 1: return new AlbumsFragment(albums, ARTIST_DETAIL, updateLibrary);
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
            case 1: return "Albums";
            default: return null;
        }
    }

    @Override
    public int getCount() { return 2; }
}

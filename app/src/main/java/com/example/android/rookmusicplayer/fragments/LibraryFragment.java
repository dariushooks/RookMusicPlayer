package com.example.android.rookmusicplayer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.transition.Fade;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.viewpager.widget.ViewPager;

import com.example.android.rookmusicplayer.Albums;
import com.example.android.rookmusicplayer.AlbumsSections;
import com.example.android.rookmusicplayer.App;
import com.example.android.rookmusicplayer.Artists;
import com.example.android.rookmusicplayer.helpers.GetMedia;
import com.example.android.rookmusicplayer.helpers.MediaControlDialog;
import com.example.android.rookmusicplayer.Playlists;
import com.example.android.rookmusicplayer.R;
import com.example.android.rookmusicplayer.helpers.SectionContent;
import com.example.android.rookmusicplayer.Songs;
import com.example.android.rookmusicplayer.adapters.MusicPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import static com.example.android.rookmusicplayer.App.FROM_LIBRARY;
import static com.example.android.rookmusicplayer.App.GET_ALBUM_SONGS;
import static com.example.android.rookmusicplayer.App.LIBRARY_MEDIA_LOADER;

public class LibraryFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList>
{
    private final String TAG = LibraryFragment.class.getSimpleName();

    private View rootView;
    private ViewPager viewPager;
    private Albums album;
    private ArrayList<Songs> songs;
    private ArrayList<Artists> artists;
    private ArrayList<AlbumsSections> albumsSections;
    private ArrayList<Playlists> playlists;
    private MusicPagerAdapter musicPagerAdapter;
    private ImageView searchView;
    private Query query;
    private MediaControlDialog.UpdateLibrary update;

    public LibraryFragment(ArrayList<Songs> songs, ArrayList<Artists> artists, ArrayList<AlbumsSections> albumsSections, ArrayList<Playlists> playlists, MediaControlDialog.UpdateLibrary update)
    {
        this.songs = songs;
        this.artists = artists;
        this.albumsSections = albumsSections;
        this.playlists = playlists;
        this.update = update;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setReenterTransition(new Fade().setStartDelay(500));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_library, container, false);

        viewPager = rootView.findViewById(R.id.viewpager);
        musicPagerAdapter = new MusicPagerAdapter(getChildFragmentManager(), songs, artists, albumsSections, playlists, FROM_LIBRARY, update);
        viewPager.setAdapter(musicPagerAdapter);
        TabLayout tabLayout = rootView.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        searchView = rootView.findViewById(R.id.searchButton);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { query.search(searchView); }
        });

        //Log.i(TAG, "FRAGMENT CURRENTLY VISIBLE: " + TAG);
        return rootView;
    }

    public interface Query { void search(ImageView sharedImage); }

    public void deleteSongFromLibrary(Songs song)
    {
        for(int i = 0; i < songs.size(); i++)
        {
            if(songs.get(i).getTitle().equals(song.getTitle()))
            {
                songs.remove(songs.get(i));
                App.songs.remove(songs.get(i));
            }
        }
    }

    public void deleteAlbumFromLibrary(Albums album, ArrayList<Songs> albumSongs)
    {
        SectionContent sectionContent = new SectionContent(album, albumsSections);
        AlbumsSections section = sectionContent.getSection();
        ArrayList<Albums> albums = section.getSectionedAlbums();
        for(int j = 0; j < albums.size(); j++)
        {
            if(albums.get(j).getAlbum().equals(album.getAlbum()))
            {
                albums.remove(albums.get(j));
                App.albums.remove(albums.get(j));
            }
        }

        for(int i = 0; i < albumSongs.size(); i++)
        {
            deleteSongFromLibrary(albumSongs.get(i));
        }

        musicPagerAdapter.notifyDataSetChanged();
    }

    public void deleteArtistFromLibrary(Artists artist, ArrayList<Albums> artistAlbums)
    {
        artists.remove(artist);
        App.artists.remove(artist);

        for(int i = 0; i < artistAlbums.size(); i++)
        {
            album = artistAlbums.get(i);
            LoaderManager.getInstance(this).initLoader(LIBRARY_MEDIA_LOADER, null, this);
        }

        musicPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);

        try { query = (Query) context; }
        catch (ClassCastException e) { throw new ClassCastException(context.toString().trim() + " must implement search"); }
    }

    @NonNull
    @Override
    public Loader<ArrayList> onCreateLoader(int id, @Nullable Bundle args)
    {
        return new GetMedia(getContext(), GET_ALBUM_SONGS, -1, album);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList> loader, ArrayList data)
    {
        deleteAlbumFromLibrary(album, data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList> loader) {}
}

package com.rookieandroid.rookiemusicplayer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.ViewPager;

import com.rookieandroid.rookiemusicplayer.Albums;
import com.rookieandroid.rookiemusicplayer.AlbumsSections;
import com.rookieandroid.rookiemusicplayer.App;
import com.rookieandroid.rookiemusicplayer.Artists;
import com.rookieandroid.rookiemusicplayer.architecture.LibraryViewModel;
import com.rookieandroid.rookiemusicplayer.helpers.MediaControlDialog;
import com.rookieandroid.rookiemusicplayer.Playlists;
import com.rookieandroid.rookiemusicplayer.R;
import com.rookieandroid.rookiemusicplayer.Songs;
import com.rookieandroid.rookiemusicplayer.adapters.MusicPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import static com.rookieandroid.rookiemusicplayer.App.FROM_ARTIST;
import static com.rookieandroid.rookiemusicplayer.App.FROM_LIBRARY;

public class LibraryFragment extends Fragment //implements LoaderManager.LoaderCallbacks<ArrayList>
{
    private final String TAG = LibraryFragment.class.getSimpleName();

    private View rootView;
    private ViewPager viewPager;
    private Albums album;
    private ArrayList<Songs> songs;
    private ArrayList<Artists> artists;
    private ArrayList<Albums> albums;
    private ArrayList<AlbumsSections> albumsSections;
    private ArrayList<Playlists> playlists;
    private MusicPagerAdapter musicPagerAdapter;
    private ImageView searchView;
    private Query query;
    private MediaControlDialog.UpdateLibrary update;
    private LibraryViewModel libraryViewModel;

    /*public LibraryFragment(ArrayList<Songs> songs, ArrayList<Artists> artists, ArrayList<AlbumsSections> albumsSections, ArrayList<Playlists> playlists, MediaControlDialog.UpdateLibrary update)
    {
        this.songs = songs;
        this.artists = artists;
        this.albumsSections = albumsSections;
        this.playlists = playlists;
        this.update = update;
    }*/

    public LibraryFragment(ArrayList<Songs> songs, ArrayList<Artists> artists, ArrayList<Albums> albums, ArrayList<Playlists> playlists, MediaControlDialog.UpdateLibrary update)
    {
        this.songs = songs;
        this.artists = artists;
        this.albums = albums;
        this.playlists = playlists;
        this.update = update;
    }

    public LibraryFragment(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setReenterTransition(new Fade().setStartDelay(500));
        //libraryViewModel = new ViewModelProvider(this).get(LibraryViewModel.class);
        //setLiveData();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_library, container, false);
        viewPager = rootView.findViewById(R.id.viewpager);
        musicPagerAdapter = new MusicPagerAdapter(getChildFragmentManager(), songs, artists, albums, playlists, FROM_LIBRARY, update);
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

    public void deleteAlbumFromLibrary(Albums album, ArrayList<Songs> albumSongs, int from)
    {
        /*SectionContent sectionContent = new SectionContent(album, albumsSections);
        AlbumsSections section = sectionContent.getSection();
        ArrayList<Albums> albums = section.getSectionedAlbums();
        for(int j = 0; j < albums.size(); j++)
        {
            if(albums.get(j).getAlbum().equals(album.getAlbum()))
            {
                albums.remove(albums.get(j));
                App.albums.remove(albums.get(j));
            }
        }*/
        if(from == FROM_ARTIST)
            for(int i = 0; i < albums.size(); i++)
            {
                if(albums.get(i).getAlbum().equals(album.getAlbum()))
                {
                    albums.remove(albums.get(i));
                    App.albums.remove(albums.get(i));
                }
            }

        for(int i = 0; i < albumSongs.size(); i++)
        {
            deleteSongFromLibrary(albumSongs.get(i));
        }

        musicPagerAdapter.notifyDataSetChanged();
    }

    public void deleteArtistFromLibrary(Artists artist, ArrayList<Songs> artistSongs)
    {
        /*for(int i = 0; i < artists.size(); i++)
        {
            if(artists.get(i).getArtist().equals(artist.getArtist()))
            {
                boolean r1 = artists.remove(artists.get(i));
                boolean r2 = App.artists.remove(artists.get(i));
            }
        }*/

        for(int i = 0; i < artistSongs.size(); i++)
        {
            deleteSongFromLibrary(artistSongs.get(i));
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

    private void setLiveData()
    {
        libraryViewModel.getSongs().observe(getViewLifecycleOwner(), new Observer<List<Songs>>() {
            @Override
            public void onChanged(List<Songs> songs)
            {
                LibraryFragment.this.songs = (ArrayList<Songs>) songs;
            }
        });

        libraryViewModel.getAlbums().observe(getViewLifecycleOwner(), new Observer<List<Albums>>() {
            @Override
            public void onChanged(List<Albums> albums)
            {
                LibraryFragment.this.albums = (ArrayList<Albums>) albums;
            }
        });

        libraryViewModel.getArtists().observe(getViewLifecycleOwner(), new Observer<List<Artists>>() {
            @Override
            public void onChanged(List<Artists> artists)
            {
                LibraryFragment.this.artists = (ArrayList<Artists>) artists;
            }
        });

        libraryViewModel.getPlaylists().observe(getViewLifecycleOwner(), new Observer<List<Playlists>>() {
            @Override
            public void onChanged(List<Playlists> playlists)
            {
                LibraryFragment.this.playlists = (ArrayList<Playlists>) playlists;
            }
        });
    }
}

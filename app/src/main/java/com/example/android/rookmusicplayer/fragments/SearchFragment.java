package com.example.android.rookmusicplayer.fragments;

import android.os.Bundle;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.transition.TransitionManager;

import com.example.android.rookmusicplayer.Albums;
import com.example.android.rookmusicplayer.App;
import com.example.android.rookmusicplayer.Artists;
import com.example.android.rookmusicplayer.R;
import com.example.android.rookmusicplayer.Songs;

import java.util.ArrayList;

import static com.example.android.rookmusicplayer.App.FROM_SEARCH;
import static com.example.android.rookmusicplayer.App.SEARCH;

public class SearchFragment extends Fragment implements View.OnClickListener
{
    private final String TAG = SearchFragment.class.getSimpleName();

    private final int CONTENT_SONGS = 0;
    private final int CONTENT_ARTISTS = 1;
    private final int CONTENT_ALBUMS = 2;
    private int selected = 0;
    private ArrayList<Songs> songs;
    private ArrayList<Artists> artists;
    private ArrayList<Albums> albums;
    private ArrayList<Songs> songsQuery = new ArrayList<>();
    private ArrayList<Artists> artistsQuery = new ArrayList<>();
    private ArrayList<Albums> albumsQuery = new ArrayList<>();
    private View rootView;
    private SearchView searchView;
    private Button contentSongs;
    private Button contentArtists;
    private Button contentAlbums;
    private ConstraintLayout constraintLayout;
    private ConstraintSet constraintSet = new ConstraintSet();
    private boolean contentEmpty = true;

    public SearchFragment(ArrayList<Songs> songs, ArrayList<Artists> artists, ArrayList<Albums> albums)
    {
        this.songs = songs;
        this.artists = artists;
        this.albums = albums;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setSharedElementEnterTransition(new App.DetailsTransition());
        setSharedElementReturnTransition(new App.DetailsTransition());
        setEnterTransition(new Fade().setStartDelay(500));
        setReturnTransition(new Fade());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_search, container, false);

        constraintLayout = rootView.findViewById(R.id.searchLayout);
        constraintSet.clone(constraintLayout);
        contentSongs = rootView.findViewById(R.id.contentSongs);
        contentSongs.setOnClickListener(this);
        contentArtists = rootView.findViewById(R.id.contentArtists);
        contentArtists.setOnClickListener(this);
        contentAlbums = rootView.findViewById(R.id.contentAlbums);
        contentAlbums.setOnClickListener(this);

        searchView = rootView.findViewById(R.id.search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s)
            {
                FragmentManager manager = getParentFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                getQuery(transaction, s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) { return false; }
        });

        //Log.i(TAG, "FRAGMENT CURRENTLY VISIBLE: " + TAG);
        return rootView;
    }



    @Override
    public void onClick(View view)
    {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        String currentQuery = searchView.getQuery().toString();
        switch (view.getId())
        {
            case R.id.contentSongs:
                selected = CONTENT_SONGS;
                searchView.setQueryHint("Search Library Songs");
                constraintSet.connect(R.id.contentSelected, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
                constraintSet.connect(R.id.contentSelected, ConstraintSet.END, R.id.contentArtists, ConstraintSet.START);
                TransitionManager.beginDelayedTransition(constraintLayout);
                constraintSet.applyTo(constraintLayout);
                contentSongs.setTextColor(getContext().getColor(R.color.colorAccent));
                contentArtists.setTextColor(getContext().getColor(R.color.white));
                contentAlbums.setTextColor(getContext().getColor(R.color.white));
                if(!currentQuery.equals(""))
                {
                    getQuery(transaction, currentQuery);
                }
                break;

            case R.id.contentArtists:
                selected = CONTENT_ARTISTS;
                searchView.setQueryHint("Search Library Artists");
                constraintSet.connect(R.id.contentSelected, ConstraintSet.START, R.id.contentSongs, ConstraintSet.END);
                constraintSet.connect(R.id.contentSelected, ConstraintSet.END, R.id.contentAlbums, ConstraintSet.START);
                TransitionManager.beginDelayedTransition(constraintLayout);
                constraintSet.applyTo(constraintLayout);
                contentSongs.setTextColor(getContext().getColor(R.color.white));
                contentArtists.setTextColor(getContext().getColor(R.color.colorAccent));
                contentAlbums.setTextColor(getContext().getColor(R.color.white));
                if(!currentQuery.equals(""))
                {
                    getQuery(transaction, currentQuery);
                }
                break;

            case R.id.contentAlbums:
                selected = CONTENT_ALBUMS;
                searchView.setQueryHint("Search Library Albums");
                constraintSet.connect(R.id.contentSelected, ConstraintSet.START, R.id.contentArtists, ConstraintSet.END);
                constraintSet.connect(R.id.contentSelected, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
                TransitionManager.beginDelayedTransition(constraintLayout);
                constraintSet.applyTo(constraintLayout);
                contentSongs.setTextColor(getContext().getColor(R.color.white));
                contentArtists.setTextColor(getContext().getColor(R.color.white));
                contentAlbums.setTextColor(getContext().getColor(R.color.colorAccent));
                if(!currentQuery.equals(""))
                {
                    getQuery(transaction, currentQuery);
                }
                break;
        }
    }


    private void getQuery(FragmentTransaction transaction, String query)
    {
        switch (selected)
        {
            case CONTENT_SONGS:
                songsQuery.clear();
                for(int i = 0; i < songs.size(); i++)
                {
                    if(songs.get(i).getTitle().toLowerCase().contains(query.toLowerCase()))
                    {
                        songsQuery.add(songs.get(i));
                    }
                }
                SongsFragment songsFragment = new SongsFragment(songsQuery, FROM_SEARCH, SEARCH, null);
                if(contentEmpty)
                    transaction.add(R.id.searchContent, songsFragment);
                else
                    transaction.replace(R.id.searchContent, songsFragment);
                break;

            case CONTENT_ARTISTS:
                artistsQuery.clear();
                for(int i = 0; i < artists.size(); i++)
                {
                    if(artists.get(i).getArtist().toLowerCase().contains(query.toLowerCase()))
                    {
                        artistsQuery.add(artists.get(i));
                    }
                }
                ArtistsFragment artistsFragment = new ArtistsFragment(artistsQuery, SEARCH, null);
                if(contentEmpty)
                    transaction.add(R.id.searchContent, artistsFragment);
                else
                    transaction.replace(R.id.searchContent, artistsFragment);
                break;

            case CONTENT_ALBUMS:
                albumsQuery.clear();
                for(int i = 0; i < albums.size(); i++)
                {
                    if(albums.get(i).getAlbum().toLowerCase().contains(query.toLowerCase()))
                    {
                        albumsQuery.add(albums.get(i));
                    }
                }
                AlbumsFragment albumsFragment = new AlbumsFragment(albumsQuery, SEARCH, null);
                if(contentEmpty)
                    transaction.add(R.id.searchContent, albumsFragment);
                else
                    transaction.replace(R.id.searchContent, albumsFragment);
                break;
        }
        contentEmpty = false;
        transaction.commit();
    }
}

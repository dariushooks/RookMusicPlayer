package com.example.android.rookmusicplayer.fragments;

import android.os.Bundle;
import android.transition.Fade;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.SharedElementCallback;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.viewpager.widget.ViewPager;

import com.example.android.rookmusicplayer.Albums;
import com.example.android.rookmusicplayer.App;
import com.example.android.rookmusicplayer.Artists;
import com.example.android.rookmusicplayer.helpers.GetMedia;
import com.example.android.rookmusicplayer.helpers.MediaControlDialog;
import com.example.android.rookmusicplayer.R;
import com.example.android.rookmusicplayer.Songs;
import com.example.android.rookmusicplayer.adapters.ArtistDetailsPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.android.rookmusicplayer.App.ARTIST_MEDIA_LOADER;
import static com.example.android.rookmusicplayer.App.FROM_ARTIST;
import static com.example.android.rookmusicplayer.App.GET_ARTIST_ALBUMS;
import static com.example.android.rookmusicplayer.App.GET_ARTIST_SONGS;

public class ArtistDetailsFragment extends Fragment
{
    private final String TAG = ArtistDetailsFragment.class.getSimpleName();

    private View rootView;
    private ArrayList<Songs> artistSongs;
    private ArrayList<Albums> artistAlbums;
    private Artists currentArtist;
    private ViewPager viewPager;
    private ArtistDetailsPagerAdapter pagerAdapter;
    private TextView artistName;
    private String artist;
    private int id;
    private MediaControlDialog.UpdateLibrary updateLibrary;

    public ArtistDetailsFragment(Artists currentArtist, MediaControlDialog.UpdateLibrary updateLibrary)
    {
        this.currentArtist = currentArtist;
        this.updateLibrary = updateLibrary;
    }

    public ArtistDetailsFragment(Songs currentArtist, MediaControlDialog.UpdateLibrary updateLibrary)
    {
        this.currentArtist = new Artists(currentArtist.getArtist(), currentArtist.getArtistKey());
        this.updateLibrary = updateLibrary;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setSharedElementEnterTransition(new App.DetailsTransition().setDuration(500));
        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements)
            {
                sharedElements.put(names.get(0), artistName);
            }
        });
        setSharedElementReturnTransition(new App.DetailsTransition().setDuration(500));
        setEnterTransition(new Fade());
        setReturnTransition(new Fade());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_artist_details, container, false);

        artist = currentArtist.getArtist();
        artistName = rootView.findViewById(R.id.artistDetailArtist);
        artistName.setText(artist);
        artistName.setTransitionName(currentArtist.getArtist());

        viewPager = rootView.findViewById(R.id.artistDetailViewPager);

        TabLayout tabLayout = rootView.findViewById(R.id.artistDetailTabs);
        tabLayout.setupWithViewPager(viewPager);


        LoaderManager.getInstance(this).initLoader(ARTIST_MEDIA_LOADER, null, songsCallbacks);
        //Log.i(TAG, "FRAGMENT CURRENTLY VISIBLE: " + TAG);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    private LoaderManager.LoaderCallbacks<ArrayList> songsCallbacks = new LoaderManager.LoaderCallbacks<ArrayList>()
    {
        @NonNull
        @Override
        public Loader<ArrayList> onCreateLoader(int id, @Nullable Bundle args)
        {
            return new GetMedia(getContext(), GET_ARTIST_SONGS, -1, currentArtist);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<ArrayList> loader, ArrayList data)
        {
            id = ((GetMedia) loader).getArtistId();
            artistSongs = data;
            LoaderManager.getInstance(ArtistDetailsFragment.this).restartLoader(ARTIST_MEDIA_LOADER, null, albumsCallbacks);
        }

        @Override
        public void onLoaderReset(@NonNull Loader<ArrayList> loader) {}
    };

    private LoaderManager.LoaderCallbacks<ArrayList> albumsCallbacks = new LoaderManager.LoaderCallbacks<ArrayList>()
    {
        @NonNull
        @Override
        public Loader<ArrayList> onCreateLoader(int id, @Nullable Bundle args)
        {
            return new GetMedia(getContext(), GET_ARTIST_ALBUMS, ArtistDetailsFragment.this.id);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<ArrayList> loader, ArrayList data)
        {
            artistAlbums = data;
            pagerAdapter = new ArtistDetailsPagerAdapter(getChildFragmentManager(), artistSongs, artistAlbums, FROM_ARTIST, updateLibrary);
            viewPager.setAdapter(pagerAdapter);
        }

        @Override
        public void onLoaderReset(@NonNull Loader<ArrayList> loader) {}
    };
}

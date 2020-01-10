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
import androidx.fragment.app.Fragment;
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

import static com.example.android.rookmusicplayer.App.FROM_ARTIST;

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

        setSharedElementEnterTransition(new App.DetailsTransition());
        setSharedElementReturnTransition(new App.DetailsTransition());
        setEnterTransition(new Fade().setStartDelay(500));
        setReturnTransition(new Fade());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_artist_details, container, false);
        GetMedia getMedia = new GetMedia(getContext(), id);
        artistSongs = getMedia.getArtistSongs(currentArtist);
        artistAlbums = getMedia.getArtistAlbums();
        artist = currentArtist.getArtist();

        viewPager = rootView.findViewById(R.id.artistDetailViewPager);

        pagerAdapter = new ArtistDetailsPagerAdapter(getChildFragmentManager(), artistSongs, artistAlbums, FROM_ARTIST, updateLibrary);
        viewPager.setAdapter(pagerAdapter);
        TabLayout tabLayout = rootView.findViewById(R.id.artistDetailTabs);
        tabLayout.setupWithViewPager(viewPager);

        artistName = rootView.findViewById(R.id.artistDetailArtist);
        artistName.setText(artist);

        Log.i(TAG, "FRAGMENT CURRENTLY VISIBLE: " + TAG);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        artistName.setTransitionName(currentArtist.getArtist());
    }

}

package com.example.android.rookmusicplayer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.rookmusicplayer.Artists;
import com.example.android.rookmusicplayer.helpers.IndexScroller;
import com.example.android.rookmusicplayer.helpers.MediaControlDialog;
import com.example.android.rookmusicplayer.R;
import com.example.android.rookmusicplayer.adapters.ArtistsAdapter;

import java.util.ArrayList;

import static com.example.android.rookmusicplayer.App.FROM_ARTIST;
import static com.example.android.rookmusicplayer.App.SEARCH;
import static com.example.android.rookmusicplayer.App.lettersArtists;
import static com.example.android.rookmusicplayer.App.sectionsArtists;

public class ArtistsFragment extends Fragment implements ArtistsAdapter.ListItemClickListener
{
    private View rootView;
    private ArtistsAdapter artistsAdapter;
    private RecyclerView recyclerView;
    private ArrayList<Artists> artists;
    private NowPlayingArtist nowPlayingArtist;
    private MediaControlDialog mediaControlDialog;
    private IndexScroller indexScroller;
    private int code;
    private MediaControlDialog.UpdateLibrary updateLibrary;

    public ArtistsFragment(ArrayList<Artists> artists, MediaControlDialog.UpdateLibrary updateLibrary)
    {
        this.artists = artists;
        this.updateLibrary = updateLibrary;
    }

    public ArtistsFragment(ArrayList<Artists> artists, int code, MediaControlDialog.UpdateLibrary updateLibrary)
    {
        this.artists = artists;
        this.code = code;
        this.updateLibrary = updateLibrary;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = getLayoutInflater().inflate(R.layout.fragment_artists, container, false);
        recyclerView = rootView.findViewById(R.id.artistsList);
        artistsAdapter = new ArtistsAdapter(artists, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(artistsAdapter);

        switch (code)
        {
            case SEARCH:
                rootView.findViewById(R.id.indexScroller).setVisibility(View.GONE);
                rootView.findViewById(R.id.indexDivider).setVisibility(View.GONE);
                break;

            default:
                indexScroller = new IndexScroller(getContext(), rootView, recyclerView, layoutManager, sectionsArtists, lettersArtists);
                indexScroller.setScrolling();
                break;
        }


        setExitTransition(new Fade());
        setReenterTransition(new Fade());
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onListItemClick(int position, TextView artist)
    {
        nowPlayingArtist.sendArtist(artists.get(position), artist, updateLibrary);
    }

    @Override
    public void onLongListItemClick(int position)
    {
        mediaControlDialog = new MediaControlDialog(getContext(), artists.get(position), artists, artistsAdapter, updateLibrary, FROM_ARTIST);
        mediaControlDialog.OpenDialog();
    }

    public interface NowPlayingArtist
    {
        void sendArtist(Artists artist, TextView sharedArtist, MediaControlDialog.UpdateLibrary updateLibrary);
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        try
        {
            nowPlayingArtist = (NowPlayingArtist) context;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString().trim() + " must implement sendArtist");
        }
    }
}

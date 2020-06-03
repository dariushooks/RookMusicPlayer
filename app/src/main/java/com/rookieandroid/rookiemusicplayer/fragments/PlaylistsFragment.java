package com.rookieandroid.rookiemusicplayer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rookieandroid.rookiemusicplayer.helpers.MediaControlDialog;
import com.rookieandroid.rookiemusicplayer.Playlists;
import com.rookieandroid.rookiemusicplayer.R;
import com.rookieandroid.rookiemusicplayer.adapters.PlaylistsAdapter;

import java.util.ArrayList;

import static com.rookieandroid.rookiemusicplayer.App.FROM_PLAYLIST;

public class PlaylistsFragment extends Fragment implements PlaylistsAdapter.ListItemClickListener, View.OnClickListener
{
    private ArrayList<Playlists> playlists;
    private View rootView;
    private RecyclerView recyclerView;
    private ConstraintLayout createNewPlaylist;
    private ConstraintLayout recentlyAdded;
    private MediaControlDialog mediaControlDialog;
    private NowPlayingPlaylist nowPlayingPlaylist;
    private PlaylistsAdapter playlistsAdapter;

    public PlaylistsFragment(ArrayList<Playlists> playlists){this.playlists = playlists;}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_playlist, container, false);
        createNewPlaylist = rootView.findViewById(R.id.createNew);
        createNewPlaylist.setOnClickListener(this);
        recentlyAdded = rootView.findViewById(R.id.recentlyAdded);
        recentlyAdded.setOnClickListener(this);
        recyclerView = rootView.findViewById(R.id.playlistRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        playlistsAdapter = new PlaylistsAdapter(playlists, this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(playlistsAdapter);
        return rootView;
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.createNew:
                mediaControlDialog = new MediaControlDialog(getContext(), null, playlists, playlistsAdapter, FROM_PLAYLIST);
                mediaControlDialog.OpenNewPlaylistDialog();
                break;

            case R.id.recentlyAdded:
                Playlists recentPlaylist = new Playlists("Recently Added", "", "");
                nowPlayingPlaylist.sendPlaylist(recentPlaylist);
                break;
        }
    }

    @Override
    public void onListItemClick(Playlists playlist)
    {
        nowPlayingPlaylist.sendPlaylist(playlist);
    }

    @Override
    public void onLongListItemClick(Playlists playlist)
    {
        mediaControlDialog = new MediaControlDialog(getContext(), playlist, playlists, playlistsAdapter, FROM_PLAYLIST);
        mediaControlDialog.OpenDialog();
    }

    public interface NowPlayingPlaylist
    {
        void sendPlaylist(Playlists playlist);
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        try
        {
            nowPlayingPlaylist = (NowPlayingPlaylist) context;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString().trim() + " must implement sendPlaylist");
        }
    }
}
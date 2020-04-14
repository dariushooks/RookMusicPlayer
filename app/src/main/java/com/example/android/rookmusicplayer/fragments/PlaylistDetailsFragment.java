package com.example.android.rookmusicplayer.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.rookmusicplayer.App;
import com.example.android.rookmusicplayer.helpers.GetMedia;
import com.example.android.rookmusicplayer.helpers.MediaControlDialog;
import com.example.android.rookmusicplayer.Playlists;
import com.example.android.rookmusicplayer.R;
import com.example.android.rookmusicplayer.Songs;
import com.example.android.rookmusicplayer.adapters.PlaylistsAdapter;
import com.example.android.rookmusicplayer.adapters.SongsAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.example.android.rookmusicplayer.App.CLEAR;
import static com.example.android.rookmusicplayer.App.FROM_PLAYLIST;
import static com.example.android.rookmusicplayer.App.RECENTLY_ADDED;
import static com.example.android.rookmusicplayer.App.SET_POSITION;
import static com.example.android.rookmusicplayer.App.SET_QUEUE_PLAYLIST;
import static com.example.android.rookmusicplayer.App.SET_UP_NEXT;
import static com.example.android.rookmusicplayer.App.SHUFFLE_QUEUE;
import static com.example.android.rookmusicplayer.App.mediaBrowserHelper;
import static com.example.android.rookmusicplayer.App.nowPlayingFrom;


public class PlaylistDetailsFragment extends Fragment implements SongsAdapter.ListItemClickListener, View.OnClickListener, MediaControlDialog.UpdatePlaylist
{
    private final String TAG = PlaylistDetailsFragment.class.getSimpleName();

    private View rootView;
    private Playlists currentPlaylist;
    private ArrayList<Songs> playlistSongs;
    private SongsAdapter playlistSongsAdapter;
    private boolean shuffled;
    private RecyclerView recyclerView;
    private TextView playlistName;
    private TextView playlistCount;
    private TextView playlistDescription;
    private Button playAlbum;
    private Button shuffleAlbum;
    private ImageView albumMediaControl;
    private MediaControlDialog mediaControlDialog;

    public PlaylistDetailsFragment(Playlists currentPlaylist) { this.currentPlaylist = currentPlaylist; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_playlist_details, container, false);
        GetMedia getMedia = new GetMedia(getContext());

        playlistName = rootView.findViewById(R.id.albumDetailName);
        playlistName.setText(currentPlaylist.getPlaylist());
        if(currentPlaylist.getPlaylist().equals("Recently Added"))
        {
            playlistSongs = getMedia.getRecentlyAddedSongs();
        }

        else
        {
            playlistSongs = getMedia.getPlaylistSongs(currentPlaylist);
        }

        playlistCount = rootView.findViewById(R.id.numberOfSongs);
        setPlaylistCount();

        rootView.findViewById(R.id.albumDetailArtist).setVisibility(View.GONE);

        playlistDescription = rootView.findViewById(R.id.playlistDescription);
        playlistDescription.setText(currentPlaylist.getDescription());
        if(!playlistDescription.getText().equals(""))
            playlistDescription.setVisibility(View.VISIBLE);

        playAlbum = rootView.findViewById(R.id.playAlbum);
        playAlbum.setOnClickListener(this);
        shuffleAlbum = rootView.findViewById(R.id.shuffleAlbum);
        shuffleAlbum.setOnClickListener(this);
        albumMediaControl = rootView.findViewById(R.id.albumMediaControl);
        albumMediaControl.setOnClickListener(this);

        recyclerView = rootView.findViewById(R.id.albumDetailSongs);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        playlistSongsAdapter = new SongsAdapter(playlistSongs, this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(playlistSongsAdapter);

        Log.i(TAG, "FRAGMENT CURRENTLY VISIBLE: " + TAG);
        return rootView;
    }

    private void setQueue(int position)
    {
        mediaBrowserHelper.getMediaController().getTransportControls().sendCustomAction(CLEAR, null);
        Bundle queue = new Bundle();
        queue.putInt("CURRENT_QUEUE_POSITION", position);
        queue.putInt("FROM", FROM_PLAYLIST);
        mediaBrowserHelper.getMediaController().getTransportControls().sendCustomAction(SET_POSITION, queue);
        mediaBrowserHelper.getMediaController().getTransportControls().sendCustomAction(SET_QUEUE_PLAYLIST, queue);
        mediaBrowserHelper.getMediaController().getTransportControls().playFromMediaId(playlistSongs.get(position).getPath(), null);
        mediaBrowserHelper.getMediaController().getTransportControls().sendCustomAction(SET_UP_NEXT, null);
        nowPlayingFrom = "Now playing from " + currentPlaylist.getPlaylist();
        mediaBrowserHelper.setBottomSheetQueue();
    }

    private void setPlaylistCount()
    {
        int pCount = playlistSongs.size();
        String count;
        if(pCount == 1)
        {
            count = pCount + " song";
            playlistCount.setText(count);
        }

        else
        {
            count = pCount + " songs";
            playlistCount.setText(count);
        }
    }

    @Override
    public void onClick(View view)
    {
        Bundle from = new Bundle();
        switch (view.getId())
        {
            case R.id.playAlbum:
                //if(shuffled)
                    //Collections.sort(playlistSongs, Comparator.comparing(Songs::getTrack));
                //shuffled = false;
                Collections.sort(playlistSongs, Comparator.comparing(Songs::getTrack));
                App.playlistSongs = playlistSongs;
                setQueue(0);
                break;

            case R.id.shuffleAlbum:
                Collections.shuffle(playlistSongs);
                //shuffled = true;
                App.playlistSongs = playlistSongs;
                setQueue(0);
                if(currentPlaylist.getPlaylist().equals("Recently Added"))
                    from.putInt("FROM", RECENTLY_ADDED);
                else
                    from.putInt("FROM", FROM_PLAYLIST);
                mediaBrowserHelper.getMediaController().getTransportControls().sendCustomAction(SHUFFLE_QUEUE, from);
                break;

            case R.id.albumMediaControl:
                ArrayList<Playlists> playlists = null;
                PlaylistsAdapter playlistsAdapter = null;
                mediaControlDialog = new MediaControlDialog(getContext(), currentPlaylist, playlists, playlistsAdapter, FROM_PLAYLIST);
                mediaControlDialog.OpenDialog();
                break;
        }
    }

    @Override
    public void onListItemClick(int position)
    {
        Collections.sort(playlistSongs, Comparator.comparing(Songs::getTitle));
        App.playlistSongs = playlistSongs;
        setQueue(position);
    }

    @Override
    public void onLongListItemClick(int position)
    {
        if(!currentPlaylist.getPlaylist().equals("Recently Added"))
        {
            mediaControlDialog = new MediaControlDialog(getContext(), playlistSongs.get(position), currentPlaylist, playlistSongs, playlistSongsAdapter, this, FROM_PLAYLIST);
            mediaControlDialog.OpenDialog();
        }
    }

    @Override
    public void updatePlaylistCount()
    {
        setPlaylistCount();
    }
}

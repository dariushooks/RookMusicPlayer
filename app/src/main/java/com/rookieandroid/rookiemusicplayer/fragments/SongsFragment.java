package com.rookieandroid.rookiemusicplayer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rookieandroid.rookiemusicplayer.helpers.IndexScroller;
import com.rookieandroid.rookiemusicplayer.helpers.MediaControlDialog;
import com.rookieandroid.rookiemusicplayer.R;
import com.rookieandroid.rookiemusicplayer.Songs;
import com.rookieandroid.rookiemusicplayer.adapters.SongsAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.rookieandroid.rookiemusicplayer.App.ARTIST_DETAIL;
import static com.rookieandroid.rookiemusicplayer.App.CLEAR;
import static com.rookieandroid.rookiemusicplayer.App.FROM_ARTIST;
import static com.rookieandroid.rookiemusicplayer.App.FROM_LIBRARY;
import static com.rookieandroid.rookiemusicplayer.App.SEARCH;
import static com.rookieandroid.rookiemusicplayer.App.SET_POSITION;
import static com.rookieandroid.rookiemusicplayer.App.SET_QUEUE_LIBRARY_SONGS;
import static com.rookieandroid.rookiemusicplayer.App.SET_UP_NEXT;
import static com.rookieandroid.rookiemusicplayer.App.SHUFFLE_QUEUE;
import static com.rookieandroid.rookiemusicplayer.App.lettersSongs;
import static com.rookieandroid.rookiemusicplayer.App.librarySongs;
import static com.rookieandroid.rookiemusicplayer.App.mediaBrowserHelper;
import static com.rookieandroid.rookiemusicplayer.App.nowPlayingFrom;
import static com.rookieandroid.rookiemusicplayer.App.sectionsSongs;

public class SongsFragment extends Fragment implements SongsAdapter.ListItemClickListener, View.OnClickListener
{

    private View rootView;
    private SongsAdapter songsAdapter;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private ArrayList<Songs> songs;
    private NowPlayingLibrary nowPlaying;
    private MediaControlDialog mediaControlDialog;
    private int from;
    private IndexScroller indexScroller;;
    private Button playSongs;
    private Button shuffleSongs;
    private int code;
    private MediaControlDialog.UpdateLibrary updateLibrary;

    public SongsFragment(ArrayList<Songs> songs, int from, MediaControlDialog.UpdateLibrary updateLibrary)
    {
        this.songs = songs;
        this.from = from;
        this.updateLibrary = updateLibrary;
    }

    public SongsFragment(ArrayList<Songs> songs, int from, int code, MediaControlDialog.UpdateLibrary updateLibrary)
    {
        this.songs = songs;
        this.from = from;
        this.code = code;
        this.updateLibrary = updateLibrary;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_songs, container, false);
        recyclerView = rootView.findViewById(R.id.songsList);
        songsAdapter = new SongsAdapter(songs, this);
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setInitialPrefetchItemCount(songs.size());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(songsAdapter);
        playSongs = rootView.findViewById(R.id.playSongs);
        playSongs.setOnClickListener(this);
        shuffleSongs = rootView.findViewById(R.id.shuffleSongs);
        shuffleSongs.setOnClickListener(this);

        switch (code)
        {
            case ARTIST_DETAIL:
                rootView.findViewById(R.id.indexScroller).setVisibility(View.GONE);
                rootView.findViewById(R.id.indexDivider).setVisibility(View.GONE);
                break;

            case SEARCH:
                rootView.findViewById(R.id.indexScroller).setVisibility(View.GONE);
                rootView.findViewById(R.id.indexDivider).setVisibility(View.GONE);
                rootView.findViewById(R.id.appbar).setVisibility(View.GONE);
                rootView.findViewById(R.id.divider).setVisibility(View.GONE);
                break;

            default:
                indexScroller = new IndexScroller(getContext(), rootView, recyclerView, layoutManager, sectionsSongs, lettersSongs);
                indexScroller.setScrolling();
                break;
        }

        return rootView;
    }

    private void setQueue(int position)
    {

        mediaBrowserHelper.getMediaController().getTransportControls().sendCustomAction(CLEAR, null);
        Bundle queue = new Bundle();
        queue.putInt("CURRENT_QUEUE_POSITION", position);
        queue.putInt("FROM", from);
        mediaBrowserHelper.getMediaController().getTransportControls().sendCustomAction(SET_POSITION, queue);
        mediaBrowserHelper.getMediaController().getTransportControls().sendCustomAction(SET_QUEUE_LIBRARY_SONGS, null);
        mediaBrowserHelper.getMediaController().getTransportControls().playFromMediaId(songs.get(position).getPath(), null);
        mediaBrowserHelper.getMediaController().getTransportControls().sendCustomAction(SET_UP_NEXT, null);
        switch (from)
        {
            case FROM_LIBRARY: nowPlayingFrom = "Now playing from Library"; break;
            case FROM_ARTIST: nowPlayingFrom = "Now playing from " + songs.get(position).getArtist(); break;
        }
        mediaBrowserHelper.setBottomSheetQueue();
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.playSongs:
                Collections.sort(songs, Comparator.comparing(Songs::getTitle));
                librarySongs = songs;
                setQueue(0);
                break;

            case R.id.shuffleSongs:
                Collections.shuffle(songs);
                librarySongs = songs;
                //Bundle bundle = new Bundle();
                //bundle.putInt("FROM", from);
                mediaBrowserHelper.getMediaController().getTransportControls().sendCustomAction(SHUFFLE_QUEUE, null);
                setQueue(0);
                break;
        }
    }

    @Override
    public void onListItemClick(int position) { nowPlaying.sendSong(songs, position, from); }

    @Override
    public void onLongListItemClick(int position)
    {
        if(code != SEARCH)
        {
            mediaControlDialog = new MediaControlDialog(getContext(), songs.get(position), songs, songsAdapter, updateLibrary, from);
            mediaControlDialog.OpenDialog();
        }

    }

    public interface NowPlayingLibrary
    {
        void sendSong(ArrayList<Songs> songs, int index, int from);
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        try { nowPlaying = (NowPlayingLibrary) context; }
        catch (ClassCastException e) { throw new ClassCastException(context.toString().trim() + " must implement sendSong"); }
    }
}

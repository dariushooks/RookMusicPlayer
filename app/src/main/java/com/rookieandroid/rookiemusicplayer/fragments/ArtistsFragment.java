package com.rookieandroid.rookiemusicplayer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.SharedElementCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rookieandroid.rookiemusicplayer.Artists;
import com.rookieandroid.rookiemusicplayer.helpers.IndexScroller;
import com.rookieandroid.rookiemusicplayer.helpers.MediaControlDialog;
import com.rookieandroid.rookiemusicplayer.R;
import com.rookieandroid.rookiemusicplayer.adapters.ArtistsAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.rookieandroid.rookiemusicplayer.App.FROM_ARTIST;
import static com.rookieandroid.rookiemusicplayer.App.SEARCH;
import static com.rookieandroid.rookiemusicplayer.App.lettersArtists;
import static com.rookieandroid.rookiemusicplayer.App.sectionsArtists;

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
    private int position;
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setExitSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements)
            {
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
                if(viewHolder == null)
                    return;
                sharedElements.put(names.get(0), viewHolder.itemView.findViewById(R.id.artistName));
            }
        });
        setExitTransition(new Fade());
        setReenterTransition(new Fade());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = getLayoutInflater().inflate(R.layout.fragment_artists, container, false);
        recyclerView = rootView.findViewById(R.id.artistsList);
        artistsAdapter = new ArtistsAdapter(artists, this, this);
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
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onListItemClick(int position, TextView artist)
    {
        this.position = position;
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

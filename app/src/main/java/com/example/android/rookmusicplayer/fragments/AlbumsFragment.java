package com.example.android.rookmusicplayer.fragments;

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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.rookmusicplayer.Albums;
import com.example.android.rookmusicplayer.AlbumsSections;
import com.example.android.rookmusicplayer.helpers.IndexScroller;
import com.example.android.rookmusicplayer.helpers.MediaControlDialog;
import com.example.android.rookmusicplayer.R;
import com.example.android.rookmusicplayer.helpers.SectionContent;
import com.example.android.rookmusicplayer.adapters.AlbumsAdapter;
import com.example.android.rookmusicplayer.adapters.AlbumsSectionsAdapter;

import java.util.ArrayList;

import static com.example.android.rookmusicplayer.App.ARTIST_DETAIL;
import static com.example.android.rookmusicplayer.App.FROM_ARTIST;
import static com.example.android.rookmusicplayer.App.FROM_LIBRARY;
import static com.example.android.rookmusicplayer.App.FROM_SEARCH;
import static com.example.android.rookmusicplayer.App.SEARCH;
import static com.example.android.rookmusicplayer.App.lettersAlbums;
import static com.example.android.rookmusicplayer.App.sectionsAlbums;

public class AlbumsFragment extends Fragment implements AlbumsAdapter.ListItemClickListener
{
    private View rootView;
    private AlbumsSectionsAdapter albumsSectionsAdapter;
    private AlbumsAdapter albumsAdapter;
    private RecyclerView recyclerView;
    private ArrayList<AlbumsSections> albumsSections;
    private ArrayList<Albums> albums;
    private NowPlayingAlbum nowPlayingAlbum;
    private MediaControlDialog mediaControlDialog;
    private IndexScroller indexScroller;
    private int code;
    private int from;
    private MediaControlDialog.UpdateLibrary updateLibrary;

    public AlbumsFragment(ArrayList<AlbumsSections> albumsSections, MediaControlDialog.UpdateLibrary updateLibrary)
    {
        this.albumsSections = albumsSections;
        this.updateLibrary = updateLibrary;
    }

    public AlbumsFragment(ArrayList<Albums> albums, int code, MediaControlDialog.UpdateLibrary updateLibrary)
    {
        this.albums = albums;
        this.code = code;
        this.updateLibrary = updateLibrary;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = getLayoutInflater().inflate(R.layout.fragment_albums, container, false);
        recyclerView = rootView.findViewById(R.id.albumSections);

        switch (code)
        {
            case ARTIST_DETAIL:
                albumsAdapter = new AlbumsAdapter(albums, this);
                GridLayoutManager gridlayoutManager1 = new GridLayoutManager(getContext(), 2);
                recyclerView.setLayoutManager(gridlayoutManager1);
                recyclerView.setAdapter(albumsAdapter);
                rootView.findViewById(R.id.indexScroller).setVisibility(View.GONE);
                rootView.findViewById(R.id.indexDivider).setVisibility(View.GONE);
                from = FROM_ARTIST;
                break;

            case SEARCH:
                albumsAdapter = new AlbumsAdapter(albums, this);
                GridLayoutManager gridlayoutManager2 = new GridLayoutManager(getContext(), 2);
                recyclerView.setLayoutManager(gridlayoutManager2);
                recyclerView.setAdapter(albumsAdapter);
                rootView.findViewById(R.id.indexScroller).setVisibility(View.GONE);
                rootView.findViewById(R.id.indexDivider).setVisibility(View.GONE);
                from = FROM_SEARCH;
                break;

            default:
                albumsSectionsAdapter = new AlbumsSectionsAdapter(albumsSections,this);
                LinearLayoutManager linearlayoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(linearlayoutManager);
                recyclerView.setAdapter(albumsSectionsAdapter);
                indexScroller = new IndexScroller(getContext(), rootView, recyclerView, linearlayoutManager, sectionsAlbums, lettersAlbums);
                indexScroller.setScrolling();
                from = FROM_LIBRARY;
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
    public void onListItemClick(Albums album, ImageView art)
    {
        nowPlayingAlbum.sendAlbum(album, art, from, updateLibrary);
    }

    @Override
    public void onLongItemClick(Albums album)
    {
        if(code != SEARCH)
        {
            if(code == ARTIST_DETAIL)
            {
                mediaControlDialog = new MediaControlDialog(getContext(), album, albums, albumsAdapter, updateLibrary, FROM_ARTIST);
                mediaControlDialog.OpenDialog();
            }

            else
            {
                SectionContent sectionContent = new SectionContent(album, albumsSections);
                AlbumsSections section = sectionContent.getSection();
                albums = section.getSectionedAlbums();
                albumsAdapter = section.getAlbumsAdapter();
                mediaControlDialog = new MediaControlDialog(getContext(), album, albums, albumsAdapter, updateLibrary, FROM_LIBRARY);
                mediaControlDialog.OpenDialog();
            }
        }

    }

    public interface NowPlayingAlbum
    {
        void sendAlbum(Albums album, ImageView sharedArt, int from, MediaControlDialog.UpdateLibrary updateLibrary);
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        try
        {
            nowPlayingAlbum = (NowPlayingAlbum) context;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString().trim() + " must implement sendAlbum");
        }
    }
}

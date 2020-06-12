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
import androidx.core.app.SharedElementCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rookieandroid.rookiemusicplayer.Albums;
import com.rookieandroid.rookiemusicplayer.AlbumsSections;
import com.rookieandroid.rookiemusicplayer.helpers.IndexScroller;
import com.rookieandroid.rookiemusicplayer.helpers.MediaControlDialog;
import com.rookieandroid.rookiemusicplayer.R;
import com.rookieandroid.rookiemusicplayer.adapters.AlbumsAdapter;
import com.rookieandroid.rookiemusicplayer.adapters.AlbumsSectionsAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.rookieandroid.rookiemusicplayer.App.ARTIST_DETAIL;
import static com.rookieandroid.rookiemusicplayer.App.FROM_ARTIST;
import static com.rookieandroid.rookiemusicplayer.App.FROM_LIBRARY;
import static com.rookieandroid.rookiemusicplayer.App.FROM_SEARCH;
import static com.rookieandroid.rookiemusicplayer.App.SEARCH;
import static com.rookieandroid.rookiemusicplayer.App.lettersAlbums;
import static com.rookieandroid.rookiemusicplayer.App.sectionsAlbums;

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
    private int position;
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //setSharedElementEnterTransition(new App.DetailsTransition().setDuration(500));
        //setSharedElementReturnTransition(new App.DetailsTransition());
        setExitSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements)
            {
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
                if(viewHolder == null)
                    return;
                sharedElements.put(names.get(0), viewHolder.itemView.findViewById(R.id.gridArt));
            }
        });
        setExitTransition(new Fade());
        setReenterTransition(new Fade().setStartDelay(500));
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
                albumsAdapter = new AlbumsAdapter(albums, this, this);
                GridLayoutManager gridlayoutManager1 = new GridLayoutManager(getContext(), 2);
                recyclerView.setLayoutManager(gridlayoutManager1);
                recyclerView.setAdapter(albumsAdapter);
                rootView.findViewById(R.id.indexScroller).setVisibility(View.GONE);
                rootView.findViewById(R.id.indexLetter).setVisibility(View.GONE);
                from = FROM_ARTIST;
                break;

            case SEARCH:
                albumsAdapter = new AlbumsAdapter(albums, this, this);
                GridLayoutManager gridlayoutManager2 = new GridLayoutManager(getContext(), 2);
                recyclerView.setLayoutManager(gridlayoutManager2);
                recyclerView.setAdapter(albumsAdapter);
                rootView.findViewById(R.id.indexScroller).setVisibility(View.GONE);
                rootView.findViewById(R.id.indexLetter).setVisibility(View.GONE);
                from = FROM_SEARCH;
                break;

            default:
                albumsAdapter = new AlbumsAdapter(albums, this, this);
                //albumsSectionsAdapter = new AlbumsSectionsAdapter(albumsSections,this);
                //LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
                layoutManager.setInitialPrefetchItemCount(albums.size());
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(albumsAdapter);
                //rootView.findViewById(R.id.indexLetter).setVisibility(View.GONE);
                indexScroller = new IndexScroller(getContext(), rootView, recyclerView, layoutManager, sectionsAlbums, lettersAlbums);
                indexScroller.setScrolling();
                from = FROM_LIBRARY;
                break;
        }

        postponeEnterTransition();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        /*recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom)
            {
                recyclerView.removeOnLayoutChangeListener(this);
                final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                View viewAtPosition = layoutManager.findViewByPosition(position);
                // Scroll to position if the view for the current position is null (not currently part of
                // layout manager children), or it's not completely visible.
                if (viewAtPosition == null || layoutManager.isViewPartiallyVisible(viewAtPosition, false, true))
                {
                    recyclerView.post(() -> layoutManager.scrollToPosition(position));
                }
            }
        });*/
    }

    @Override
    public void onListItemClick(Albums album, ImageView art, int position)
    {
        this.position = position;
        nowPlayingAlbum.sendAlbum(album, art, from, updateLibrary, position);
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
                /*SectionContent sectionContent = new SectionContent(album, albumsSections);
                AlbumsSections section = sectionContent.getSection();
                albums = section.getSectionedAlbums();
                albumsAdapter = section.getAlbumsAdapter();*/
                mediaControlDialog = new MediaControlDialog(getContext(), album, albums, albumsAdapter, updateLibrary, FROM_LIBRARY);
                mediaControlDialog.OpenDialog();
            }
        }

    }

    public interface NowPlayingAlbum
    {
        void sendAlbum(Albums album, ImageView sharedArt, int from, MediaControlDialog.UpdateLibrary updateLibrary, int position);
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

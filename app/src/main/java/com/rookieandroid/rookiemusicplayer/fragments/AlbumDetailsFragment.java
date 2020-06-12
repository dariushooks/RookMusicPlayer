package com.rookieandroid.rookiemusicplayer.fragments;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.SharedElementCallback;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.rookieandroid.rookiemusicplayer.Albums;
import com.rookieandroid.rookiemusicplayer.App;
import com.rookieandroid.rookiemusicplayer.helpers.GetMedia;
import com.rookieandroid.rookiemusicplayer.helpers.MediaControlDialog;
import com.rookieandroid.rookiemusicplayer.R;
import com.rookieandroid.rookiemusicplayer.Songs;
import com.rookieandroid.rookiemusicplayer.adapters.AlbumDetailsAdapter;
import com.rookieandroid.rookiemusicplayer.adapters.AlbumsAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.rookieandroid.rookiemusicplayer.App.ALBUM_MEDIA_LOADER;
import static com.rookieandroid.rookiemusicplayer.App.CLEAR;
import static com.rookieandroid.rookiemusicplayer.App.FROM_ALBUM;
import static com.rookieandroid.rookiemusicplayer.App.GET_ALBUM_SONGS;
import static com.rookieandroid.rookiemusicplayer.App.SET_POSITION;
import static com.rookieandroid.rookiemusicplayer.App.SET_QUEUE_ALBUM;
import static com.rookieandroid.rookiemusicplayer.App.SET_UP_NEXT;
import static com.rookieandroid.rookiemusicplayer.App.SHUFFLE_QUEUE;
import static com.rookieandroid.rookiemusicplayer.App.mediaBrowserHelper;
import static com.rookieandroid.rookiemusicplayer.App.nowPlayingFrom;

public class AlbumDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList>, AlbumDetailsAdapter.ListItemClickListener, View.OnClickListener
{
    private final String TAG = AlbumDetailsFragment.class.getSimpleName();

    private View rootView;
    private ArrayList<Songs> albumSongs;
    private Albums currentAlbum;
    private int position;
    private AlbumDetailsAdapter albumDetailsAdapter;
    private RecyclerView recyclerView;
    private String album;
    private String artist;
    private String art;

    private TextView albumName;
    private TextView albumArtist;
    private ImageView albumArt;
    private TextView numberOfSongs;
    private Button playAlbum;
    private Button shuffleAlbum;
    private ImageView albumMediaControl;
    private MediaControlDialog mediaControlDialog;
    private MediaControlDialog.UpdateLibrary updateLibrary;

    public AlbumDetailsFragment(Albums currentAlbum, MediaControlDialog.UpdateLibrary updateLibrary, int position)
    {
        this.currentAlbum = currentAlbum;
        this.updateLibrary = updateLibrary;
        this.position = position;
    }

    public AlbumDetailsFragment(Songs currentAlbum, MediaControlDialog.UpdateLibrary updateLibrary)
    {
        this.currentAlbum = new Albums("-1", currentAlbum.getAlbum(), currentAlbum.getAlbumKey(), currentAlbum.getArt(), currentAlbum.getArtist());
        this.updateLibrary = updateLibrary;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setSharedElementEnterTransition(new App.DetailsTransition().setDuration(500));
        setSharedElementReturnTransition(new App.DetailsTransition().setDuration(500));
        setEnterTransition(new Fade().setStartDelay(500));
        setReturnTransition(new Fade().excludeTarget(albumArt, true));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_album_details, container, false);

        album = currentAlbum.getAlbum();
        artist = currentAlbum.getArtist();
        art = currentAlbum.getArt();

        albumName = rootView.findViewById(R.id.albumDetailName);
        albumName.setText(album);

        albumArtist = rootView.findViewById(R.id.albumDetailArtist);
        albumArtist.setText(artist);

        albumArt = rootView.findViewById(R.id.albumDetailArt);
        albumArt.setTransitionName(currentAlbum.getAlbum());
        Glide.with(this).load(Uri.parse(art)).dontAnimate().dontTransform().fallback(R.drawable.noalbumart).error(R.drawable.noalbumart)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource)
                    {
                        startPostponedEnterTransition();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource)
                    {
                        startPostponedEnterTransition();
                        return false;
                    }
                }).into(albumArt);

        recyclerView = rootView.findViewById(R.id.albumDetailSongs);
        numberOfSongs = rootView.findViewById(R.id.numberOfSongs);

        playAlbum = rootView.findViewById(R.id.playAlbum);
        playAlbum.setOnClickListener(this);
        shuffleAlbum = rootView.findViewById(R.id.shuffleAlbum);
        shuffleAlbum.setOnClickListener(this);
        albumMediaControl = rootView.findViewById(R.id.albumMediaControl);
        albumMediaControl.setOnClickListener(this);

        //Log.i(TAG, "FRAGMENT CURRENTLY VISIBLE: " + TAG);
        LoaderManager.getInstance(this).initLoader(ALBUM_MEDIA_LOADER, null, this);
        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements)
            {
                sharedElements.put(names.get(0), albumArt);
            }
        });
        postponeEnterTransition();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    private void setQueue(int position)
    {
        mediaBrowserHelper.getMediaController().getTransportControls().sendCustomAction(CLEAR, null);
        Bundle queue = new Bundle();
        queue.putInt("CURRENT_QUEUE_POSITION", position);
        queue.putInt("FROM", FROM_ALBUM);
        mediaBrowserHelper.getMediaController().getTransportControls().sendCustomAction(SET_POSITION, queue);
        mediaBrowserHelper.getMediaController().getTransportControls().sendCustomAction(SET_QUEUE_ALBUM, queue);
        mediaBrowserHelper.getMediaController().getTransportControls().playFromMediaId(albumSongs.get(position).getId(), null);
        mediaBrowserHelper.getMediaController().getTransportControls().sendCustomAction(SET_UP_NEXT, null);
        nowPlayingFrom = "Now playing from " + album;
        mediaBrowserHelper.setBottomSheetQueue();
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.playAlbum:
                Collections.sort(albumSongs, Comparator.comparing(Songs::getTrack));
                App.albumSongs = albumSongs;
                setQueue(0);
                break;

            case R.id.shuffleAlbum:
                Collections.shuffle(albumSongs);
                App.albumSongs = albumSongs;
                mediaBrowserHelper.getMediaController().getTransportControls().sendCustomAction(SHUFFLE_QUEUE, null);
                setQueue(0);
                break;

            case R.id.albumMediaControl:
                ArrayList<Albums> albums = null;
                AlbumsAdapter albumsAdapter = null;
                mediaControlDialog = new MediaControlDialog(getContext(), currentAlbum, albums, albumsAdapter, updateLibrary, FROM_ALBUM);
                mediaControlDialog.OpenDialog();
                break;
        }
    }

    @Override
    public void onListItemClick(int position)
    {
        Collections.sort(albumSongs, Comparator.comparing(Songs::getTrack));
        App.albumSongs = albumSongs;
        setQueue(position);
    }

    @Override
    public void onLongListItemClick(int position)
    {
        mediaControlDialog = new MediaControlDialog(getContext(), albumSongs.get(position), albumSongs, albumDetailsAdapter, updateLibrary, FROM_ALBUM);
        mediaControlDialog.OpenDialog();
    }

    @NonNull
    @Override
    public Loader<ArrayList> onCreateLoader(int id, @Nullable Bundle args)
    {
        return new GetMedia(getContext(), GET_ALBUM_SONGS, -1, currentAlbum);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList> loader, ArrayList data)
    {
        albumSongs = data;
        albumSongs.sort(Comparator.comparing(Songs::getTrack));

        String songsNum;
        if(albumSongs.size() == 1)
        {
            songsNum = albumSongs.size() + " song";
            numberOfSongs.setText(songsNum);
        }

        else
        {
            songsNum = albumSongs.size() + " songs";
            numberOfSongs.setText(songsNum);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        albumDetailsAdapter = new AlbumDetailsAdapter(albumSongs, this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(albumDetailsAdapter);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList> loader) { }
}

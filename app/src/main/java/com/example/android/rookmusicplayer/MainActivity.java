package com.example.android.rookmusicplayer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.android.rookmusicplayer.architecture.StateViewModel;
import com.example.android.rookmusicplayer.fragments.AlbumDetailsFragment;
import com.example.android.rookmusicplayer.fragments.AlbumsFragment;
import com.example.android.rookmusicplayer.fragments.ArtistDetailsFragment;
import com.example.android.rookmusicplayer.fragments.ArtistsFragment;
import com.example.android.rookmusicplayer.fragments.LibraryFragment;
import com.example.android.rookmusicplayer.fragments.PlaylistDetailsFragment;
import com.example.android.rookmusicplayer.fragments.PlaylistsFragment;
import com.example.android.rookmusicplayer.fragments.SearchFragment;
import com.example.android.rookmusicplayer.fragments.SongsFragment;
import com.example.android.rookmusicplayer.helpers.GoToDialog;
import com.example.android.rookmusicplayer.helpers.MediaBrowserHelper;
import com.example.android.rookmusicplayer.helpers.MediaControlDialog;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.rookmusicplayer.App.CLEAR;
import static com.example.android.rookmusicplayer.App.FROM_ARTIST;
import static com.example.android.rookmusicplayer.App.FROM_LIBRARY;
import static com.example.android.rookmusicplayer.App.FROM_SEARCH;
import static com.example.android.rookmusicplayer.App.SET_POSITION;
import static com.example.android.rookmusicplayer.App.SET_QUEUE_ARTIST;
import static com.example.android.rookmusicplayer.App.SET_QUEUE_LIBRARY;
import static com.example.android.rookmusicplayer.App.SET_QUEUE_SEARCH;
import static com.example.android.rookmusicplayer.App.SET_UP_NEXT;
import static com.example.android.rookmusicplayer.App.TO_ALBUM;
import static com.example.android.rookmusicplayer.App.TO_ARTIST;
import static com.example.android.rookmusicplayer.App.albums;
import static com.example.android.rookmusicplayer.App.albumsSections;
import static com.example.android.rookmusicplayer.App.artistSongs;
import static com.example.android.rookmusicplayer.App.artists;
import static com.example.android.rookmusicplayer.App.mediaBrowserHelper;
import static com.example.android.rookmusicplayer.App.nowPlayingFrom;
import static com.example.android.rookmusicplayer.App.playlists;
import static com.example.android.rookmusicplayer.App.savedSongs;
import static com.example.android.rookmusicplayer.App.savedState;
import static com.example.android.rookmusicplayer.App.searchSong;
import static com.example.android.rookmusicplayer.App.songs;

public class MainActivity extends AppCompatActivity implements SongsFragment.NowPlayingLibrary, AlbumsFragment.NowPlayingAlbum, ArtistsFragment.NowPlayingArtist, PlaylistsFragment.NowPlayingPlaylist, GoToDialog.GoTo, LibraryFragment.Query, MediaControlDialog.UpdateLibrary
{
    private static final String TAG = MainActivity.class.getSimpleName();

    //PERMISSIONS
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int PERMISSION_REQUEST_CODE = 1;
    private StateViewModel stateViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.hide();

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
        else
        {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            LibraryFragment fragment = new LibraryFragment(songs, artists, albumsSections, playlists, this);
            transaction.add(R.id.fragment_container, fragment, "Main Library").commit();

            stateViewModel = ViewModelProviders.of(this).get(StateViewModel.class);
            stateViewModel.getSavedQueue().observe(this, new Observer<List<Songs>>() {
                @Override
                public void onChanged(List<Songs> songs)
                {
                    savedSongs = (ArrayList<Songs>) songs;
                    Log.i(TAG, "RETRIEVED SAVED QUEUE");
                }
            });

            stateViewModel.getSavedStateDetails().observe(this, new Observer<List<SavedStateDetails>>() {
                @Override
                public void onChanged(List<SavedStateDetails> savedStateDetails)
                {
                    savedState = (ArrayList<SavedStateDetails>) savedStateDetails;
                    Log.i(TAG, "RETRIEVED SAVED STATE");
                }
            });

            View rootView = findViewById(R.id.fragment_container).getRootView();
            mediaBrowserHelper = new MediaBrowserHelper(this, rootView, stateViewModel);
            mediaBrowserHelper.onCreate();
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        mediaBrowserHelper.onStart();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        Log.i(TAG, "SAVING UI STATE");
        mediaBrowserHelper.onStop();
        Log.i(TAG, "UI STATE SAVED");
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mediaBrowserHelper.onDestroy();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
        {
            mediaBrowserHelper.setVolumeBar();
            return false;
        }

        else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
        {
            mediaBrowserHelper.setVolumeBar();
            return false;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "PERMISSIONS GRANTED", Toast.LENGTH_LONG).show();
                break;

            default:
                Toast.makeText(this, "PERMISSIONS NOT GRANTED", Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void updateSongsLibrary(Songs song)
    {
        LibraryFragment fragment = (LibraryFragment) getSupportFragmentManager().findFragmentByTag("Main Library");
        if(fragment != null)
            fragment.deleteSongFromLibrary(song);
    }

    @Override
    public void updateAlbumsLibrary(Albums album, ArrayList<Songs> albumSongs)
    {
        LibraryFragment fragment = (LibraryFragment) getSupportFragmentManager().findFragmentByTag("Main Library");
        if(fragment != null)
            fragment.deleteAlbumFromLibrary(album, albumSongs);
    }

    @Override
    public void updateArtistsLibrary(Artists artist, ArrayList<Albums> artistAlbums)
    {
        LibraryFragment fragment = (LibraryFragment) getSupportFragmentManager().findFragmentByTag("Main Library");
        if(fragment != null)
            fragment.deleteArtistFromLibrary(artist, artistAlbums);
    }

    @Override
    public void sendSong(ArrayList<Songs> songs, int index, int from)
    {
        getMediaController().getTransportControls().sendCustomAction(CLEAR, null);
        Bundle queue = new Bundle();
        switch (from)
        {
            case FROM_LIBRARY:
                queue.putInt("CURRENT_QUEUE_POSITION", index);
                getMediaController().getTransportControls().sendCustomAction(SET_POSITION, queue);
                getMediaController().getTransportControls().sendCustomAction(SET_QUEUE_LIBRARY, null);
                nowPlayingFrom = "Now playing from Library";
                break;

            case FROM_ARTIST:
                queue.putInt("CURRENT_QUEUE_POSITION", index);
                getMediaController().getTransportControls().sendCustomAction(SET_POSITION, queue);
                artistSongs = songs;
                getMediaController().getTransportControls().sendCustomAction(SET_QUEUE_ARTIST, null);
                nowPlayingFrom = "Now playing from " + songs.get(index).getArtist();
                break;

            case FROM_SEARCH:
                queue.putInt("CURRENT_QUEUE_POSITION", 0);
                getMediaController().getTransportControls().sendCustomAction(SET_POSITION, queue);
                searchSong.clear(); searchSong.add(songs.get(index));
                getMediaController().getTransportControls().sendCustomAction(SET_QUEUE_SEARCH, null);
                nowPlayingFrom = "Now playing from Search";
                break;
        }
        getMediaController().getTransportControls().playFromMediaId(songs.get(index).getPath(), null);
        getMediaController().getTransportControls().sendCustomAction(SET_UP_NEXT, null);
        mediaBrowserHelper.setBottomSheetQueue();
    }

    @Override
    public void sendAlbum(Albums album, ImageView sharedArt, int from, MediaControlDialog.UpdateLibrary updateLibrary)
    {
        AlbumDetailsFragment fragment = new AlbumDetailsFragment(album, updateLibrary);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.addSharedElement(sharedArt, ViewCompat.getTransitionName(sharedArt)).replace(R.id.fragment_container, fragment).addToBackStack("").commit();
        Log.i(TAG, "FRAGMENT CURRENTLY IN BACKSTACK: " + manager.findFragmentById(R.id.fragment_container).getClass().getSimpleName());
    }

    @Override
    public void sendArtist(Artists artist, TextView sharedArtist, MediaControlDialog.UpdateLibrary updateLibrary)
    {
        ArtistDetailsFragment fragment = new ArtistDetailsFragment(artist, updateLibrary);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.addSharedElement(sharedArtist, ViewCompat.getTransitionName(sharedArtist)).replace(R.id.fragment_container, fragment).addToBackStack("").commit();
        Log.i(TAG, "FRAGMENT CURRENTLY IN BACKSTACK: " + manager.findFragmentById(R.id.fragment_container).getClass().getSimpleName());
    }

    @Override
    public void sendPlaylist(Playlists playlist)
    {
        PlaylistDetailsFragment fragment = new PlaylistDetailsFragment(playlist);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment).addToBackStack("").commit();
        Log.i(TAG, "FRAGMENT CURRENTLY IN BACKSTACK: " + manager.findFragmentById(R.id.fragment_container).getClass().getSimpleName());
    }

    @Override
    public void goTo(Songs artist_album, int to)
    {
        mediaBrowserHelper.CollapseBottomSheet();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run()
            {
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();

                Fragment fragment;
                switch (to)
                {
                    case TO_ARTIST:
                        fragment = new ArtistDetailsFragment(artist_album, MainActivity.this);
                        transaction.replace(R.id.fragment_container, fragment).addToBackStack("").commit();
                        break;

                    case TO_ALBUM:
                        fragment = new AlbumDetailsFragment(artist_album, MainActivity.this);
                        transaction.replace(R.id.fragment_container, fragment).addToBackStack("").commit();
                        break;
                }

                Log.i(TAG, "FRAGMENT CURRENTLY IN BACKSTACK: " + manager.findFragmentById(R.id.fragment_container).getClass().getSimpleName());
            }
        }, 800);
    }

    @Override
    public void search()
    {
        SearchFragment fragment = new SearchFragment(songs, artists, albums);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment).addToBackStack("").commit();
        Log.i(TAG, "FRAGMENT CURRENTLY IN BACKSTACK: " + manager.findFragmentById(R.id.fragment_container).getClass().getSimpleName());
    }
}

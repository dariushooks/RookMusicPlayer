package com.example.android.rookmusicplayer;

import android.Manifest;
import android.app.PendingIntent;
import android.app.RecoverableSecurityException;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.example.android.rookmusicplayer.architecture.LibraryViewModel;
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
import com.example.android.rookmusicplayer.helpers.MediaBrowserHelperMotion;
import com.example.android.rookmusicplayer.helpers.MediaControlDialog;
import com.example.android.rookmusicplayer.helpers.ReadStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.android.rookmusicplayer.App.CLEAR;
import static com.example.android.rookmusicplayer.App.FROM_ARTIST;
import static com.example.android.rookmusicplayer.App.FROM_LIBRARY;
import static com.example.android.rookmusicplayer.App.FROM_SEARCH;
import static com.example.android.rookmusicplayer.App.READ_STORAGE_LOADER;
import static com.example.android.rookmusicplayer.App.SET_POSITION;
import static com.example.android.rookmusicplayer.App.SET_QUEUE_ARTIST;
import static com.example.android.rookmusicplayer.App.SET_QUEUE_LIBRARY;
import static com.example.android.rookmusicplayer.App.SET_QUEUE_SEARCH;
import static com.example.android.rookmusicplayer.App.SET_UP_NEXT;
import static com.example.android.rookmusicplayer.App.TO_ALBUM;
import static com.example.android.rookmusicplayer.App.TO_ARTIST;
import static com.example.android.rookmusicplayer.App.albums;
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
    private static final int REQUEST_CODE = 1;

    //PERMISSIONS
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int PERMISSION_REQUEST_CODE = 1;
    private StateViewModel stateViewModel;
    private LibraryViewModel libraryViewModel;
    private ArrayList<Uri> deleteUris = new ArrayList<>();
    private ArrayList<Songs> deleteSongs;
    private String mediaName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        libraryViewModel = new ViewModelProvider(this).get(LibraryViewModel.class);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.hide();

        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
        }

        else
        {
            LoaderManager.getInstance(this).initLoader(READ_STORAGE_LOADER, null, loaderCallbacks);
        }
    }

    private void loadUI()
    {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        LibraryFragment fragment = new LibraryFragment(songs, artists, albums, playlists, this);
        transaction.add(R.id.fragment_container, fragment, "Main Library").commit();

        stateViewModel = new ViewModelProvider(this).get(StateViewModel.class);
        //stateViewModel = ViewModelProviders.of(this).get(StateViewModel.class);
        stateViewModel.getSavedQueue().observe(this, new Observer<List<Songs>>() {
            @Override
            public void onChanged(List<Songs> songs)
            {
                savedSongs = (ArrayList<Songs>) songs;
                Log.i(TAG, "RETRIEVED " + savedSongs.size() + " SONGS FROM SAVED QUEUE");
            }
        });

        stateViewModel.getSavedStateDetails().observe(this, new Observer<List<SavedStateDetails>>() {
            @Override
            public void onChanged(List<SavedStateDetails> savedStateDetails)
            {
                savedState = (ArrayList<SavedStateDetails>) savedStateDetails;
                //Log.i(TAG, "RETRIEVED SAVED STATE");
            }
        });

        View rootView = findViewById(R.id.bottomSheetPlaying).getRootView();
        mediaBrowserHelper = new MediaBrowserHelperMotion(this, rootView, stateViewModel);
        mediaBrowserHelper.onCreate();
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        mediaBrowserHelper.onRestart();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        //Log.i(TAG, "SAVING UI STATE");
        mediaBrowserHelper.onStop();
        //Log.i(TAG, "UI STATE SAVED");
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
                {
                    Toast.makeText(this, "PERMISSIONS GRANTED", Toast.LENGTH_LONG).show();
                    LoaderManager.getInstance(this).initLoader(READ_STORAGE_LOADER, null, loaderCallbacks);
                }

                break;

            default:
                Toast.makeText(this, "PERMISSIONS NOT GRANTED", Toast.LENGTH_LONG).show();
                if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                {
                    new AlertDialog.Builder(MainActivity.this).setTitle("Permission Needed")
                            .setMessage("This permission is needed to allow access to the music stored on the device")
                            .setPositiveButton("ALLOW", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    requestPermissions(permissions, PERMISSION_REQUEST_CODE);
                                }
                            })
                            .setNegativeButton("DENY", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    dialog.dismiss();
                                }
                            })
                            .create().show();
                }
                break;
        }
    }

    @Override
    public void updateSongsLibrary(Songs song)
    {
        mediaName = song.getTitle();
        ArrayList<Songs> list = new ArrayList<>();
        list.add(song);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            deleteMedia(list);
        else
            delete(list);
        LibraryFragment fragment = (LibraryFragment) getSupportFragmentManager().findFragmentByTag("Main Library");
        if(fragment != null)
            fragment.deleteSongFromLibrary(song);
    }

    @Override
    public void updateAlbumsLibrary(Albums album, ArrayList<Songs> albumSongs, int from)
    {
        mediaName = album.getAlbum();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            deleteMedia(albumSongs);
        else
            delete(albumSongs);
        LibraryFragment fragment = (LibraryFragment) getSupportFragmentManager().findFragmentByTag("Main Library");
        if(fragment != null)
            fragment.deleteAlbumFromLibrary(album, albumSongs, from);
    }

    @Override
    public void updateArtistsLibrary(Artists artist, ArrayList<Songs> artistSongs)
    {
        mediaName = artist.getArtist();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            deleteMedia(artistSongs);
        else
            delete(artistSongs);
        LibraryFragment fragment = (LibraryFragment) getSupportFragmentManager().findFragmentByTag("Main Library");
        if(fragment != null)
            fragment.deleteArtistFromLibrary(artist, artistSongs);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
        {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                MediaStore.createDeleteRequest(getContentResolver(), deleteUris);

            else
            {
                String selectionSongs = MediaStore.Audio.Media._ID + "=?";
                for(int i = 0; i < deleteSongs.size(); i++)
                {
                    long songId = Long.parseLong(deleteSongs.get(i).getId());
                    Uri songsUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId);
                    String[] selectionArgsArtistSongs = {deleteSongs.get(i).getId()};
                    getContentResolver().delete(songsUri, selectionSongs, selectionArgsArtistSongs);
                }
            }

            Toast.makeText(this, mediaName + " DELETED", Toast.LENGTH_SHORT).show();
        }

        else
            Toast.makeText(this, "ACCESS DENIED", Toast.LENGTH_SHORT).show();
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
                queue.putInt("FROM", from);
                getMediaController().getTransportControls().sendCustomAction(SET_POSITION, queue);
                getMediaController().getTransportControls().sendCustomAction(SET_QUEUE_LIBRARY, queue);
                nowPlayingFrom = "Now playing from Library";
                break;

            case FROM_ARTIST:
                queue.putInt("CURRENT_QUEUE_POSITION", index);
                queue.putInt("FROM", from);
                getMediaController().getTransportControls().sendCustomAction(SET_POSITION, queue);
                artistSongs = songs;
                getMediaController().getTransportControls().sendCustomAction(SET_QUEUE_ARTIST, queue);
                nowPlayingFrom = "Now playing from " + songs.get(index).getArtist();
                break;

            case FROM_SEARCH:
                queue.putInt("CURRENT_QUEUE_POSITION", 0);
                queue.putInt("FROM", from);
                getMediaController().getTransportControls().sendCustomAction(SET_POSITION, queue);
                searchSong.clear(); searchSong.add(songs.get(index));
                getMediaController().getTransportControls().sendCustomAction(SET_QUEUE_SEARCH, queue);
                nowPlayingFrom = "Now playing from Search";
                break;
        }

        getMediaController().getTransportControls().playFromMediaId(songs.get(index).getId(), null);
        getMediaController().getTransportControls().sendCustomAction(SET_UP_NEXT, null);
        mediaBrowserHelper.setBottomSheetQueue();
    }

    @Override
    public void sendAlbum(Albums album, ImageView sharedArt, int from, MediaControlDialog.UpdateLibrary updateLibrary, int position)
    {
        AlbumDetailsFragment fragment = new AlbumDetailsFragment(album, updateLibrary, position);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.addSharedElement(sharedArt, sharedArt.getTransitionName()).replace(R.id.fragment_container, fragment).addToBackStack("").commit();
        //Log.i(TAG, "FRAGMENT CURRENTLY IN BACKSTACK: " + manager.findFragmentById(R.id.fragment_container).getClass().getSimpleName());
    }

    @Override
    public void sendArtist(Artists artist, TextView sharedArtist, MediaControlDialog.UpdateLibrary updateLibrary)
    {
        ArtistDetailsFragment fragment = new ArtistDetailsFragment(artist, updateLibrary);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.addSharedElement(sharedArtist, sharedArtist.getTransitionName()).replace(R.id.fragment_container, fragment).addToBackStack("").commit();
        //Log.i(TAG, "FRAGMENT CURRENTLY IN BACKSTACK: " + manager.findFragmentById(R.id.fragment_container).getClass().getSimpleName());
    }

    @Override
    public void sendPlaylist(Playlists playlist)
    {
        PlaylistDetailsFragment fragment = new PlaylistDetailsFragment(playlist);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment).addToBackStack("").commit();
        //Log.i(TAG, "FRAGMENT CURRENTLY IN BACKSTACK: " + manager.findFragmentById(R.id.fragment_container).getClass().getSimpleName());
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

                //Log.i(TAG, "FRAGMENT CURRENTLY IN BACKSTACK: " + manager.findFragmentById(R.id.fragment_container).getClass().getSimpleName());
            }
        }, 800);
    }

    @Override
    public void search(ImageView sharedImage)
    {
        SearchFragment fragment = new SearchFragment(songs, artists, albums);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.addSharedElement(sharedImage, sharedImage.getTransitionName()).replace(R.id.fragment_container, fragment).addToBackStack("").commit();
        //Log.i(TAG, "FRAGMENT CURRENTLY IN BACKSTACK: " + manager.findFragmentById(R.id.fragment_container).getClass().getSimpleName());
    }

    private void delete(ArrayList<Songs> deleteSongs)
    {
        this.deleteSongs = deleteSongs;
        String selectionSongs = MediaStore.Audio.Media._ID + "=?";
        for(int i = 0; i < deleteSongs.size(); i++)
        {
            long songId = Long.parseLong(deleteSongs.get(i).getId());
            Uri songsUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId);
            String[] selectionArgsArtistSongs = {deleteSongs.get(i).getId()};
            try
            {
                getContentResolver().delete(songsUri, selectionSongs, selectionArgsArtistSongs);
            }

            catch (SecurityException securityException)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                {
                    RecoverableSecurityException recoverableSecurityException;
                    if (securityException instanceof RecoverableSecurityException)
                        recoverableSecurityException = (RecoverableSecurityException) securityException;
                    else
                        throw new RuntimeException(securityException.getMessage(), securityException);
                    IntentSender intentSender = recoverableSecurityException.getUserAction().getActionIntent().getIntentSender();
                    try {
                        startIntentSenderForResult(intentSender, REQUEST_CODE, null, 0, 0, 0, null);
                    } catch (IntentSender.SendIntentException e) { e.printStackTrace(); }
                }

                else
                    throw new RuntimeException(securityException.getMessage(), securityException);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void deleteMedia(ArrayList<Songs> deleteSongs)
    {
        long songId;
        Uri songsUri;
        for(int i = 0; i < deleteSongs.size(); i++)
        {
            songId = Long.parseLong(deleteSongs.get(i).getId());
            songsUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId);
            deleteUris.add(songsUri);
        }

        PendingIntent pendingIntent = MediaStore.createDeleteRequest(getContentResolver(), deleteUris);
        try
        {
            startIntentSenderForResult(pendingIntent.getIntentSender(), REQUEST_CODE, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) { e.printStackTrace();}
        /*try
        {
            PendingIntent pendingIntent = MediaStore.createDeleteRequest(getContentResolver(), deleteUris);
            startIntentSenderForResult(pendingIntent.getIntentSender(), REQUEST_CODE, null, 0, 0, 0);
        }

        catch (SecurityException securityException)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            {
                RecoverableSecurityException recoverableSecurityException;
                if (securityException instanceof RecoverableSecurityException)
                    recoverableSecurityException = (RecoverableSecurityException) securityException;
                else
                    throw new RuntimeException(securityException.getMessage(), securityException);
                IntentSender intentSender = recoverableSecurityException.getUserAction().getActionIntent().getIntentSender();
                try {
                    startIntentSenderForResult(intentSender, REQUEST_CODE, null, 0, 0, 0, null);
                } catch (IntentSender.SendIntentException e) { e.printStackTrace(); }
            }

            else
                throw new RuntimeException(securityException.getMessage(), securityException);
        }*/
    }


    private LoaderManager.LoaderCallbacks loaderCallbacks = new LoaderManager.LoaderCallbacks()
    {
        @NonNull
        @Override
        public Loader onCreateLoader(int id, @Nullable Bundle args) { return new ReadStorage(MainActivity.this, libraryViewModel); }

        @Override
        public void onLoadFinished(@NonNull Loader loader, Object data)
        {
            LoaderManager.getInstance(MainActivity.this).destroyLoader(READ_STORAGE_LOADER);
            loadUI();
        }

        @Override
        public void onLoaderReset(@NonNull Loader loader) {}
    };
}

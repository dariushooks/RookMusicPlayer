package com.rookieandroid.rookiemusicplayer.helpers;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rookieandroid.rookiemusicplayer.Albums;
import com.rookieandroid.rookiemusicplayer.Artists;
import com.rookieandroid.rookiemusicplayer.MainActivity;
import com.rookieandroid.rookiemusicplayer.Playlists;
import com.rookieandroid.rookiemusicplayer.R;
import com.rookieandroid.rookiemusicplayer.Songs;
import com.rookieandroid.rookiemusicplayer.adapters.AlbumDetailsAdapter;
import com.rookieandroid.rookiemusicplayer.adapters.AlbumsAdapter;
import com.rookieandroid.rookiemusicplayer.adapters.ArtistsAdapter;
import com.rookieandroid.rookiemusicplayer.adapters.PlaylistDialogAdapter;
import com.rookieandroid.rookiemusicplayer.adapters.PlaylistsAdapter;
import com.rookieandroid.rookiemusicplayer.adapters.SongsAdapter;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import static com.rookieandroid.rookiemusicplayer.App.DIALOG_MEDIA_LOADER;
import static com.rookieandroid.rookiemusicplayer.App.FROM_ALBUM;
import static com.rookieandroid.rookiemusicplayer.App.FROM_ARTIST;
import static com.rookieandroid.rookiemusicplayer.App.FROM_LIBRARY;
import static com.rookieandroid.rookiemusicplayer.App.FROM_PLAYLIST;
import static com.rookieandroid.rookiemusicplayer.App.GET_ALBUM_SONGS;
import static com.rookieandroid.rookiemusicplayer.App.GET_ARTIST_SONGS;
import static com.rookieandroid.rookiemusicplayer.App.mediaBrowserHelper;
import static com.rookieandroid.rookiemusicplayer.App.playlists;

public class MediaControlDialog extends AlertDialog implements PlaylistDialogAdapter.ListItemClickListener
{
    private final String TAG = MediaControlDialog.class.getSimpleName();

    private static final int SONG = 1;
    private static final int ALBUM = 2;
    private static final int ARTIST = 3;
    private static final int PLAYLIST = 4;
    private static final int PLAYLIST_SONG = 5;

    ContentResolver contentResolver;
    private int removePosition;
    private ArrayList<Songs> albumSongs;
    private ArrayList<Songs> artistSongs;

    private Songs song;
    private Songs playlistSong;
    private Albums album;
    private Artists artist;
    private Playlists playlist;
    private ArrayList<Songs> librarySongs;
    private SongsAdapter librarySongsAdapter;
    private ArrayList<Albums> libraryAlbums;
    private AlbumsAdapter libraryAlbumsAdapter;
    private AlbumDetailsAdapter albumSongsAdapter;
    private ArrayList<Artists> libraryArtists;
    private ArtistsAdapter libraryArtistsAdapter;
    private ArrayList<Playlists> currentPlaylists;
    private PlaylistsAdapter playlistsAdapter;
    private ArrayList<Songs> playlistSongs;
    private SongsAdapter playlistSongsAdapter;
    private AlertDialog alertDialog;
    private AlertDialog alertDialogPlaylist;
    private AlertDialog alertDialogNewPlaylist;
    private int media;
    private int code;
    private int ID;
    private Context context;
    private MediaControlDialog.UpdateLibrary updateLibrary;
    private MediaControlDialog.UpdatePlaylist updatePlaylist;

    //MEDIA CONTROL
    private ImageView mediaArt;
    private TextView mediaName;
    private TextView mediaArtist;
    private TextView mediaType;
    private TextView deleteText;
    private RelativeLayout delete;
    private RelativeLayout playlistAction;
    private RelativeLayout next;
    private RelativeLayout last;

    //PLAYLIST CONTROL
    private RecyclerView recyclerView;
    private Button cancelPlaylist;
    private Button createNewPlaylist;

    //NEW PLAYLIST CONTROL
    private TextInputEditText newPlaylistName;
    private TextInputEditText newPlaylistDescription;
    private Button newPlaylistCancel;
    private Button newPlaylistCreate;

    public interface UpdateLibrary
    {
        void updateSongsLibrary(Songs song);
        void updateAlbumsLibrary(Albums album, ArrayList<Songs> albumSongs, int from);
        void updateArtistsLibrary(Artists artist, ArrayList<Songs> artistSongs);
    }

    public interface UpdatePlaylist
    {
        void updatePlaylistCount();
    }

    //DIALOG FOR SONG
    public MediaControlDialog(Context context, Songs song, ArrayList<Songs> librarySongs, SongsAdapter librarySongsAdapter, MediaControlDialog.UpdateLibrary updateLibrary, int code)
    {
        super(context);
        this.song = song;
        this.librarySongs = librarySongs;
        this.librarySongsAdapter = librarySongsAdapter;
        this.context = context;
        this.updateLibrary = updateLibrary;
        this.code = code;
        media = SONG;
        contentResolver = context.getContentResolver();
    }

    //DIALOG FOR ALBUM SONG
    public MediaControlDialog(Context context, Songs song, ArrayList<Songs> librarySongs, AlbumDetailsAdapter albumSongsAdapter, MediaControlDialog.UpdateLibrary updateLibrary, int code)
    {
        super(context);
        this.song = song;
        this.librarySongs = librarySongs;
        this.albumSongsAdapter = albumSongsAdapter;
        this.context = context;
        this.updateLibrary = updateLibrary;
        this.code = code;
        media = SONG;
        contentResolver = context.getContentResolver();
    }

    //DIALOG FOR ALBUM
    public MediaControlDialog(Context context, Albums album, ArrayList<Albums> libraryAlbums, AlbumsAdapter libraryAlbumsAdapter, MediaControlDialog.UpdateLibrary updateLibrary, int code)
    {
        super(context);
        this.album = album;
        this.libraryAlbums = libraryAlbums;
        this.libraryAlbumsAdapter = libraryAlbumsAdapter;
        this.context = context;
        this.updateLibrary = updateLibrary;
        this.code = code;
        media = ALBUM;
        contentResolver = context.getContentResolver();
    }

    //DIALOG FOR ARTIST
    public MediaControlDialog(Context context, Artists artist, ArrayList<Artists> libraryArtists, ArtistsAdapter libraryArtistsAdapter, MediaControlDialog.UpdateLibrary updateLibrary, int code)
    {
        super(context);
        this.artist = artist;
        this.libraryArtists = libraryArtists;
        this.libraryArtistsAdapter = libraryArtistsAdapter;
        this.context = context;
        this.updateLibrary = updateLibrary;
        this.code = code;
        media = ARTIST;
        contentResolver = context.getContentResolver();
    }

    //DIALOG FOR PLAYLIST
    public MediaControlDialog(Context context, Playlists playlist, ArrayList<Playlists> currentPlaylists, PlaylistsAdapter playlistsAdapter, int code)
    {
        super(context);
        this.playlist = playlist;
        this.context = context;
        this.currentPlaylists = currentPlaylists;
        this.playlistsAdapter = playlistsAdapter;
        this.code = code;
        media = PLAYLIST;
        contentResolver = context.getContentResolver();
    }

    //DIALOG FOR PLAYLIST SONG
    public MediaControlDialog(Context context, Songs playlistSong, Playlists playlist, ArrayList<Songs> playlistSongs, SongsAdapter playlistSongsAdapter, MediaControlDialog.UpdatePlaylist updatePlaylist, int code)
    {
        super(context);
        this.playlistSong = playlistSong;
        this.playlist = playlist;
        this.context = context;
        this.playlistSongs = playlistSongs;
        this.playlistSongsAdapter = playlistSongsAdapter;
        this.code = code;
        this.updatePlaylist = updatePlaylist;
        media = PLAYLIST_SONG;
        contentResolver = context.getContentResolver();
    }

    public void OpenDialog()
    {
        alertDialog = new AlertDialog.Builder(context).create();
        final View alertLayout = getLayoutInflater().inflate(R.layout.dialog_media,null);

        mediaArt = alertLayout.findViewById(R.id.mediaArt);
        mediaName = alertLayout.findViewById(R.id.mediaName);
        mediaArtist = alertLayout.findViewById(R.id.mediaArtist);
        mediaType = alertLayout.findViewById(R.id.mediaType);
        delete = alertLayout.findViewById(R.id.mediaDelete);
        playlistAction = alertLayout.findViewById(R.id.mediaPlaylist);
        deleteText = alertLayout.findViewById(R.id.mediaDeleteText);
        next = alertLayout.findViewById(R.id.mediaPlayNext);
        last = alertLayout.findViewById(R.id.mediaPlayLast);
        setDialogClickListeners(alertDialog);

        switch (media)
        {
            case SONG:
                    setSongArt(song);
                    deleteText.setText(context.getString(R.string.deleteSongFromLibrary));
                    mediaName.setText(song.getTitle());
                    mediaArtist.setText(song.getArtist());
                    mediaType.setText(song.getAlbum());
                    break;

            case ALBUM:
                    setAlbumArt(album);
                    deleteText.setText(context.getString(R.string.deleteAlbumFromLibrary));
                    mediaName.setText(album.getAlbum());
                    mediaArtist.setText(album.getArtist());
                    mediaType.setVisibility(View.GONE);
                    break;

            case ARTIST:
                    deleteText.setText(context.getString(R.string.deleteArtistFromLibrary));
                    mediaArt.setImageDrawable(context.getDrawable(R.drawable.ic_noartistart));
                    mediaName.setText(artist.getArtist());
                    mediaArtist.setVisibility(View.GONE);
                    mediaType.setVisibility(View.GONE);
                    break;

            case PLAYLIST:
                    setPlaylistArt();
                    if(currentPlaylists == null && playlistsAdapter == null)
                        delete.setVisibility(View.GONE);
                    else
                        deleteText.setText(context.getString(R.string.deletePlaylist));
                    mediaName.setText(playlist.getPlaylist());
                    mediaArtist.setVisibility(View.GONE);
                    mediaType.setVisibility(View.GONE);
                    break;

            case PLAYLIST_SONG:
                setSongArt(playlistSong);
                deleteText.setText(context.getString(R.string.deleteSongFromPlaylist));
                mediaName.setText(playlistSong.getTitle());
                mediaArtist.setText(playlistSong.getArtist());
                mediaType.setText(playlistSong.getAlbum());
                break;
        }

        switch (code)
        {
            case FROM_LIBRARY:
                //Log.i(TAG, "DIALOG FROM LIBRARY");
                break;

            case FROM_ARTIST:
                //Log.i(TAG, "DIALOG FROM ARTIST");
                break;

            case FROM_ALBUM:
                //Log.i(TAG, "DIALOG FROM ALBUM");
                break;

            case FROM_PLAYLIST:
                //Log.i(TAG, "DIALOG FROM PLAYLIST");
                break;
        }

        alertDialog.setView(alertLayout);
        new Dialog(context);
        alertDialog.show();
        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 20);
        alertDialog.getWindow().setBackgroundDrawable(inset);
        alertDialog.getWindow().setElevation(20.0f);
    }

    private void OpenPlaylistDialog()
    {
        alertDialogPlaylist = new AlertDialog.Builder(context).create();
        final View alertLayoutPlaylist = getLayoutInflater().inflate(R.layout.dialog_playlist,null);

        cancelPlaylist = alertLayoutPlaylist.findViewById(R.id.cancelPlaylist);
        createNewPlaylist = alertLayoutPlaylist.findViewById(R.id.createPlaylist);
        setPlaylistDialogClickListeners(alertDialogPlaylist);

        recyclerView = alertLayoutPlaylist.findViewById(R.id.createdPlaylists);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        PlaylistDialogAdapter dialogAdapter = new PlaylistDialogAdapter(playlists, this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(dialogAdapter);

        alertDialogPlaylist.setView(alertLayoutPlaylist);
        new Dialog(context);
        alertDialogPlaylist.show();
        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 100);

        alertDialogPlaylist.getWindow().setBackgroundDrawable(inset);
        alertDialogPlaylist.getWindow().setElevation(20.0f);
    }

    public void OpenNewPlaylistDialog()
    {
        alertDialogNewPlaylist = new AlertDialog.Builder(context).create();
        final View alertLayoutNewPlaylist = getLayoutInflater().inflate(R.layout.dialog_new_playlist,null);

        newPlaylistCancel = alertLayoutNewPlaylist.findViewById(R.id.cancelNewPlaylist);
        newPlaylistCreate = alertLayoutNewPlaylist.findViewById(R.id.createNewPlaylist);
        newPlaylistName = alertLayoutNewPlaylist.findViewById(R.id.newPlaylistName);
        newPlaylistDescription = alertLayoutNewPlaylist.findViewById(R.id.newPlaylistDescription);
        setNewPlaylistDialogClickListeners(alertDialogNewPlaylist);

        alertDialogNewPlaylist.setView(alertLayoutNewPlaylist);
        new Dialog(context);
        alertDialogNewPlaylist.show();
        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 100);
        alertDialogNewPlaylist.getWindow().setBackgroundDrawable(inset);
        alertDialogNewPlaylist.getWindow().setElevation(20.0f);
    }

    private void setSongArt(Songs song)
    {
        Uri uri = Uri.parse(song.getArt());
        Glide.with(context).load(uri).placeholder(R.drawable.noalbumart).fallback(R.drawable.noalbumart).error(R.drawable.noalbumart).into(mediaArt);
    }

    private void setAlbumArt(Albums album)
    {
        Uri uri = Uri.parse(album.getArt());
        Glide.with(context).load(uri).placeholder(R.drawable.noalbumart).fallback(R.drawable.noalbumart).error(R.drawable.noalbumart).into(mediaArt);
    }

    private void setPlaylistArt()
    {
        Glide.with(context).load(R.drawable.playlistart).into(mediaArt);
    }

    @Override
    public void onListItemClick(Playlists playlist)
    {
        switch (media)
        {
            case SONG:
                mediaBrowserHelper.addToPlaylist(playlist, song);
                Toast.makeText(context, song.getTitle().toUpperCase() + " ADDED TO " + playlist.getPlaylist().toUpperCase(), Toast.LENGTH_SHORT).show();
                break;

            case ARTIST:
                mediaBrowserHelper.addToPlaylist(playlist, artist);
                Toast.makeText(context, artist.getArtist().toUpperCase() + " ADDED TO " + playlist.getPlaylist().toUpperCase(), Toast.LENGTH_SHORT).show();
                //Log.i(TAG, artist.getArtist().toUpperCase() + " ADDED TO " + playlist.getPlaylist().toUpperCase());
                break;

            case ALBUM:
                mediaBrowserHelper.addToPlaylist(playlist, album);
                Toast.makeText(context, album.getAlbum().toUpperCase() + " ADDED TO " + playlist.getPlaylist().toUpperCase(), Toast.LENGTH_SHORT).show();
                //Log.i(TAG, album.getAlbum().toUpperCase() + " ADDED TO " + playlist.getPlaylist().toUpperCase());
                break;

            case PLAYLIST:
                mediaBrowserHelper.addToPlaylist(playlist, this.playlist);
                Toast.makeText(context, this.playlist.getPlaylist().toUpperCase() + " ADDED TO " + playlist.getPlaylist().toUpperCase(), Toast.LENGTH_SHORT).show();
                //Log.i(TAG, this.playlist.getPlaylist().toUpperCase() + " ADDED TO " + playlist.getPlaylist().toUpperCase());
                break;
        }
        alertDialogPlaylist.dismiss();
    }

    private void setDialogClickListeners(AlertDialog alertDialog)
    {
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                switch (media)
                {
                    case SONG:
                        alertDialog.dismiss();
                        /*if(code == FROM_ALBUM || code == FROM_ARTIST)
                        {
                            updateLibrary.updateSongsLibrary(song);
                        }*/
                        updateLibrary.updateSongsLibrary(song);
                        removePosition = librarySongs.indexOf(song);
                        librarySongs.remove(song);
                        if(code == FROM_LIBRARY || code == FROM_ARTIST)
                            librarySongsAdapter.notifyItemRemoved(removePosition);
                        else if(code == FROM_ALBUM)
                            albumSongsAdapter.notifyItemRemoved(removePosition);

                        /*long songId = Long.parseLong(song.getId());
                        Uri songUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId);
                        String selectionSong = MediaStore.Audio.Media._ID + "=?";
                        String[] selectionArgsSong = {song.getId()};
                        contentResolver.delete(songUri, selectionSong, selectionArgsSong);*/

                        //Toast.makeText(context, song.getTitle() + " DELETED", Toast.LENGTH_SHORT).show();
                        //Log.i(TAG, song.getTitle().toUpperCase() + " DELETED FROM LIBRARY");
                        break;

                    case ALBUM:
                        alertDialog.dismiss();
                        LoaderManager.getInstance((MainActivity) context).initLoader(DIALOG_MEDIA_LOADER, null, songsCallbacks);
                        break;

                    case ARTIST:
                        alertDialog.dismiss();
                        LoaderManager.getInstance((MainActivity) context).initLoader(DIALOG_MEDIA_LOADER, null, songsCallbacks);
                        break;

                    case PLAYLIST:
                        alertDialog.dismiss();
                        removePosition = currentPlaylists.indexOf(playlist);
                        currentPlaylists.remove(playlist);
                        playlistsAdapter.notifyItemRemoved(removePosition);
                        playlists.remove(playlist);

                        Uri playlistUri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
                        String selectionPlaylist = MediaStore.Audio.Playlists.NAME + "=?";
                        String[] selectionArgPlaylist = {playlist.getPlaylist() + "SPLITHERE" + playlist.getDescription()};
                        contentResolver.delete(playlistUri, selectionPlaylist, selectionArgPlaylist);

                        Uri playlistSongsUri = MediaStore.Audio.Playlists.Members.getContentUri("external", Long.parseLong(playlist.getId()));
                        contentResolver.delete(playlistSongsUri, null, null);

                        Toast.makeText(context, playlist.getPlaylist() + " DELETED", Toast.LENGTH_SHORT).show();
                        //Log.i(TAG, playlist.getPlaylist().toUpperCase() + " DELETED FROM PLAYLISTS");
                        break;

                    case PLAYLIST_SONG:
                        alertDialog.dismiss();
                        removePosition = playlistSongs.indexOf(playlistSong);
                        playlistSongs.remove(playlistSong);
                        playlistSongsAdapter.notifyItemRemoved(removePosition);

                        Uri playlistSongUri = MediaStore.Audio.Playlists.Members.getContentUri("external", Long.parseLong(playlist.getId()));
                        String selectionPlaylistSong = MediaStore.Audio.Playlists.Members.AUDIO_ID + "=?";
                        String[] selectionArgPlaylistSong = {playlistSong.getId()};
                        contentResolver.delete(playlistSongUri, selectionPlaylistSong, selectionArgPlaylistSong);
                        updatePlaylist.updatePlaylistCount();

                        Toast.makeText(context, playlistSong.getTitle() + " DELETED", Toast.LENGTH_SHORT).show();
                        //Log.i(TAG, playlistSong.getTitle().toUpperCase() + " DELETED FROM PLAYLIST");
                        break;
                }
            }
        });

        playlistAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                alertDialog.dismiss();
                OpenPlaylistDialog();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                switch (media)
                {
                    case SONG: mediaBrowserHelper.addNext(song); alertDialog.dismiss(); break;
                    case PLAYLIST_SONG: mediaBrowserHelper.addNext(playlistSong); alertDialog.dismiss(); break;
                    case ALBUM: mediaBrowserHelper.addNext(album); alertDialog.dismiss(); break;
                    case ARTIST: mediaBrowserHelper.addNext(artist); alertDialog.dismiss(); break;
                    case PLAYLIST: mediaBrowserHelper.addNext(playlist); alertDialog.dismiss(); break;
                }
            }
        });

        last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                switch (media)
                {
                    case SONG: mediaBrowserHelper.addLast(song); alertDialog.dismiss(); break;
                    case PLAYLIST_SONG: mediaBrowserHelper.addLast(playlistSong); alertDialog.dismiss(); break;
                    case ALBUM: mediaBrowserHelper.addLast(album); alertDialog.dismiss(); break;
                    case ARTIST: mediaBrowserHelper.addLast(artist); alertDialog.dismiss(); break;
                    case PLAYLIST: mediaBrowserHelper.addLast(playlist); alertDialog.dismiss(); break;
                }
            }
        });
    }

    private void setPlaylistDialogClickListeners(AlertDialog alertDialog)
    {
        cancelPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                alertDialog.dismiss();
            }
        });

        createNewPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                alertDialog.dismiss();
                OpenNewPlaylistDialog();
            }
        });
    }

    private void setNewPlaylistDialogClickListeners(AlertDialog alertDialog)
    {
        newPlaylistCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                alertDialog.dismiss();
            }
        });

        newPlaylistCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String newName = newPlaylistName.getText().toString();
                String newDescription = newPlaylistDescription.getText().toString();
                if(!TextUtils.isEmpty(newName))
                {
                    ContentResolver contentResolver = context.getContentResolver();
                    Uri playlistUri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;

                    ContentValues values = new ContentValues();

                    String split = "SPLITHERE";
                    values.put(MediaStore.Audio.Playlists.NAME, newName + split + newDescription);
                    Uri newPlaylistUri = contentResolver.insert(playlistUri, values);
                    if(newPlaylistUri != null)
                    {
                        String[] projection = {MediaStore.Audio.Playlists.NAME ,MediaStore.Audio.Playlists._ID};
                        Cursor cursor = contentResolver.query(newPlaylistUri ,projection, null, null, null);
                        if(cursor != null && cursor.moveToFirst())
                        {
                            int playlistDetails = cursor.getColumnIndex(MediaStore.Audio.Playlists.NAME);
                            int id = cursor.getColumnIndex(MediaStore.Audio.Playlists._ID);

                            String NameDescription = cursor.getString(playlistDetails);
                            String[] parts = NameDescription.split(split);
                            String playlistName = parts[0];
                            String playlistDescription = parts[1];
                            String playlistID = cursor.getString(id);

                            //Log.i(TAG, "PLAYLIST " + playlistName.toUpperCase() + " CREATED WITH ID " + playlistID);
                            playlists.add(new Playlists(playlistName, playlistID, playlistDescription));
                            cursor.close();
                        }
                    }

                    alertDialog.dismiss();
                    if(playlist != null)
                        OpenPlaylistDialog();
                }
                else
                    newPlaylistName.setError("MUST INPUT NAME");
            }
        });
    }

    private LoaderManager.LoaderCallbacks<ArrayList> songsCallbacks = new LoaderManager.LoaderCallbacks<ArrayList>()
    {
        @NonNull
        @Override
        public Loader<ArrayList> onCreateLoader(int id, @Nullable Bundle args)
        {
            if(media == ALBUM)
                return new GetMedia(context, GET_ALBUM_SONGS, -1, album);
            return new GetMedia(context, GET_ARTIST_SONGS, -1, artist);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<ArrayList> loader, ArrayList data)
        {
            if(media == ALBUM)
            {
                albumSongs = data;
                updateLibrary.updateAlbumsLibrary(album, albumSongs, code);
                if(code == FROM_LIBRARY || code == FROM_ARTIST)
                {
                    removePosition = libraryAlbums.indexOf(album);
                    libraryAlbums.remove(album);
                    libraryAlbumsAdapter.notifyItemRemoved(removePosition);
                }
            }

            else
            {
                artistSongs = data;
                updateLibrary.updateArtistsLibrary(artist, artistSongs);
                removePosition = libraryArtists.indexOf(artist);
                libraryArtists.remove(artist);
                libraryArtistsAdapter.notifyItemRemoved(removePosition);
            }
        }

        @Override
        public void onLoaderReset(@NonNull Loader<ArrayList> loader) {}
    };
}

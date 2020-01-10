package com.example.android.rookmusicplayer.helpers;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.rookmusicplayer.Albums;
import com.example.android.rookmusicplayer.Artists;
import com.example.android.rookmusicplayer.Playlists;
import com.example.android.rookmusicplayer.R;
import com.example.android.rookmusicplayer.Songs;
import com.example.android.rookmusicplayer.adapters.AlbumDetailsAdapter;
import com.example.android.rookmusicplayer.adapters.AlbumsAdapter;
import com.example.android.rookmusicplayer.adapters.ArtistsAdapter;
import com.example.android.rookmusicplayer.adapters.PlaylistDialogAdapter;
import com.example.android.rookmusicplayer.adapters.PlaylistsAdapter;
import com.example.android.rookmusicplayer.adapters.SongsAdapter;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.ArrayList;

import static com.example.android.rookmusicplayer.App.FROM_ALBUM;
import static com.example.android.rookmusicplayer.App.FROM_ARTIST;
import static com.example.android.rookmusicplayer.App.FROM_LIBRARY;
import static com.example.android.rookmusicplayer.App.FROM_PLAYLIST;
import static com.example.android.rookmusicplayer.App.mediaBrowserHelper;
import static com.example.android.rookmusicplayer.App.playlists;

public class MediaControlDialog extends AlertDialog implements PlaylistDialogAdapter.ListItemClickListener
{
    private final String TAG = MediaControlDialog.class.getSimpleName();

    private static final int SONG = 1;
    private static final int ALBUM = 2;
    private static final int ARTIST = 3;
    private static final int PLAYLIST = 4;
    private static final int PLAYLIST_SONG = 5;

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
    private GetMedia getMedia;
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
        void updateAlbumsLibrary(Albums album, ArrayList<Songs> albumSongs);
        void updateArtistsLibrary(Artists artist, ArrayList<Albums> artistAlbums);
    }

    public interface UpdatePlaylist
    {
        void updatePlaylistCount();
    }

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
    }

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
    }

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
    }

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
    }

    public MediaControlDialog(Context context, Playlists playlist, ArrayList<Playlists> currentPlaylists, PlaylistsAdapter playlistsAdapter, int code)
    {
        super(context);
        this.playlist = playlist;
        this.context = context;
        this.currentPlaylists = currentPlaylists;
        this.playlistsAdapter = playlistsAdapter;
        this.code = code;
        media = PLAYLIST;
    }

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
                    setAlbumArt();
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
                Log.i(TAG, "DIALOG FROM LIBRARY");
                break;

            case FROM_ARTIST:
                Log.i(TAG, "DIALOG FROM ARTIST");
                break;

            case FROM_ALBUM:
                Log.i(TAG, "DIALOG FROM ALBUM");
                break;

            case FROM_PLAYLIST:
                Log.i(TAG, "DIALOG FROM PLAYLIST");
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
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(song.getPath());
        byte[] cover = retriever.getEmbeddedPicture();
        if(cover != null)
            mediaArt.setImageBitmap(BitmapFactory.decodeByteArray(cover, 0, cover.length));
        else
            mediaArt.setImageDrawable(context.getDrawable(R.drawable.noalbumart));
        retriever.release();
    }

    private void setAlbumArt()
    {
        try
        {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(album.getArt()));
            if(bitmap != null)
                mediaArt.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 450, 200, false));
            else
                mediaArt.setImageDrawable(context.getDrawable(R.drawable.noalbumart));
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void setPlaylistArt()
    {
        mediaArt.setImageDrawable(context.getDrawable(R.drawable.playlistart));
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
                Log.i(TAG, artist.getArtist().toUpperCase() + " ADDED TO " + playlist.getPlaylist().toUpperCase());
                break;

            case ALBUM:
                mediaBrowserHelper.addToPlaylist(playlist, album);
                Toast.makeText(context, album.getAlbum().toUpperCase() + " ADDED TO " + playlist.getPlaylist().toUpperCase(), Toast.LENGTH_SHORT).show();
                Log.i(TAG, album.getAlbum().toUpperCase() + " ADDED TO " + playlist.getPlaylist().toUpperCase());
                break;

            case PLAYLIST:
                mediaBrowserHelper.addToPlaylist(playlist, this.playlist);
                Toast.makeText(context, this.playlist.getPlaylist().toUpperCase() + " ADDED TO " + playlist.getPlaylist().toUpperCase(), Toast.LENGTH_SHORT).show();
                Log.i(TAG, this.playlist.getPlaylist().toUpperCase() + " ADDED TO " + playlist.getPlaylist().toUpperCase());
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
                ContentResolver contentResolver = context.getContentResolver();
                getMedia = new GetMedia(context);
                int removePosition;
                switch (media)
                {
                    case SONG:
                        alertDialog.dismiss();
                        removePosition = librarySongs.indexOf(song);
                        librarySongs.remove(song);
                        if(code == FROM_LIBRARY || code == FROM_ARTIST)
                            librarySongsAdapter.notifyItemRemoved(removePosition);
                        else if(code == FROM_ALBUM)
                            albumSongsAdapter.notifyItemRemoved(removePosition);

                        if(code == FROM_ALBUM || code == FROM_ARTIST)
                        {
                            updateLibrary.updateSongsLibrary(song);
                        }

                        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        String selectionSong = MediaStore.Audio.Media.DATA + "=?";
                        String[] selectionArgsSong = {song.getPath()};
                        contentResolver.delete(songUri, selectionSong, selectionArgsSong);

                        Toast.makeText(context, song.getTitle() + " DELETED", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, song.getTitle().toUpperCase() + " DELETED FROM LIBRARY");
                        break;

                    case ALBUM:
                        alertDialog.dismiss();
                        String albumName = album.getAlbum();
                        String albumKey = album.getKey();
                        ArrayList<Songs> albumSongs = getMedia.getAlbumSongs(album);

                        if(code == FROM_LIBRARY || code == FROM_ARTIST)
                        {
                            removePosition = libraryAlbums.indexOf(album);
                            libraryAlbums.remove(album);
                            libraryAlbumsAdapter.notifyItemRemoved(removePosition);
                        }

                        updateLibrary.updateAlbumsLibrary(album, albumSongs);

                        /*if(code == FROM_ARTIST || code == FROM_ALBUM)
                        {
                            updateLibrary.updateAlbumsLibrary(album, albumSongs);
                        }*/

                        Uri albumSongsUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        String selectionAlbumSongs = MediaStore.Audio.Media.DATA + "=?";
                        for(int i = 0; i < albumSongs.size(); i++)
                        {
                            String[] selectionArgsAlbumSongs = {albumSongs.get(i).getPath()};
                            contentResolver.delete(albumSongsUri, selectionAlbumSongs, selectionArgsAlbumSongs);
                        }


                        /*Uri albumUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
                        String selectionAlbum = MediaStore.Audio.Albums.ALBUM_KEY + "=?";
                        String[] selectionArgsAlbum = {albumKey};
                        contentResolver.delete(albumUri, selectionAlbum, selectionArgsAlbum);*/

                        Toast.makeText(context, albumName + " DELETED", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, albumName.toUpperCase() + " DELETED FROM LIBRARY");
                        break;

                    case ARTIST:
                        getMedia = new GetMedia(context, ID);
                        ArrayList<Songs> artistSongs = getMedia.getArtistSongs(artist);
                        ArrayList<Albums> artistAlbums = getMedia.getArtistAlbums();

                        alertDialog.dismiss();
                        removePosition = libraryArtists.indexOf(artist);
                        libraryArtists.remove(artist);
                        libraryArtistsAdapter.notifyItemRemoved(removePosition);
                        updateLibrary.updateArtistsLibrary(artist, artistAlbums);

                        Uri artistSongsUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        String selectionArtistSongs = MediaStore.Audio.Media.DATA + "=?";

                        for(int i = 0; i < artistSongs.size(); i++)
                        {
                            String[] selectionArgsArtistSongs = {artistSongs.get(i).getPath()};
                            contentResolver.delete(artistSongsUri, selectionArtistSongs, selectionArgsArtistSongs);
                        }


                        /*Uri artistAlbumsUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
                        String selectionArtistAlbums = MediaStore.Audio.Albums.ARTIST_ID + "=?";
                        String[] selectionArgsArtistAlbums = {String.valueOf(ID)};
                        contentResolver.delete(artistAlbumsUri, selectionArtistAlbums, selectionArgsArtistAlbums);

                        Uri artistUri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
                        String selectionArtist = MediaStore.Audio.Artists.ARTIST_KEY + "=?";
                        String[] selectionArgsArtist = {artist.getArtistKey()};
                        contentResolver.delete(artistUri, selectionArtist, selectionArgsArtist);*/

                        Toast.makeText(context, artist.getArtist() + " DELETED", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, artist.getArtist().toUpperCase() + " DELETED FROM LIBRARY");
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
                        Log.i(TAG, playlist.getPlaylist().toUpperCase() + " DELETED FROM PLAYLISTS");
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
                        Log.i(TAG, playlistSong.getTitle().toUpperCase() + " DELETED FROM PLAYLIST");
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

                            Log.i(TAG, "PLAYLIST " + playlistName.toUpperCase() + " CREATED WITH ID " + playlistID);
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
}

package com.rookieandroid.rookiemusicplayer.helpers;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.BaseColumns;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.rookieandroid.rookiemusicplayer.Albums;
import com.rookieandroid.rookiemusicplayer.Artists;
import com.rookieandroid.rookiemusicplayer.Playlists;
import com.rookieandroid.rookiemusicplayer.Songs;
import com.rookieandroid.rookiemusicplayer.architecture.LibraryViewModel;

import static com.rookieandroid.rookiemusicplayer.App.albums;
import static com.rookieandroid.rookiemusicplayer.App.artists;
import static com.rookieandroid.rookiemusicplayer.App.playlists;
import static com.rookieandroid.rookiemusicplayer.App.songs;


public class ReadStorage extends AsyncTaskLoader
{
    private LibraryViewModel libraryViewModel;
    public ReadStorage(@NonNull Context context, LibraryViewModel libraryViewModel)
    {
        super(context);
        this.libraryViewModel = libraryViewModel;
    }

    @Override
    protected void onStartLoading()
    {
        super.onStartLoading();
        forceLoad();
    }

    @Nullable
    @Override
    public Object loadInBackground()
    {
        getSongs();
        getArtists();
        getAlbums();
        getPlaylists();
        //SectionContent sectionContent = new SectionContent(albums, albumsSections);
        //sectionContent.sectionAlbums();
        return null;
    }

    private void getSongs()
    {
        ContentResolver contentResolver = getContext().getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");
        Cursor cursor = contentResolver.query(songUri, null, null, null, MediaStore.Audio.Media.TITLE + " ASC");
        if(cursor != null && cursor.moveToFirst())
        {
            int path = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int id = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int music = cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC);
            int album = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int albumId = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            int albumKey = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_KEY);
            int artist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int artistKey = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_KEY);
            int duration = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

            do
            {
                if(cursor.getString(music).equals("1"))
                {
                    String currentID = cursor.getString(id);
                    String currentTitle = cursor.getString(title);
                    String currentAlbum = cursor.getString(album);
                    long currentAlbumId = cursor.getLong(albumId);
                    String currentAlbumKey = cursor.getString(albumKey);
                    String currentArt = ContentUris.withAppendedId(albumArtUri, currentAlbumId).toString();
                    String currentArtist = cursor.getString(artist);
                    String currentArtistKey = cursor.getString(artistKey);
                    String currentPath = cursor.getString(path);
                    long currentDuration = cursor.getLong(duration);
                    songs.add(new Songs(currentID, currentTitle, currentAlbum, currentAlbumKey, currentArt, currentArtist, currentArtistKey, currentDuration, currentPath, -1));
                }
            }while(cursor.moveToNext());
            libraryViewModel.setSongs(songs);
            cursor.close();
        }
    }

    private void getAlbums()
    {
        ContentResolver contentResolver = getContext().getContentResolver();
        Uri albumUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM, MediaStore.Audio.Albums.ALBUM_ID, MediaStore.Audio.Albums.ALBUM_KEY, MediaStore.Audio.Albums.ARTIST};
        Cursor cursor = contentResolver.query(albumUri, null, null, null, MediaStore.Audio.Albums.ALBUM + " ASC");
        if(cursor != null && cursor.moveToFirst())
        {
            Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");
            int id = cursor.getColumnIndex(MediaStore.Audio.Albums._ID);
            int album = cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM);
            int albumID;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                albumID = cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ID);
            else
                albumID = cursor.getColumnIndex(BaseColumns._ID);
            int albumKey = cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_KEY);
            int artist = cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST);

            do
            {
                String ID = cursor.getString(id);
                String currentAlbum = cursor.getString(album);
                int currentID = cursor.getInt(albumID);
                String currentAlbumKey = cursor.getString(albumKey);
                String currentArtist = cursor.getString(artist);
                String currentArt = ContentUris.withAppendedId(albumArtUri, currentID).toString();
                albums.add(new Albums(ID, currentAlbum, currentAlbumKey, currentArt, currentArtist));
            }while(cursor.moveToNext());
            libraryViewModel.setAlbums(albums);
            cursor.close();
        }
    }

    private void getArtists()
    {
        ContentResolver contentResolver = getContext().getContentResolver();
        Uri artistUri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.Artists.ARTIST, MediaStore.Audio.Artists.ARTIST_KEY};
        Cursor cursor = contentResolver.query(artistUri, projection, null, null, MediaStore.Audio.Artists.ARTIST + " ASC");
        if(cursor != null && cursor.moveToFirst())
        {
            int artist = cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST);
            int artistKey = cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST_KEY);
            do
            {
                String currentArtist = cursor.getString(artist);
                String currentArtistKey = cursor.getString(artistKey);
                artists.add(new Artists(currentArtist, currentArtistKey));
            }while(cursor.moveToNext());
            libraryViewModel.setArtists(artists);
            cursor.close();
        }
    }

    private void getPlaylists()
    {
        ContentResolver contentResolver = getContext().getContentResolver();
        Uri playlistUri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(playlistUri, null, null, null, MediaStore.Audio.Playlists.DATE_ADDED);
        if(cursor != null && cursor.moveToFirst())
        {
            int playlist = cursor.getColumnIndex(MediaStore.Audio.Playlists.NAME);
            int id = cursor.getColumnIndex(MediaStore.Audio.Playlists._ID);

            do
            {
                String NameDescription = cursor.getString(playlist);
                String[] parts = NameDescription.split("SPLITHERE");
                String currentPlaylist = parts[0];
                String currentDescription = parts[1];
                String currentId = cursor.getString(id);

                long playlistID = Long.parseLong(currentId);
                Uri playlistSongsUri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistID);
                Cursor c = contentResolver.query(playlistSongsUri, null, null, null, null);
                int songCount;
                if(c != null && c.moveToFirst())
                {
                    songCount = c.getCount();
                    c.close();
                }
                else
                    songCount = 0;
                playlists.add(new Playlists(currentPlaylist, currentId, currentDescription));
                //Log.i(TAG, "PLAYLIST: " + currentPlaylist.toUpperCase() + "\tID: " + currentId + "\tCOUNT: " + songCount);
            }while(cursor.moveToNext());
            libraryViewModel.setPlaylists(playlists);
            cursor.close();
        }
    }
}

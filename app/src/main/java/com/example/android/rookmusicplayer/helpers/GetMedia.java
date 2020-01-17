package com.example.android.rookmusicplayer.helpers;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.android.rookmusicplayer.Albums;
import com.example.android.rookmusicplayer.Artists;
import com.example.android.rookmusicplayer.Playlists;
import com.example.android.rookmusicplayer.Songs;

import java.util.ArrayList;

public class GetMedia
{
    Context context;
    private int ID;

    public GetMedia(Context context) { this.context = context; }
    public GetMedia(Context context, int ID) {this.context = context; this.ID = ID;}

    public ArrayList<Songs> getAlbumSongs(Albums current)
    {
        ArrayList<Songs> songs = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.ALBUM_KEY + "=?";
        String[] selectionArg = {current.getKey()};
        Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");
        Cursor cursor = contentResolver.query(songUri, null, selection, selectionArg, MediaStore.Audio.Media.TRACK + " ASC");
        if(cursor != null && cursor.moveToFirst())
        {
            int path = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int id = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int album = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int albumId = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            int albumKey = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_KEY);
            int artist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int artistKey = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_KEY);
            int duration = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int track = cursor.getColumnIndex(MediaStore.Audio.Media.TRACK);

            do
            {
                String currentID = cursor.getString(id);
                String currentTitle = cursor.getString(title);
                String currentAlbum = cursor.getString(album);
                String currentAlbumKey = cursor.getString(albumKey);
                long currentAlbumId = cursor.getLong(albumId);
                String currentArt = ContentUris.withAppendedId(albumArtUri, currentAlbumId).toString();
                String currentArtist = cursor.getString(artist);
                String currentArtistKey = cursor.getString(artistKey);
                String currentPath = cursor.getString(path);
                long currentDuration = cursor.getLong(duration);
                String currentTrack = cursor.getString(track);
                String[] trackParts;
                if(currentTrack != null && currentTrack.contains("/"))
                {
                    trackParts = currentTrack.split("/");
                    currentTrack = trackParts[0];
                }
                else if(currentTrack == null)
                    currentTrack = "-1";

                songs.add(new Songs(currentID, currentTitle, currentAlbum, currentAlbumKey, currentArt, currentArtist, currentArtistKey, currentDuration, currentPath, Integer.parseInt(currentTrack)));
            }while(cursor.moveToNext());
            cursor.close();
            return songs;
        }

        return new ArrayList<>();
    }

    public ArrayList<Songs> getArtistSongs(Artists current)
    {
        ArrayList<Songs> songs = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.ARTIST_KEY + "=?";
        String[] selectionArg = {current.getArtistKey()};
        Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");
        Cursor cursor = contentResolver.query(songUri, null, selection, selectionArg, MediaStore.Audio.Media.TITLE + " ASC");
        if(cursor != null && cursor.moveToFirst())
        {
            int path = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int id = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int album = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int albumId = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            int albumKey = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_KEY);
            int artist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int artistId = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID);
            int artistKey = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_KEY);
            int duration = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

            ID = cursor.getInt(artistId);

            do
            {
                String currentID = cursor.getString(id);
                String currentTitle = cursor.getString(title);
                String currentAlbum = cursor.getString(album);
                String currentAlbumKey = cursor.getString(albumKey);
                long currentAlbumId = cursor.getLong(albumId);
                String currentArt = ContentUris.withAppendedId(albumArtUri, currentAlbumId).toString();
                String currentArtist = cursor.getString(artist);
                String currentArtistKey = cursor.getString(artistKey);
                String currentPath = cursor.getString(path);
                long currentDuration = cursor.getLong(duration);
                songs.add(new Songs(currentID, currentTitle, currentAlbum, currentAlbumKey, currentArt, currentArtist, currentArtistKey, currentDuration, currentPath, -1));
            }while(cursor.moveToNext());
            cursor.close();
            return songs;
        }

        return new ArrayList<>();
    }

    public ArrayList<Songs> getPlaylistSongs(Playlists current)
    {
        ArrayList<Songs> songs = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        long playlistID = Long.parseLong(current.getId());
        Uri playlistSongsUri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistID);
        Cursor cursor = contentResolver.query(playlistSongsUri, null, null, null, null);
        if(cursor != null && cursor.moveToFirst())
        {
            int id = cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID);
            int order = cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.PLAY_ORDER);
            Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String selection = MediaStore.Audio.Media._ID + "=?";

            do
            {
                String currentID = cursor.getString(id);
                int currentOrder = cursor.getInt(order);

                Cursor c = contentResolver.query(songUri, null, selection, new String[]{currentID}, null);
                if(c != null && c.moveToFirst())
                {
                    int path = c.getColumnIndex(MediaStore.Audio.Media.DATA);
                    int title = c.getColumnIndex(MediaStore.Audio.Media.TITLE);
                    int album = c.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                    int albumKey = c.getColumnIndex(MediaStore.Audio.Media.ALBUM_KEY);
                    int artist = c.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                    int artistKey = c.getColumnIndex(MediaStore.Audio.Media.ARTIST_KEY);
                    int duration = c.getColumnIndex(MediaStore.Audio.Media.DURATION);

                    String currentTitle = c.getString(title);
                    String currentAlbum = c.getString(album);
                    String currentAlbumKey = c.getString(albumKey);
                    String currentArtist = c.getString(artist);
                    String currentArtistKey = c.getString(artistKey);
                    String currentPath = c.getString(path);
                    long currentDuration = c.getLong(duration);
                    songs.add(new Songs(currentID, currentTitle, currentAlbum, currentAlbumKey, null, currentArtist, currentArtistKey, currentDuration, currentPath, currentOrder));
                    c.close();
                }
            }while (cursor.moveToNext());
            cursor.close();
            return songs;
        }
        return new ArrayList<>();
    }

    public ArrayList<Songs> getRecentlyAddedSongs()
    {
        int count = 0;
        ArrayList<Songs> songs = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");
        Cursor cursor = contentResolver.query(songUri, null, null, null, MediaStore.Audio.Media.DATE_ADDED + " DESC");
        if(cursor != null && cursor.moveToFirst())
        {
            int path = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int id = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int album = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int albumId = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            int albumKey = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_KEY);
            int artist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int artistKey = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_KEY);
            int duration = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int date = cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED);

            do
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
                int currentDate = cursor.getInt(date);
                songs.add(new Songs(currentID, currentTitle, currentAlbum, currentAlbumKey, currentArt, currentArtist, currentArtistKey, currentDuration, currentPath, currentDate));
                count++;
            }while(cursor.moveToNext() && count < 50);
            cursor.close();
            return songs;
        }
        return new ArrayList<>();
    }

    public ArrayList<Albums> getArtistAlbums()
    {
        ArrayList<Albums> albums = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        Uri albumUri = MediaStore.Audio.Artists.Albums.getContentUri("external", ID);
        Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");
        Cursor cursor = contentResolver.query(albumUri, null, null, null, MediaStore.Audio.Artists.Albums.ALBUM + " ASC");
        if(cursor != null && cursor.moveToFirst())
        {
            int album = cursor.getColumnIndex(MediaStore.Audio.Artists.Albums.ALBUM);
            int albumID = cursor.getColumnIndex(MediaStore.Audio.Artists.Albums.ALBUM_ID);
            int albumKey = cursor.getColumnIndex(MediaStore.Audio.Artists.Albums.ALBUM_KEY);
            int artist = cursor.getColumnIndex(MediaStore.Audio.Artists.Albums.ARTIST);

            do
            {
                String currentAlbum = cursor.getString(album);
                long currentAlbumID = cursor.getLong(albumID);
                String currentAlbumKey = cursor.getString(albumKey);
                String currentArtist = cursor.getString(artist);
                String currentArt = ContentUris.withAppendedId(albumArtUri, currentAlbumID).toString();
                albums.add(new Albums(currentAlbum, currentAlbumKey, currentArt, currentArtist));
            }while(cursor.moveToNext());
            cursor.close();
            return albums;
        }
        return new ArrayList<>();
    }
}

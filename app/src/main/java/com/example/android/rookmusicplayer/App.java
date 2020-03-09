package com.example.android.rookmusicplayer;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.transition.ChangeBounds;
import android.transition.ChangeClipBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.rookmusicplayer.adapters.QueueAdapter;
import com.example.android.rookmusicplayer.helpers.MediaBrowserHelper;
import com.example.android.rookmusicplayer.helpers.MediaBrowserHelperAlt;
import com.example.android.rookmusicplayer.helpers.SectionContent;

import java.util.ArrayList;

public class App extends Application
{
    private final String TAG = App.class.getSimpleName();
    private SectionContent sectionContent;

    public static final String CHANNEL_1 = "channel_1";
    //public static MediaBrowserHelper mediaBrowserHelper;
    public static MediaBrowserHelperAlt mediaBrowserHelper;

    //LIBRARY
    public static ArrayList<Songs> songs = new ArrayList<>();
    public static ArrayList<Albums> albums = new ArrayList<>();
    public static ArrayList<AlbumsSections> albumsSections = new ArrayList<>();
    public static ArrayList<Artists> artists = new ArrayList<>();
    public static ArrayList<Playlists> playlists = new ArrayList<>();

    //QUEUE CONTENTS
    public static ArrayList<Songs> librarySongs = new ArrayList<>();
    public static ArrayList<Songs> artistSongs = new ArrayList<>();
    public static ArrayList<Songs> albumSongs = new ArrayList<>();
    public static ArrayList<Songs> playlistSongs = new ArrayList<>();
    public static ArrayList<Songs> searchSong = new ArrayList<>();
    public static ArrayList<Songs> addToQueue = new ArrayList<>();

    //SAVING AND RESTORING UI STATE
    public static ArrayList<Songs> savedSongs;
    public static ArrayList<SavedStateDetails> savedState;

    //UP NEXT
    public static ArrayList<Songs> queueDisplay = new ArrayList<>();
    public static QueueAdapter queueAdapter;
    public static String nowPlayingFrom;

    //FAST SCROLLING
    public static ArrayList<Integer> sectionsSongs = new ArrayList<>();
    public static ArrayList<String> lettersSongs = new ArrayList<>();
    public static ArrayList<Integer> sectionsArtists = new ArrayList<>();
    public static ArrayList<String> lettersArtists = new ArrayList<>();
    public static ArrayList<Integer> sectionsAlbums = new ArrayList<>();
    public static ArrayList<String> lettersAlbums = new ArrayList<>();

    //CONSTANTS
    public static final int SKIP_NEXT = 1;
    public static final int SKIP_PREVIOUS = 2;
    public static final int QUEUE_NEXT = 3;
    public static final int QUEUE_END = 4;
    public static final int FROM_LIBRARY = 5;
    public static final int FROM_ARTIST = 6;
    public static final int FROM_ALBUM = 7;
    public static final int FROM_SEARCH = 8;
    public static final int FROM_PLAYLIST = 9;
    public static final int TO_ARTIST = 10;
    public static final int TO_ALBUM = 11;
    public static final int ARTIST_DETAIL = 12;
    public static final int SEARCH = 13;
    public static final int RECENTLY_ADDED = 14;

    //COMMANDS AND RESULT CODES
    public static final String GET_CURRENT_POSITION = "1";
    public static final int RECEIVE_CURRENT_POSITION = 1;
    public static final String GET_ARTIST_ALBUM = "2";
    public static final int RECEIVE_ARTIST_ALBUM = 2;
    public static final String GET_QUEUE_POSITION = "3";
    public static final int RECEIVE_QUEUE_POSITION = 3;
    public static final String GET_PLAYBACKSTATE = "4";
    public static final int RECEIVE_PLAYBACKSTATE = 4;

    //CUSTOM ACTIONS
    public static final String ADD_SONG = "1";
    public static final String ADD_ALBUM_ARTIST_PLAYLIST = "2";
    public static final String SET_POSITION = "3";
    public static final String CLEAR = "4";
    public static final String ACTIVITY_RESTORE = "5";
    public static final String SET_QUEUE_LIBRARY = "6";
    public static final String SET_QUEUE_LIBRARY_SONGS = "7";
    public static final String SET_QUEUE_ARTIST = "8";
    public static final String SET_QUEUE_ALBUM = "9";
    public static final String SET_QUEUE_SEARCH = "10";
    public static final String SET_QUEUE_PLAYLIST = "11";
    public static final String SET_UP_NEXT = "12";
    public static final String QUEUE_CLICK = "13";
    public static final String SHUFFLE_QUEUE = "14";
    public static final String SAVE_QUEUE = "15";
    public static final String RESTORE_SAVED_QUEUE = "16";
    public static final String INITIALIZE_QUEUE_CHANGE = "17";
    public static final String SET_ELAPSED_TIME = "18";

    @Override
    public void onCreate()
    {
        super.onCreate();
        createNotificationChannels();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run()
            {
                getSongs(); getArtists(); getAlbums(); getPlaylists();
                sectionContent = new SectionContent(albums, albumsSections);
                sectionContent.sectionAlbums();
            }
        });
    }

    private void createNotificationChannels()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel_1 = new NotificationChannel(CHANNEL_1, "Now Playing", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if(notificationManager != null)
                notificationManager.createNotificationChannel(channel_1);
        }
    }

    private void getSongs()
    {
        ContentResolver contentResolver = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");
        Cursor cursor = contentResolver.query(songUri, null, null, null, MediaStore.Audio.Media.TITLE + " ASC");
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
                songs.add(new Songs(currentID, currentTitle, currentAlbum, currentAlbumKey, currentArt, currentArtist, currentArtistKey, currentDuration, currentPath, -1));
            }while(cursor.moveToNext());
            cursor.close();
        }
    }

    private void getAlbums()
    {
        ContentResolver contentResolver = getContentResolver();
        Uri albumUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.Albums.ALBUM, MediaStore.Audio.Albums.ALBUM_ID, MediaStore.Audio.Albums.ALBUM_KEY, MediaStore.Audio.Albums.ARTIST};
        Cursor cursor = contentResolver.query(albumUri, projection, null, null, MediaStore.Audio.Albums.ALBUM + " ASC");
        if(cursor != null && cursor.moveToFirst())
        {
            Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");
            int album = cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM);
            int albumID = cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ID);
            int albumKey = cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_KEY);
            int artist = cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST);

            do
            {
                String currentAlbum = cursor.getString(album);
                int currentID = cursor.getInt(albumID);
                String currentAlbumKey = cursor.getString(albumKey);
                String currentArtist = cursor.getString(artist);
                String currentArt = ContentUris.withAppendedId(albumArtUri, currentID).toString();
                albums.add(new Albums(currentAlbum, currentAlbumKey, currentArt, currentArtist));
            }while(cursor.moveToNext());
            cursor.close();
        }
    }

    private void getArtists()
    {
        ContentResolver contentResolver = getContentResolver();
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
            cursor.close();
        }
    }

    private void getPlaylists()
    {
        ContentResolver contentResolver = getContentResolver();
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
                Log.i(TAG, "PLAYLIST: " + currentPlaylist.toUpperCase() + "\tID: " + currentId + "\tCOUNT: " + songCount);

            }while(cursor.moveToNext());
            cursor.close();
        }
    }

    public static int calculateSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
    {
        final int width = options.outWidth;
        final int height = options.outHeight;
        int sampleSize = 1;

        if (height > reqHeight || width > reqWidth)
        {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / sampleSize) >= reqHeight && (halfWidth / sampleSize) >= reqWidth)
                sampleSize *= 2;
        }

        return sampleSize;
    }

    public static Bitmap calculateBitmapSize(Bitmap bitmap, int reqWidth, int reqHeight)
    {
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        int scaleWidth = bitmap.getWidth();
        int scaleHeight = bitmap.getHeight();
        int sampleSize = 1;

        if (height > reqHeight || width > reqWidth)
        {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / sampleSize) >= reqHeight && (halfWidth / sampleSize) >= reqWidth)
            {
                scaleWidth = halfWidth / sampleSize;
                scaleHeight = halfHeight / sampleSize;
                sampleSize *= 2;
            }
        }

        return Bitmap.createScaledBitmap(bitmap, scaleWidth, scaleHeight, false);
    }

    public static class DetailsTransition extends TransitionSet
    {
        public DetailsTransition()
        {
            setOrdering(ORDERING_TOGETHER);
            addTransition(new ChangeBounds()).addTransition(new ChangeTransform()).addTransition(new ChangeClipBounds()).addTransition(new ChangeImageTransform());
        }
    }

   public static ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback()
   {
        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder)
        {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target)
        {
            return queueAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction)
        {
            queueAdapter.onItemDismiss(viewHolder.getAdapterPosition());
        }

        @Override
        public boolean isLongPressDragEnabled()
        {
            return super.isLongPressDragEnabled();
        }

        @Override
        public boolean isItemViewSwipeEnabled()
        {
            return super.isItemViewSwipeEnabled();
        }
    });
}

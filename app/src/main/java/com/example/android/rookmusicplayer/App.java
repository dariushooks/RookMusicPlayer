package com.example.android.rookmusicplayer;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
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
import com.example.android.rookmusicplayer.helpers.MediaBrowserHelperMotion;

import java.util.ArrayList;

public class App extends Application
{
    private final String TAG = App.class.getSimpleName();

    public static final String CHANNEL_1 = "channel_1";
    public static MediaBrowserHelperMotion mediaBrowserHelper;

    //LOADERS
    public static final int READ_STORAGE_LOADER = 0;
    public static final int PLAYLIST_MEDIA_LOADER = 1;
    public static final int BROWSER_MEDIA_LOADER = 2;
    public static final int DIALOG_MEDIA_LOADER = 3;
    public static final int ALBUM_MEDIA_LOADER = 4;
    public static final int LIBRARY_MEDIA_LOADER = 5;
    public static final int ARTIST_MEDIA_LOADER = 6;


    //GET MEDIA
    public static final int GET_ALBUM_SONGS = 1;
    public static final int GET_ARTIST_SONGS = 2;
    public static final int GET_PLAYLIST_SONGS = 3;
    public static final int GET_RECENT_SONGS = 4;
    public static final int GET_ARTIST_ALBUMS = 5;

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
    public static final String SET_FROM = "19";

    @Override
    public void onCreate()
    {
        super.onCreate();
        createNotificationChannels();
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

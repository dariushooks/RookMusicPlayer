package com.rookieandroid.rookiemusicplayer.helpers;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.ColorStateList;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rookieandroid.rookiemusicplayer.Albums;
import com.rookieandroid.rookiemusicplayer.Artists;
import com.rookieandroid.rookiemusicplayer.MainActivity;
import com.rookieandroid.rookiemusicplayer.MediaPlaybackService;
import com.rookieandroid.rookiemusicplayer.Playlists;
import com.rookieandroid.rookiemusicplayer.R;
import com.rookieandroid.rookiemusicplayer.Songs;
import com.rookieandroid.rookiemusicplayer.adapters.QueueAdapter;
import com.rookieandroid.rookiemusicplayer.architecture.SavedDetails;
import com.rookieandroid.rookiemusicplayer.architecture.StateViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.rookieandroid.rookiemusicplayer.App.ACTIVITY_RESTORE;
import static com.rookieandroid.rookiemusicplayer.App.ADD_ALBUM_ARTIST_PLAYLIST;
import static com.rookieandroid.rookiemusicplayer.App.ADD_SONG;
import static com.rookieandroid.rookiemusicplayer.App.CLEAR;
import static com.rookieandroid.rookiemusicplayer.App.FROM_LIBRARY;
import static com.rookieandroid.rookiemusicplayer.App.GET_ALBUM_SONGS;
import static com.rookieandroid.rookiemusicplayer.App.GET_ARTIST_SONGS;
import static com.rookieandroid.rookiemusicplayer.App.GET_CURRENT_POSITION;
import static com.rookieandroid.rookiemusicplayer.App.GET_ARTIST_ALBUM;
import static com.rookieandroid.rookiemusicplayer.App.GET_PLAYLIST_SONGS;
import static com.rookieandroid.rookiemusicplayer.App.GET_QUEUE_POSITION;
import static com.rookieandroid.rookiemusicplayer.App.INITIALIZE_QUEUE_CHANGE;
import static com.rookieandroid.rookiemusicplayer.App.PLAYLIST_MEDIA_LOADER;
import static com.rookieandroid.rookiemusicplayer.App.PLAY_BUTTON_START;
import static com.rookieandroid.rookiemusicplayer.App.QUEUE_CLICK;
import static com.rookieandroid.rookiemusicplayer.App.QUEUE_END;
import static com.rookieandroid.rookiemusicplayer.App.BROWSER_MEDIA_LOADER;
import static com.rookieandroid.rookiemusicplayer.App.QUEUE_NEXT;
import static com.rookieandroid.rookiemusicplayer.App.RECEIVE_ARTIST_ALBUM;
import static com.rookieandroid.rookiemusicplayer.App.RECEIVE_PLAYBACKSTATE;
import static com.rookieandroid.rookiemusicplayer.App.RECEIVE_QUEUE_POSITION;
import static com.rookieandroid.rookiemusicplayer.App.RESTORE_SAVED_QUEUE;
import static com.rookieandroid.rookiemusicplayer.App.SAVE_QUEUE;
import static com.rookieandroid.rookiemusicplayer.App.SET_ELAPSED_TIME;
import static com.rookieandroid.rookiemusicplayer.App.SET_FROM;
import static com.rookieandroid.rookiemusicplayer.App.SET_POSITION;
import static com.rookieandroid.rookiemusicplayer.App.SET_QUEUE_LIBRARY;
import static com.rookieandroid.rookiemusicplayer.App.SET_UP_NEXT;
import static com.rookieandroid.rookiemusicplayer.App.addToQueue;
import static com.rookieandroid.rookiemusicplayer.App.currentState;
import static com.rookieandroid.rookiemusicplayer.App.itemTouchHelper;
import static com.rookieandroid.rookiemusicplayer.App.nowPlayingFrom;
import static com.rookieandroid.rookiemusicplayer.App.queueAdapter;
import static com.rookieandroid.rookiemusicplayer.App.queueDisplay;
import static com.rookieandroid.rookiemusicplayer.App.repeat;
import static com.rookieandroid.rookiemusicplayer.App.savedSongs;
import static com.rookieandroid.rookiemusicplayer.App.savedState;
import static com.rookieandroid.rookiemusicplayer.App.shuffle;

public class MediaBrowserHelperMotion implements QueueAdapter.ListItemClickListener
{
    private final String TAG = MediaBrowserHelperMotion.class.getSimpleName();

    private Context context;
    private MediaBrowserCompat mediaBrowserCompat;
    private MediaControllerCompat mediaControllerCompat;
    private int currentDuration;
    private int currentElapsed;
    private Songs artist_album;

    //ADDING TO QUEUE OR PLAYLIST
    private int content;
    private Songs song;

    private Albums album;
    private ArrayList<Songs> albumSongs;

    private Artists artist;
    private ArrayList<Songs> artistSongs;

    private Playlists playlist;
    private Playlists playlistToAdd;
    private ArrayList<Songs> playlistSongs;


    //UI
    private View rootView;
    private Handler timeHandler = new Handler();
    private AudioManager audioManager;
    private RecyclerView recyclerView;

    private ImageView nowPlayingArt;
    private CardView nowPlayingArtHolder;
    private TextView nowPlayingName;
    private TextView nowPlayingArtist;
    private ImageButton nowPlayingButton;
    private ImageButton nowPlayingForward;
    private MotionLayout motionLayout;
    private FrameLayout frameLayout;

    private TextView nowPlayingNameExpanded;
    private TextView nowPlayingArtistAlbumExpanded;
    private TextView nowPlayingDurationExpanded;
    private TextView nowPlayingElapsedExpanded;
    private TextView nowPlayingFromExpanded;
    private ImageButton nowPlayingButtonExpanded;
    private ImageButton nowPlayingBackExpanded;
    private ImageButton nowPlayingForwardExpanded;
    private ImageButton nowPlayingSlideDown;
    private ProgressBar progressBar;
    private SeekBar seekBar;
    private SeekBar volumeBar;
    private Button shuffleExpanded;
    private View shuffleBackground;
    private Button repeatExpanded;
    private View repeatBackground;
    private ImageView upNextQueue;
    private ImageView upNextBackground;
    private ImageView upNextShuffle;
    private View upNextShuffleBackground;
    private ImageView upNextRepeat;
    private View upNextRepeatBackground;
    private boolean upNextIsShowing;

    //SAVING UI STATE
    private int savedPosition;
    private int savedElapsed;
    private int savedDuration;
    private int savedShuffle;
    private int savedRepeat;
    private int savedPlayState;
    private String savedNowPlayingFrom;
    private int savedFrom;
    private StateViewModel stateViewModel;

    public MediaBrowserHelperMotion(Context context, View rootView, StateViewModel stateViewModel)
    {
        this.context = context;
        this.rootView = rootView;
        this.stateViewModel = stateViewModel;
    }

    public void onCreate()
    {
        mediaBrowserCompat = new MediaBrowserCompat(context, new ComponentName(context, MediaPlaybackService.class), connectionCallbacks, null);
        mediaBrowserCompat.connect();
    }

    public void onRestart()
    {
        if(!mediaBrowserCompat.isConnected())
            mediaBrowserCompat.connect();
    }

    public void onStop()
    {
        mediaControllerCompat.sendCommand(GET_QUEUE_POSITION, null, resultReceiver);
    }

    public void onDestroy()
    {
        if (MediaControllerCompat.getMediaController((Activity) context) != null)
            MediaControllerCompat.getMediaController((Activity) context).unregisterCallback(controllerCallbacks);
        mediaBrowserCompat.unsubscribe(mediaBrowserCompat.getRoot());
        mediaBrowserCompat.disconnect();
    }

    public void setVolumeBar()
    {
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volumeBar.setProgress(currentVolume);
    }

    private final MediaBrowserCompat.ConnectionCallback connectionCallbacks =
            new MediaBrowserCompat.ConnectionCallback()
            {
                @Override
                public void onConnected()
                {
                    String root = mediaBrowserCompat.getRoot();
                    mediaBrowserCompat.subscribe(root, subscriptionCallbacks);
                    try
                    {
                        // Get the token for the MediaSession
                        MediaSessionCompat.Token token = mediaBrowserCompat.getSessionToken();

                        // Create a MediaControllerCompat
                        mediaControllerCompat = new MediaControllerCompat(context, token);
                        mediaControllerCompat.registerCallback(controllerCallbacks);
                        mediaControllerCompat.getTransportControls().setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_NONE);
                        mediaControllerCompat.getTransportControls().setRepeatMode(PlaybackStateCompat.REPEAT_MODE_NONE);
                        // Save the controller
                        MediaControllerCompat.setMediaController((Activity) context, mediaControllerCompat);

                    } catch (RemoteException e) { /*Log.e(MainActivity.class.getSimpleName(), "Error creating controller", e);*/ }

                    // Finish building the UI
                    buildTransportControls();

                    if(!savedSongs.isEmpty() && !savedState.isEmpty())
                    {
                        if(currentState != PlaybackStateCompat.STATE_PLAYING)
                        {
                            savedPosition = savedState.get(0).getPosition();
                            savedElapsed = savedState.get(0).getElapsed();
                            savedDuration = savedState.get(0).getDuration();
                            savedFrom = savedState.get(0).getFrom();
                            shuffle = savedState.get(0).getShuffle();
                            mediaControllerCompat.getTransportControls().setShuffleMode(shuffle);
                            repeat = savedState.get(0).getRepeat();
                            mediaControllerCompat.getTransportControls().setRepeatMode(repeat);
                            currentState = savedState.get(0).getState();
                            nowPlayingFrom = savedState.get(0).getNow_playing_from();
                            mediaControllerCompat.getTransportControls().sendCustomAction(CLEAR, null);
                            Bundle queuePosition = new Bundle(); queuePosition.putInt("CURRENT_QUEUE_POSITION", savedPosition);
                            Bundle elapsedTime = new Bundle(); elapsedTime.putInt("CURRENT_ELAPSED_TIME", savedElapsed);
                            Bundle from = new Bundle(); from.putInt("CURRENT_FROM", savedFrom);
                            mediaControllerCompat.getTransportControls().sendCustomAction(SET_POSITION, queuePosition);
                            mediaControllerCompat.getTransportControls().sendCustomAction(SET_FROM, from);
                            mediaControllerCompat.getTransportControls().sendCustomAction(RESTORE_SAVED_QUEUE, null);
                            mediaControllerCompat.getTransportControls().sendCustomAction(SET_ELAPSED_TIME, elapsedTime);
                            progressBar.setMax(savedDuration);
                            seekBar.setMax(savedDuration);
                            String duration = calculateTime(savedDuration);
                            nowPlayingDurationExpanded.setText(duration);
                            progressBar.setProgress(savedElapsed);
                            seekBar.setProgress(savedElapsed);
                            String elapsed = calculateTime(savedElapsed);
                            nowPlayingElapsedExpanded.setText(elapsed);
                        }
                        mediaControllerCompat.getTransportControls().sendCustomAction(ACTIVITY_RESTORE, null);
                        mediaControllerCompat.getTransportControls().sendCustomAction(SET_UP_NEXT, null);
                        setBottomSheetQueue();
                    }
                }

                @Override
                public void onConnectionSuspended()
                {
                    // The Service has crashed. Disable transport controls until it automatically reconnects
                }

                @Override
                public void onConnectionFailed()
                {
                    // The Service has refused our connection
                }

            };

    private final MediaBrowserCompat.SubscriptionCallback subscriptionCallbacks =
            new MediaBrowserCompat.SubscriptionCallback()
            {
                @Override
                public void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children)
                {
                    super.onChildrenLoaded(parentId, children);
                    if(children == null || children.isEmpty())
                        return;
                    MediaBrowserCompat.MediaItem firstItem = children.get(0);
                    // Play the first item?
                    // Probably should check firstItem.isPlayable()
                }
            };

    private Runnable updateTime = new Runnable()
    {
        @Override
        public void run()
        {
            if(currentState == PlaybackStateCompat.STATE_PLAYING)
            {
                mediaControllerCompat.sendCommand(GET_CURRENT_POSITION, null, new ResultReceiver(new Handler())
                {
                    @Override
                    protected void onReceiveResult(int resultCode, Bundle resultData)
                    {
                        super.onReceiveResult(resultCode, resultData);
                        currentElapsed = resultData.getInt("currentPosition");
                        progressBar.setProgress(currentElapsed);
                        seekBar.setProgress(currentElapsed);
                        String elapsedTime = calculateTime(currentElapsed);
                        nowPlayingElapsedExpanded.setText(elapsedTime);
                    }
                });
                timeHandler.postDelayed(this, 1000);
            }
        }
    };

    private final ResultReceiver resultReceiver =
            new ResultReceiver(new Handler())
            {
                @Override
                protected void onReceiveResult(int resultCode, Bundle resultData)
                {
                    super.onReceiveResult(resultCode, resultData);
                    switch (resultCode)
                    {
                        /*case RECEIVE_CURRENT_POSITION:
                            currentElapsed = resultData.getInt("currentPosition");
                            progressBar.setProgress(currentElapsed);
                            seekBar.setProgress(currentElapsed);
                            String elapsedTime = calculateTime(currentElapsed);
                            nowPlayingElapsedExpanded.setText(elapsedTime);
                            break;*/

                        case RECEIVE_ARTIST_ALBUM: artist_album = resultData.getParcelable("currentArtistAlbum");
                            GoToDialog goToDialog = new GoToDialog(context, artist_album);
                            goToDialog.OpenDialog();
                            break;

                        case RECEIVE_QUEUE_POSITION:
                            savedPosition = resultData.getInt("queuePosition");
                            savedShuffle = shuffle;
                            savedRepeat = repeat;
                            savedPlayState = currentState;
                            savedElapsed = resultData.getInt("currentElapsed");
                            savedDuration = currentDuration;
                            savedNowPlayingFrom = nowPlayingFrom;
                            savedFrom = resultData.getInt("currentFrom");
                            SavedDetails details = new SavedDetails(savedPosition, savedShuffle, savedRepeat, savedPlayState, savedElapsed, savedDuration, savedNowPlayingFrom, savedFrom);
                            if(savedState.isEmpty())
                                stateViewModel.insert(details);
                            else
                            {
                                details.setId(savedState.get(0).getId());
                                stateViewModel.update(details);
                            }
                            mediaControllerCompat.getTransportControls().sendCustomAction(SAVE_QUEUE, null);
                            break;

                        case RECEIVE_PLAYBACKSTATE:
                            int state = resultData.getInt("currentState");
                            break;
                    }
                }
            };

    private final MediaControllerCompat.Callback controllerCallbacks =
            new MediaControllerCompat.Callback()
            {

                @Override
                public void onMetadataChanged(MediaMetadataCompat metadata)
                {
                    super.onMetadataChanged(metadata);

                    Uri uri = Uri.parse(metadata.getString(MediaMetadataCompat.METADATA_KEY_ART_URI));
                    Glide.with(context).load(uri).fallback(R.drawable.noalbumart).error(R.drawable.noalbumart).override(400).into(nowPlayingArt);

                    if(motionLayout.getCurrentState() == R.id.end && currentState == PlaybackStateCompat.STATE_PLAYING)
                        nowPlayingArtHolder.setCardElevation(30);
                    nowPlayingName.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
                    nowPlayingArtist.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));

                    nowPlayingNameExpanded.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
                    String artist = metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST);
                    String album = metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM);
                    String expanded = artist + " - " + album;
                    nowPlayingArtistAlbumExpanded.setText(expanded);

                    if (currentState == PlaybackStateCompat.STATE_PLAYING)
                    {
                        nowPlayingButton.setBackground(context.getResources().getDrawable(R.drawable.ic_pause));
                        nowPlayingButtonExpanded.setBackground(context.getResources().getDrawable(R.drawable.ic_pause));
                    }

                    else
                    {
                        nowPlayingButton.setBackground(context.getResources().getDrawable(R.drawable.ic_play));
                        nowPlayingButtonExpanded.setBackground(context.getResources().getDrawable(R.drawable.ic_play));
                    }

                    setShuffle(shuffle);
                    setRepeat(repeat);

                    currentDuration = (int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
                    progressBar.setMax(currentDuration);
                    seekBar.setMax(currentDuration);
                    String d = calculateTime(currentDuration);
                    nowPlayingDurationExpanded.setText(d);
                }

                @Override
                public void onRepeatModeChanged(int repeatMode)
                {
                    super.onRepeatModeChanged(repeatMode);
                    repeat = repeatMode;
                    setRepeat(repeatMode);
                }

                @Override
                public void onShuffleModeChanged(int shuffleMode)
                {
                    super.onShuffleModeChanged(shuffleMode);
                    shuffle = shuffleMode;
                    setShuffle(shuffleMode);
                }

                @Override
                public void onPlaybackStateChanged(PlaybackStateCompat state)
                {
                    super.onPlaybackStateChanged(state);
                    if(state == null)
                        return;
                    switch (state.getState())
                    {
                        case PlaybackStateCompat.STATE_PLAYING:
                            currentState = PlaybackStateCompat.STATE_PLAYING;
                            timeHandler.post(updateTime);
                            //nowPlayingNameExpanded.setSelected(true);
                            //nowPlayingArtistAlbumExpanded.setSelected(true);
                            nowPlayingButton.setBackground(context.getDrawable(R.drawable.ic_pause));
                            nowPlayingForward.setVisibility(View.VISIBLE);
                            nowPlayingButtonExpanded.setBackground(context.getDrawable(R.drawable.ic_pause));
                            if(motionLayout.getCurrentState() == R.id.end)
                            {
                                nowPlayingArtHolder.animate().scaleX(1.35f);
                                nowPlayingArtHolder.animate().scaleY(1.3f);
                                nowPlayingArtHolder.setCardElevation(30f);
                            }
                            break;

                        case PlaybackStateCompat.STATE_PAUSED:
                            currentState = PlaybackStateCompat.STATE_PAUSED;
                            //nowPlayingNameExpanded.setSelected(false);
                            //nowPlayingArtistAlbumExpanded.setSelected(false);
                            nowPlayingButton.setBackground(context.getDrawable(R.drawable.ic_play));
                            nowPlayingButtonExpanded.setBackground(context.getDrawable(R.drawable.ic_play));
                            if(motionLayout.getCurrentState() == R.id.end)
                            {
                                nowPlayingArtHolder.animate().scaleX(1f);
                                nowPlayingArtHolder.animate().scaleY(1f);
                                nowPlayingArtHolder.setCardElevation(0f);
                            }
                            break;
                    }
                }
            };

    @Override
    public void QueueListItemClick(int position)
    {
        Bundle click = new Bundle();
        click.putInt("clickedIndex", position);
        mediaControllerCompat.getTransportControls().sendCustomAction(QUEUE_CLICK, click);
        mediaControllerCompat.getTransportControls().playFromMediaId(queueDisplay.get(position).getId(), null);
        mediaControllerCompat.getTransportControls().sendCustomAction(SET_UP_NEXT, null);
    }

    private String calculateTime(int time)
    {
        String calculated;
        int minutes = time / 1000 / 60;
        int seconds = time/ 1000 % 60;
        if(seconds > 9)
            calculated = String.format(Locale.US, "%d:%d", minutes, seconds);
        else
            calculated = String.format(Locale.US, "%d:%d%d", minutes, 0, seconds);
        return calculated;
    }

    public MediaControllerCompat getMediaController() { return mediaControllerCompat; }

    public void CollapseBottomSheet() { motionLayout.transitionToState(R.id.start); }

    public void setBottomSheetQueue()
    {
        queueAdapter = new QueueAdapter(this);
        recyclerView.setAdapter(queueAdapter);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        mediaControllerCompat.getTransportControls().sendCustomAction(INITIALIZE_QUEUE_CHANGE, null);
        nowPlayingFromExpanded.setText(nowPlayingFrom);
    }

    private void setRepeat(int state)
    {
        switch(state)
        {
            case PlaybackStateCompat.REPEAT_MODE_ALL:
                repeatBackground.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.colorAccent)));
                repeatExpanded.setBackground(context.getDrawable(R.drawable.ic_repeat));
                repeatExpanded.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.white)));
                break;

            case PlaybackStateCompat.REPEAT_MODE_ONE:
                repeatBackground.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.colorAccent)));
                repeatExpanded.setBackground(context.getDrawable(R.drawable.ic_repeat_one));
                repeatExpanded.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.white)));
                break;

            case PlaybackStateCompat.REPEAT_MODE_NONE:
                repeatBackground.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.darkGray)));
                repeatExpanded.setBackground(context.getDrawable(R.drawable.ic_repeat));
                repeatExpanded.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.colorAccent)));
                break;
        }
    }

    private void setShuffle(int state)
    {
        switch(state)
        {
            case PlaybackStateCompat.SHUFFLE_MODE_ALL:
                shuffleBackground.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.colorAccent)));
                shuffleExpanded.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.white)));
                break;

            case PlaybackStateCompat.SHUFFLE_MODE_NONE:
                shuffleBackground.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.darkGray)));
                shuffleExpanded.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.colorAccent)));
                break;
        }
    }

    public void addNext(Songs song)
    {
        Bundle next = new Bundle();
        next.putParcelable("ADD_SONG", song);
        next.putInt("QUEUE_POSITION", QUEUE_NEXT);
        mediaControllerCompat.getTransportControls().sendCustomAction(ADD_SONG, next);
        Toast.makeText(context, "ADDED NEXT", Toast.LENGTH_SHORT).show();
    }

    public void addNext(Albums album)
    {
        this.album = album;
        content = GET_ALBUM_SONGS;
        LoaderManager.getInstance((MainActivity) context).initLoader(BROWSER_MEDIA_LOADER, null, addNextCallbacks);
    }

    public void addNext(Artists artist)
    {
        this.artist = artist;
        content = GET_ARTIST_SONGS;
        LoaderManager.getInstance((MainActivity) context).initLoader(BROWSER_MEDIA_LOADER, null, addNextCallbacks);
    }

    public void addNext(Playlists playlist)
    {
        this.playlist = playlist;
        content = GET_PLAYLIST_SONGS;
        LoaderManager.getInstance((MainActivity) context).initLoader(BROWSER_MEDIA_LOADER, null, addNextCallbacks);
    }

    public void addLast(Songs song)
    {
        Bundle end = new Bundle();
        end.putParcelable("ADD_SONG", song);
        end.putInt("QUEUE_POSITION", QUEUE_END);
        mediaControllerCompat.getTransportControls().sendCustomAction(ADD_SONG, end);
        Toast.makeText(context, "ADDED LAST", Toast.LENGTH_SHORT).show();
    }

    public void addLast(Albums album)
    {
        this.album = album;
        content = GET_ALBUM_SONGS;
        LoaderManager.getInstance((MainActivity) context).initLoader(BROWSER_MEDIA_LOADER, null, addLastCallbacks);
    }

    public void addLast(Artists artist)
    {
        this.artist = artist;
        content = GET_ARTIST_SONGS;
        LoaderManager.getInstance((MainActivity) context).initLoader(BROWSER_MEDIA_LOADER, null, addLastCallbacks);
    }

    public void addLast(Playlists playlist)
    {
        this.playlist = playlist;
        content = GET_PLAYLIST_SONGS;
        LoaderManager.getInstance((MainActivity) context).initLoader(BROWSER_MEDIA_LOADER, null, addLastCallbacks);
    }

    public void addToPlaylist(Playlists playlist, Songs song)
    {
        //GET SONGS CURRENTLY IN PLAYLIST AND ADD NEW SONG
        this.playlist = playlist;
        this.song = song;
        content = GET_PLAYLIST_SONGS;
        LoaderManager.getInstance((MainActivity) context).initLoader(PLAYLIST_MEDIA_LOADER, null, addToPlaylistCallbacks);
    }

    public void addToPlaylist(Playlists playlist, Albums album)
    {
        this.album = album;
        this.playlist = playlist;
        content = GET_ALBUM_SONGS;
        LoaderManager.getInstance((MainActivity) context).initLoader(BROWSER_MEDIA_LOADER, null, addToPlaylistCallbacks);
    }

    public void addToPlaylist(Playlists playlist, Artists artist)
    {
        this.artist = artist;
        this.playlist = playlist;
        content = GET_ARTIST_SONGS;
        LoaderManager.getInstance((MainActivity) context).initLoader(BROWSER_MEDIA_LOADER, null, addToPlaylistCallbacks);
    }

    public void addToPlaylist(Playlists playlist, Playlists playlistToAdd)
    {
        this.playlist = playlist;
        this.playlistToAdd = playlistToAdd;
        content = GET_PLAYLIST_SONGS;
        LoaderManager.getInstance((MainActivity) context).initLoader(BROWSER_MEDIA_LOADER, null, addToPlaylistCallbacks);
    }

    private LoaderManager.LoaderCallbacks<ArrayList> addNextCallbacks = new LoaderManager.LoaderCallbacks<ArrayList>()
    {
        @NonNull
        @Override
        public Loader<ArrayList> onCreateLoader(int id, @Nullable Bundle args)
        {
            switch(content)
            {
                case GET_ALBUM_SONGS: return new GetMedia(context, content, -1, album);
                case GET_ARTIST_SONGS: return new GetMedia(context, content, -1, artist);
                case GET_PLAYLIST_SONGS: return new GetMedia(context, content, -1, playlist);
                default: return new GetMedia(context, -1, -1);
            }
        }

        @Override
        public void onLoadFinished(@NonNull Loader<ArrayList> loader, ArrayList data)
        {
            switch(content)
            {
                case GET_ALBUM_SONGS:
                    albumSongs = data;
                    if(!albumSongs.isEmpty())
                    {
                        if(albumSongs.size() == 1)
                            addNext(albumSongs.get(0));
                        else
                        {
                            addToQueue.clear();
                            addToQueue = albumSongs;
                            Bundle next = new Bundle();
                            next.putInt("QUEUE_POSITION", QUEUE_NEXT);
                            mediaControllerCompat.getTransportControls().sendCustomAction(ADD_ALBUM_ARTIST_PLAYLIST, next);
                        }
                        Toast.makeText(context, "ADDED NEXT", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case GET_ARTIST_SONGS:
                    artistSongs = data;
                    if(!artistSongs.isEmpty())
                    {
                        if(artistSongs.size() == 1)
                            addNext(artistSongs.get(0));
                        else
                        {
                            addToQueue.clear();
                            addToQueue = artistSongs;
                            Bundle next = new Bundle();
                            next.putInt("QUEUE_POSITION", QUEUE_NEXT);
                            mediaControllerCompat.getTransportControls().sendCustomAction(ADD_ALBUM_ARTIST_PLAYLIST, next);
                        }
                        Toast.makeText(context, "ADDED NEXT", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case GET_PLAYLIST_SONGS:
                    playlistSongs = data;
                    if(!playlistSongs.isEmpty())
                    {
                        if(playlistSongs.size() == 1)
                            addNext(playlistSongs.get(0));
                        else
                        {
                            addToQueue.clear();
                            addToQueue = playlistSongs;
                            Bundle next = new Bundle();
                            next.putInt("QUEUE_POSITION", QUEUE_NEXT);
                            mediaControllerCompat.getTransportControls().sendCustomAction(ADD_ALBUM_ARTIST_PLAYLIST, next);
                        }
                    }
                    break;
            }
        }

        @Override
        public void onLoaderReset(@NonNull Loader<ArrayList> loader) { }
    };

    private LoaderManager.LoaderCallbacks<ArrayList> addLastCallbacks = new LoaderManager.LoaderCallbacks<ArrayList>()
    {
        @NonNull
        @Override
        public Loader<ArrayList> onCreateLoader(int id, @Nullable Bundle args)
        {
            switch(content)
            {
                case GET_ALBUM_SONGS: return new GetMedia(context, content, -1, album);
                case GET_ARTIST_SONGS: return new GetMedia(context, content, -1, artist);
                case GET_PLAYLIST_SONGS: return new GetMedia(context, content, -1, playlist);
                default: return new GetMedia(context, -1, -1);
            }
        }

        @Override
        public void onLoadFinished(@NonNull Loader<ArrayList> loader, ArrayList data)
        {
            switch(content)
            {
                case GET_ALBUM_SONGS:
                    albumSongs = data;
                    if(!albumSongs.isEmpty())
                    {
                        if(albumSongs.size() == 1)
                            addLast(albumSongs.get(0));
                        else
                        {
                            addToQueue.clear();
                            addToQueue = albumSongs;
                            Bundle end = new Bundle();
                            end.putInt("QUEUE_POSITION", QUEUE_END);
                            mediaControllerCompat.getTransportControls().sendCustomAction(ADD_ALBUM_ARTIST_PLAYLIST, end);
                            Toast.makeText(context, "ADDED LAST", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;

                case GET_ARTIST_SONGS:
                    artistSongs = data;
                    if(!artistSongs.isEmpty())
                    {
                        if(artistSongs.size() == 1)
                            addLast(artistSongs.get(0));
                        else
                        {
                            addToQueue.clear();
                            addToQueue = artistSongs;
                            Bundle end = new Bundle();
                            end.putInt("QUEUE_POSITION", QUEUE_END);
                            mediaControllerCompat.getTransportControls().sendCustomAction(ADD_ALBUM_ARTIST_PLAYLIST, end);
                            Toast.makeText(context, "ADDED LAST", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;

                case GET_PLAYLIST_SONGS:
                    playlistSongs = data;
                    if(!playlistSongs.isEmpty())
                    {
                        if(playlistSongs.size() == 1)
                            addLast(playlistSongs.get(0));
                        else
                        {
                            addToQueue.clear();
                            addToQueue = playlistSongs;
                            Bundle end = new Bundle();
                            end.putInt("QUEUE_POSITION", QUEUE_END);
                            mediaControllerCompat.getTransportControls().sendCustomAction(ADD_ALBUM_ARTIST_PLAYLIST, end);
                            Toast.makeText(context, "ADDED LAST", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
            }
        }

        @Override
        public void onLoaderReset(@NonNull Loader<ArrayList> loader) { }
    };

    private LoaderManager.LoaderCallbacks<ArrayList> addToPlaylistCallbacks = new LoaderManager.LoaderCallbacks<ArrayList>()
    {
        @NonNull
        @Override
        public Loader<ArrayList> onCreateLoader(int id, @Nullable Bundle args)
        {
            switch(content)
            {
                case GET_ALBUM_SONGS: return new GetMedia(context, content, -1, album);
                case GET_ARTIST_SONGS: return new GetMedia(context, content, -1, artist);
                case GET_PLAYLIST_SONGS: return new GetMedia(context, content, -1, playlistToAdd);
                default: return new GetMedia(context, -1, -1);
            }
        }

        @Override
        public void onLoadFinished(@NonNull Loader<ArrayList> loader, ArrayList data)
        {
            switch(content)
            {
                case GET_ALBUM_SONGS:
                    albumSongs = data;
                    for (int i = 0; i < albumSongs.size(); i++)
                    {
                        addToPlaylist(playlist, albumSongs.get(i));
                    }
                    break;

                case GET_ARTIST_SONGS:
                    artistSongs = data;
                    for (int i = 0; i < artistSongs.size(); i++)
                    {
                        addToPlaylist(playlist, artistSongs.get(i));
                    }
                    break;

                case GET_PLAYLIST_SONGS:
                    playlistSongs = data;
                    if(loader.getId() == PLAYLIST_MEDIA_LOADER)
                    {
                        playlistSongs.add(song);

                        long playlistID = Long.parseLong(playlist.getId());
                        ContentResolver contentResolver = context.getContentResolver();
                        Uri playlistSongsUri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistID);

                        //DELETE SONGS IN PLAYLIST MEMBER TABLE
                        contentResolver.delete(playlistSongsUri, null, null);

                        //RECREATE PLAYLIST MEMBER TABLE WITH NEW SONG AND INSERT
                        ContentValues[] values = new ContentValues[playlistSongs.size()];

                        for(int i = 0; i < playlistSongs.size(); i++)
                        {
                            values[i] = new ContentValues();
                            values[i].put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, Long.valueOf(i));
                            values[i].put(MediaStore.Audio.Playlists.Members.AUDIO_ID, Long.parseLong(playlistSongs.get(i).getId()));
                            values[i].put(MediaStore.Audio.Playlists.Members.PLAYLIST_ID, playlistID);
                        }

                        contentResolver.bulkInsert(playlistSongsUri, values);

                        //Log.i(TAG, song.getTitle().toUpperCase() + " ADDED TO PLAYLIST " + playlist.getPlaylist().toUpperCase());
                    }

                    else
                    {
                        for (int i = 0; i < playlistSongs.size(); i++)
                        {
                            addToPlaylist(playlist, playlistSongs.get(i));
                        }
                    }
                    break;
            }
        }

        @Override
        public void onLoaderReset(@NonNull Loader<ArrayList> loader) { }
    };

    private void buildTransportControls()
    {
        ///////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////BOTTOM SHEET/////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////

        frameLayout = rootView.findViewById(R.id.fragment_container);

        recyclerView = rootView.findViewById(R.id.currentBottomSheetQueue);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        motionLayout = rootView.findViewById(R.id.bottomSheetPlaying);
        motionLayout.setTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int i, int i1)
            {
                if((motionLayout.getStartState() == R.id.end || motionLayout.getStartState() == R.id.start) && upNextIsShowing)
                {
                    upNextIsShowing = false;
                }

                else if(motionLayout.getEndState() == R.id.endQueue && !upNextIsShowing)
                {
                    upNextIsShowing = true;
                }
            }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int i, int i1, float v)
            {

            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int i)
            {
                if(motionLayout.getCurrentState() == R.id.start)
                {
                    nowPlayingArtHolder.animate().scaleX(1f);
                    nowPlayingArtHolder.animate().scaleY(1f);
                    nowPlayingArtHolder.setCardElevation(0f);
                }

                else if(motionLayout.getCurrentState() == R.id.endQueue)
                {
                    upNextShuffle.setAlpha(0f);
                    upNextShuffleBackground.setAlpha(0f);
                    upNextRepeat.setAlpha(0f);
                    upNextRepeatBackground.setAlpha(0f);
                    nowPlayingArtHolder.animate().scaleX(1f);
                    nowPlayingArtHolder.animate().scaleY(1f);
                    nowPlayingArtHolder.setCardElevation(0f);
                }

                else
                {
                    if(shuffle == PlaybackStateCompat.SHUFFLE_MODE_ALL)
                    {
                        upNextShuffle.setAlpha(1f);
                        upNextShuffleBackground.setAlpha(1f);
                    }

                    if(repeat == PlaybackStateCompat.REPEAT_MODE_ALL || repeat == PlaybackStateCompat.REPEAT_MODE_ONE)
                    {
                        if(repeat == PlaybackStateCompat.REPEAT_MODE_ONE)
                            upNextRepeat.setImageResource(R.drawable.ic_repeat_one);
                        else
                            upNextRepeat.setImageResource(R.drawable.ic_repeat);
                        upNextRepeat.setAlpha(1f);
                        upNextRepeatBackground.setAlpha(1f);
                    }

                    if(currentState == PlaybackStateCompat.STATE_PLAYING)
                    {
                        nowPlayingArtHolder.animate().scaleX(1.35f);
                        nowPlayingArtHolder.animate().scaleY(1.3f);
                        nowPlayingArtHolder.setCardElevation(30f);
                    }

                    else
                    {
                        nowPlayingArtHolder.animate().scaleX(1f);
                        nowPlayingArtHolder.animate().scaleY(1f);
                        nowPlayingArtHolder.setCardElevation(0f);
                    }
                }
            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int i, boolean b, float v)
            {

            }
        });

        nowPlayingArt = rootView.findViewById(R.id.currentArtBottomSheet);
        nowPlayingArtHolder = rootView.findViewById(R.id.currentArtBottomSheetHolder);
        nowPlayingName = rootView.findViewById(R.id.currentNameCollapsed);
        nowPlayingArtist = rootView.findViewById(R.id.currentArtistCollapsed);

        nowPlayingButton = rootView.findViewById(R.id.currentPlay);
        nowPlayingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(!nowPlayingName.getText().equals("Not Playing"))
                {
                    if(currentState == PlaybackStateCompat.STATE_PLAYING)
                    {
                        mediaControllerCompat.getTransportControls().pause();
                    }

                    else
                    {
                        mediaControllerCompat.getTransportControls().play();
                    }
                }

                else
                {
                    getMediaController().getTransportControls().sendCustomAction(CLEAR, null);
                    Bundle queue = new Bundle();
                    queue.putInt("CURRENT_QUEUE_POSITION", 0);
                    queue.putInt("FROM", FROM_LIBRARY);
                    getMediaController().getTransportControls().sendCustomAction(SET_POSITION, queue);
                    getMediaController().getTransportControls().sendCustomAction(SET_QUEUE_LIBRARY, queue);
                    nowPlayingFrom = "Now playing from Library";
                    getMediaController().getTransportControls().sendCustomAction(PLAY_BUTTON_START, null);
                    getMediaController().getTransportControls().sendCustomAction(SET_UP_NEXT, null);
                    setBottomSheetQueue();
                }
            }
        });

        nowPlayingForward = rootView.findViewById(R.id.currentNext);
        nowPlayingForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                mediaControllerCompat.getTransportControls().skipToNext();
            }
        });

        nowPlayingNameExpanded = rootView.findViewById(R.id.currentNameBottomSheet);
        nowPlayingElapsedExpanded = rootView.findViewById(R.id.currentTimeBottomSheet);
        nowPlayingDurationExpanded = rootView.findViewById(R.id.currentDurationBottomSheet);
        nowPlayingFromExpanded = rootView.findViewById(R.id.playingFrom);

        nowPlayingArtistAlbumExpanded = rootView.findViewById(R.id.currentArtistAlbumBottomSheet);
        nowPlayingArtistAlbumExpanded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                mediaControllerCompat.sendCommand(GET_ARTIST_ALBUM, null, resultReceiver);
            }
        });

        nowPlayingSlideDown = rootView.findViewById(R.id.slideDownBottomSheet);

        nowPlayingButtonExpanded = rootView.findViewById(R.id.currentPlayBottomSheet);
        nowPlayingButtonExpanded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(!nowPlayingName.getText().equals("Not Playing"))
                {
                    if(currentState == PlaybackStateCompat.STATE_PLAYING)
                    {
                        mediaControllerCompat.getTransportControls().pause();
                        nowPlayingArtHolder.setCardElevation(0f);
                    }

                    else
                    {
                        mediaControllerCompat.getTransportControls().play();
                        nowPlayingArtHolder.setCardElevation(30f);
                    }
                }

                else
                {
                    getMediaController().getTransportControls().sendCustomAction(CLEAR, null);
                    Bundle queue = new Bundle();
                    queue.putInt("CURRENT_QUEUE_POSITION", 0);
                    queue.putInt("FROM", FROM_LIBRARY);
                    getMediaController().getTransportControls().sendCustomAction(SET_POSITION, queue);
                    getMediaController().getTransportControls().sendCustomAction(SET_QUEUE_LIBRARY, queue);
                    nowPlayingFrom = "Now playing from Library";
                    getMediaController().getTransportControls().sendCustomAction(PLAY_BUTTON_START, null);
                    getMediaController().getTransportControls().sendCustomAction(SET_UP_NEXT, null);
                    setBottomSheetQueue();
                }
            }
        });

        nowPlayingBackExpanded = rootView.findViewById(R.id.currentBackBottomSheet);
        nowPlayingBackExpanded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                mediaControllerCompat.getTransportControls().skipToPrevious();
            }
        });

        nowPlayingForwardExpanded = rootView.findViewById(R.id.currentForwardBottomSheet);
        nowPlayingForwardExpanded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                mediaControllerCompat.getTransportControls().skipToNext();
            }
        });

        progressBar = rootView.findViewById(R.id.collapsedSeekBar);
        seekBar = rootView.findViewById(R.id.currentSeekBottomSheet);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if(fromUser)
                {
                    mediaControllerCompat.getTransportControls().seekTo((long) progress);
                    seekBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });

        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        volumeBar = rootView.findViewById(R.id.volumeControlSeek);
        volumeBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volumeBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int volume, boolean fromUser)
            {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });

        shuffleExpanded = rootView.findViewById(R.id.currentBottomSheetShuffle);
        shuffleBackground = rootView.findViewById(R.id.shuffleBackground);
        shuffleExpanded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(shuffle == PlaybackStateCompat.SHUFFLE_MODE_NONE)
                {
                    mediaControllerCompat.getTransportControls().setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_ALL);
                }

                else if(shuffle == PlaybackStateCompat.SHUFFLE_MODE_ALL)
                {
                    mediaControllerCompat.getTransportControls().setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_NONE);
                }

                //setBottomSheetQueue();
            }
        });

        repeatExpanded = rootView.findViewById(R.id.currentBottomSheetRepeat);
        repeatBackground = rootView.findViewById(R.id.repeatBackground);
        repeatExpanded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(repeat == PlaybackStateCompat.REPEAT_MODE_NONE)
                {
                    mediaControllerCompat.getTransportControls().setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ALL);
                }

                else if(repeat == PlaybackStateCompat.REPEAT_MODE_ALL)
                {
                    mediaControllerCompat.getTransportControls().setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ONE);
                }

                else if(repeat == PlaybackStateCompat.REPEAT_MODE_ONE)
                {
                    mediaControllerCompat.getTransportControls().setRepeatMode(PlaybackStateCompat.REPEAT_MODE_NONE);
                }
            }
        });

        upNextQueue = rootView.findViewById(R.id.upNextButton);
        upNextBackground = rootView.findViewById(R.id.upNextBackground);
        upNextShuffle = rootView.findViewById(R.id.upNextShuffle);
        upNextShuffleBackground = rootView.findViewById(R.id.upNextShuffleBackground);
        upNextRepeat = rootView.findViewById(R.id.upNextRepeat);
        upNextRepeatBackground = rootView.findViewById(R.id.upNextRepeatBackground);

        //////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////BOTTOM SHEET/////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////
    }
}

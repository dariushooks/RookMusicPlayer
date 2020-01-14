package com.example.android.rookmusicplayer.helpers;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.rookmusicplayer.Albums;
import com.example.android.rookmusicplayer.Artists;
import com.example.android.rookmusicplayer.MainActivity;
import com.example.android.rookmusicplayer.MediaPlaybackService;
import com.example.android.rookmusicplayer.Playlists;
import com.example.android.rookmusicplayer.R;
import com.example.android.rookmusicplayer.Songs;
import com.example.android.rookmusicplayer.adapters.QueueAdapter;
import com.example.android.rookmusicplayer.architecture.SavedDetails;
import com.example.android.rookmusicplayer.architecture.StateViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.example.android.rookmusicplayer.App.ACTIVITY_RESTORE;
import static com.example.android.rookmusicplayer.App.ADD_ALBUM_ARTIST_PLAYLIST;
import static com.example.android.rookmusicplayer.App.ADD_SONG;
import static com.example.android.rookmusicplayer.App.CLEAR;
import static com.example.android.rookmusicplayer.App.GET_CURRENT_POSITION;
import static com.example.android.rookmusicplayer.App.GET_ARTIST_ALBUM;
import static com.example.android.rookmusicplayer.App.GET_PLAYBACKSTATE;
import static com.example.android.rookmusicplayer.App.GET_QUEUE_POSITION;
import static com.example.android.rookmusicplayer.App.INITIALIZE_QUEUE_CHANGE;
import static com.example.android.rookmusicplayer.App.QUEUE_CLICK;
import static com.example.android.rookmusicplayer.App.QUEUE_END;
import static com.example.android.rookmusicplayer.App.QUEUE_NEXT;
import static com.example.android.rookmusicplayer.App.RECEIVE_ARTIST_ALBUM;
import static com.example.android.rookmusicplayer.App.RECEIVE_CURRENT_POSITION;
import static com.example.android.rookmusicplayer.App.RECEIVE_PLAYBACKSTATE;
import static com.example.android.rookmusicplayer.App.RECEIVE_QUEUE_POSITION;
import static com.example.android.rookmusicplayer.App.RESTORE_SAVED_QUEUE;
import static com.example.android.rookmusicplayer.App.SAVE_QUEUE;
import static com.example.android.rookmusicplayer.App.SET_ELAPSED_TIME;
import static com.example.android.rookmusicplayer.App.SET_POSITION;
import static com.example.android.rookmusicplayer.App.SET_UP_NEXT;
import static com.example.android.rookmusicplayer.App.addToQueue;
import static com.example.android.rookmusicplayer.App.itemTouchHelper;
import static com.example.android.rookmusicplayer.App.nowPlayingFrom;
import static com.example.android.rookmusicplayer.App.queueAdapter;
import static com.example.android.rookmusicplayer.App.queueDisplay;
import static com.example.android.rookmusicplayer.App.savedSongs;
import static com.example.android.rookmusicplayer.App.savedState;

public class MediaBrowserHelper implements QueueAdapter.ListItemClickListener
{
    private final String TAG = MediaBrowserHelper.class.getSimpleName();

    private Context context;
    private MediaBrowserCompat mediaBrowserCompat;
    private MediaControllerCompat mediaControllerCompat;
    private GetMedia getMedia;
    private int currentState;
    private int repeat;
    private int shuffle;
    private int currentDuration;
    private int currentElapsed;
    private Songs artist_album;

    //UI
    private View rootView;
    private Handler myHandler = new Handler();
    private AudioManager audioManager;
    private RecyclerView recyclerView;

    private ImageView nowPlayingArt;
    private CardView nowPlayingArtHolder;
    private TextView nowPlayingName;
    private TextView nowPlayingArtist;
    private ImageButton nowPlayingButton;
    private ImageButton nowPlayingForward;
    private RelativeLayout currentlyPlaying;
    private RelativeLayout currentBottomSheet;

    private TextView nowPlayingNameExpanded;
    private TextView nowPlayingArtistAlbumExpanded;
    private TextView nowPlayingDurationExpanded;
    private TextView nowPlayingElapsedExpanded;
    private TextView nowPlayingFromExpanded;
    private ImageButton nowPlayingButtonExpanded;
    private ImageButton nowPlayingBackExpanded;
    private ImageButton nowPlayingForwardExpanded;
    private ImageButton nowPlayingSlideDown;
    private SeekBar seekBar;
    private SeekBar volumeBar;
    private Button shuffleExpanded;
    private Button repeatExpanded;
    private BottomSheetBehavior bottomSheetBehavior;

    //SAVING UI STATE
    private int savedPosition;
    private int savedElapsed;
    private int savedShuffle;
    private int savedRepeat;
    private int savedPlayState;
    private String savedNowPlayingFrom;
    private StateViewModel stateViewModel;

    public MediaBrowserHelper(Context context, View rootView, StateViewModel stateViewModel)
    {
        this.context = context;
        this.rootView = rootView;
        this.stateViewModel = stateViewModel;
        getMedia = new GetMedia(context);
    }

    public void onCreate()
    {
        mediaBrowserCompat = new MediaBrowserCompat(context, new ComponentName(context, MediaPlaybackService.class), connectionCallbacks, null);
    }

    public void onStart()
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

                    } catch (RemoteException e) { Log.e(MainActivity.class.getSimpleName(), "Error creating controller", e); }

                    // Finish building the UI
                    buildTransportControls();

                    if(!savedSongs.isEmpty() && !savedState.isEmpty())
                    {
                        savedPosition = savedState.get(0).getPosition();
                        savedElapsed = savedState.get(0).getElapsed();
                        currentDuration = savedSongs.get(savedPosition).getDuration().intValue();
                        shuffle = savedState.get(0).getShuffle();
                        mediaControllerCompat.getTransportControls().setShuffleMode(shuffle);
                        repeat = savedState.get(0).getRepeat();
                        mediaControllerCompat.getTransportControls().setRepeatMode(repeat);
                        currentState = savedState.get(0).getState();
                        nowPlayingFrom = savedState.get(0).getNow_playing_from();
                        mediaControllerCompat.getTransportControls().sendCustomAction(CLEAR, null);
                        Bundle queuePosition = new Bundle(); queuePosition.putInt("CURRENT_QUEUE_POSITION", savedPosition);
                        Bundle elapsedTime = new Bundle(); elapsedTime.putInt("CURRENT_ELAPSED_TIME", savedElapsed);
                        mediaControllerCompat.getTransportControls().sendCustomAction(SET_POSITION, queuePosition);
                        mediaControllerCompat.getTransportControls().sendCustomAction(RESTORE_SAVED_QUEUE, null);
                        if(currentState != PlaybackStateCompat.STATE_PLAYING)
                        {

                            mediaControllerCompat.getTransportControls().sendCustomAction(SET_ELAPSED_TIME, elapsedTime);
                            seekBar.setMax(currentDuration);
                            String duration = calculateTime(currentDuration);
                            nowPlayingDurationExpanded.setText(duration);
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
                mediaControllerCompat.sendCommand(GET_CURRENT_POSITION, null, resultReceiver);
                myHandler.postDelayed(this, 1000);
            }

            else
            {
                myHandler.removeCallbacks(this);
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
                        case RECEIVE_CURRENT_POSITION:
                            currentElapsed = resultData.getInt("currentPosition");
                            seekBar.setProgress(currentElapsed);
                            String elapsedTime = calculateTime(currentElapsed);
                            nowPlayingElapsedExpanded.setText(elapsedTime);
                            break;

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
                            savedNowPlayingFrom = nowPlayingFrom;
                            SavedDetails details = new SavedDetails(savedPosition, savedShuffle, savedRepeat, savedPlayState, savedElapsed, savedNowPlayingFrom);
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
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    String source = metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID);
                    retriever.setDataSource(source);
                    byte[] cover = retriever.getEmbeddedPicture();
                    if (cover != null)
                        nowPlayingArt.setImageBitmap(BitmapFactory.decodeByteArray(cover, 0, cover.length));
                    else
                        nowPlayingArt.setImageDrawable(context.getDrawable(R.drawable.noalbumart));
                    retriever.release();
                    if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED && currentState == PlaybackStateCompat.STATE_PLAYING)
                        nowPlayingArtHolder.setCardElevation(30);
                    nowPlayingName.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
                    nowPlayingArtist.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));

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

                    if(shuffle == PlaybackStateCompat.SHUFFLE_MODE_ALL)
                    {
                        shuffleExpanded.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.colorAccent)));
                        shuffleExpanded.setTextColor(context.getColor(R.color.darkGray));
                        shuffleExpanded.getCompoundDrawables()[0].setTint(context.getColor(R.color.darkGray));
                    }

                    else if(shuffle == PlaybackStateCompat.SHUFFLE_MODE_NONE)
                    {
                        shuffleExpanded.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.darkGray)));
                        shuffleExpanded.setTextColor(context.getColor(R.color.colorAccent));
                        shuffleExpanded.getCompoundDrawables()[0].setTint(context.getColor(R.color.colorAccent));
                    }

                    if(repeat == PlaybackStateCompat.REPEAT_MODE_NONE)
                    {
                        repeatExpanded.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.darkGray)));
                        repeatExpanded.setTextColor(context.getColor(R.color.colorAccent));
                        repeatExpanded.setCompoundDrawablesWithIntrinsicBounds(context.getDrawable(R.drawable.ic_repeat), null, null, null);
                        repeatExpanded.getCompoundDrawables()[0].setTint(context.getColor(R.color.colorAccent));
                    }

                    else if(repeat == PlaybackStateCompat.REPEAT_MODE_ALL)
                    {
                        repeatExpanded.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.colorAccent)));
                        repeatExpanded.setTextColor(context.getColor(R.color.darkGray));
                        repeatExpanded.setCompoundDrawablesWithIntrinsicBounds(context.getDrawable(R.drawable.ic_repeat), null, null, null);
                        repeatExpanded.getCompoundDrawables()[0].setTint(context.getColor(R.color.darkGray));
                    }

                    else if(repeat == PlaybackStateCompat.REPEAT_MODE_ONE)
                    {
                        repeatExpanded.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.colorAccent)));
                        repeatExpanded.setTextColor(context.getColor(R.color.darkGray));
                        repeatExpanded.setCompoundDrawablesWithIntrinsicBounds(context.getDrawable(R.drawable.ic_repeat_one), null, null, null);
                        repeatExpanded.getCompoundDrawables()[0].setTint(context.getColor(R.color.darkGray));
                    }

                    nowPlayingNameExpanded.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
                    nowPlayingNameExpanded.setSelected(true);

                    String artist = metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST);
                    String album = metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM);
                    String expanded = artist + " - " + album;
                    nowPlayingArtistAlbumExpanded.setText(expanded);
                    nowPlayingArtistAlbumExpanded.setSelected(true);

                    currentDuration = (int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
                    seekBar.setMax(currentDuration);
                    String d = calculateTime(currentDuration);
                    nowPlayingDurationExpanded.setText(d);
                }

                @Override
                public void onRepeatModeChanged(int repeatMode)
                {
                    super.onRepeatModeChanged(repeatMode);
                    switch (repeatMode)
                    {
                        case PlaybackStateCompat.REPEAT_MODE_ALL:
                            repeat = PlaybackStateCompat.REPEAT_MODE_ALL;
                            repeatExpanded.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.colorAccent)));
                            repeatExpanded.setTextColor(context.getColor(R.color.darkGray));
                            repeatExpanded.setCompoundDrawablesWithIntrinsicBounds(context.getDrawable(R.drawable.ic_repeat), null, null, null);
                            repeatExpanded.getCompoundDrawables()[0].setTint(context.getColor(R.color.darkGray));
                            break;

                        case PlaybackStateCompat.REPEAT_MODE_ONE:
                            repeat = PlaybackStateCompat.REPEAT_MODE_ONE;
                            repeatExpanded.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.colorAccent)));
                            repeatExpanded.setTextColor(context.getColor(R.color.darkGray));
                            repeatExpanded.setCompoundDrawablesWithIntrinsicBounds(context.getDrawable(R.drawable.ic_repeat_one), null, null, null);
                            repeatExpanded.getCompoundDrawables()[0].setTint(context.getColor(R.color.darkGray));
                            break;

                        case PlaybackStateCompat.REPEAT_MODE_NONE:
                            repeat = PlaybackStateCompat.REPEAT_MODE_NONE;
                            repeatExpanded.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.darkGray)));
                            repeatExpanded.setTextColor(context.getColor(R.color.colorAccent));
                            repeatExpanded.setCompoundDrawablesWithIntrinsicBounds(context.getDrawable(R.drawable.ic_repeat), null, null, null);
                            repeatExpanded.getCompoundDrawables()[0].setTint(context.getColor(R.color.colorAccent));
                            break;
                    }
                }

                @Override
                public void onShuffleModeChanged(int shuffleMode)
                {
                    super.onShuffleModeChanged(shuffleMode);
                    switch (shuffleMode)
                    {
                        case PlaybackStateCompat.SHUFFLE_MODE_ALL:
                            shuffle = PlaybackStateCompat.SHUFFLE_MODE_ALL;
                            shuffleExpanded.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.colorAccent)));
                            shuffleExpanded.setTextColor(context.getColor(R.color.darkGray));
                            shuffleExpanded.getCompoundDrawables()[0].setTint(context.getColor(R.color.darkGray));
                            break;

                        case PlaybackStateCompat.SHUFFLE_MODE_NONE:
                            shuffle = PlaybackStateCompat.SHUFFLE_MODE_NONE;
                            shuffleExpanded.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.darkGray)));
                            shuffleExpanded.setTextColor(context.getColor(R.color.colorAccent));
                            shuffleExpanded.getCompoundDrawables()[0].setTint(context.getColor(R.color.colorAccent));
                            break;
                    }
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
                            myHandler.post(updateTime);
                            nowPlayingNameExpanded.setSelected(true);
                            nowPlayingArtistAlbumExpanded.setSelected(true);
                            nowPlayingButton.setBackground(context.getResources().getDrawable(R.drawable.ic_pause));
                            nowPlayingForward.setVisibility(View.VISIBLE);
                            nowPlayingButtonExpanded.setBackground(context.getResources().getDrawable(R.drawable.ic_pause));
                            if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
                            {
                                nowPlayingArtHolder.setCardElevation(30f);
                                nowPlayingArtHolder.animate().scaleX(6.75f);
                                nowPlayingArtHolder.animate().scaleY(6.75f);
                            }
                            break;

                        case PlaybackStateCompat.STATE_PAUSED:
                            currentState = PlaybackStateCompat.STATE_PAUSED;
                            nowPlayingNameExpanded.setSelected(false);
                            nowPlayingArtistAlbumExpanded.setSelected(false);
                            nowPlayingButton.setBackground(context.getResources().getDrawable(R.drawable.ic_play));
                            nowPlayingButtonExpanded.setBackground(context.getResources().getDrawable(R.drawable.ic_play));
                            if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
                            {
                                nowPlayingArtHolder.setCardElevation(0f);
                                nowPlayingArtHolder.animate().scaleX(5f);
                                nowPlayingArtHolder.animate().scaleY(5f);
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
        mediaControllerCompat.getTransportControls().playFromMediaId(queueDisplay.get(position).getPath(), null);
        mediaControllerCompat.getTransportControls().sendCustomAction(SET_UP_NEXT, null);
    }

    @Override
    public void OrderLongClick(int position)
    {

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

    public void CollapseBottomSheet() { bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED); }

    public void setBottomSheetQueue()
    {
        queueAdapter = new QueueAdapter(this);
        recyclerView.setAdapter(queueAdapter);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        mediaControllerCompat.getTransportControls().sendCustomAction(INITIALIZE_QUEUE_CHANGE, null);
        nowPlayingFromExpanded.setText(nowPlayingFrom);
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
        ArrayList<Songs> albumSongs = getMedia.getAlbumSongs(album);
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

    }

    public void addNext(Artists artist)
    {
        ArrayList<Songs> artistSongs = getMedia.getArtistSongs(artist);
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

    }

    public void addNext(Playlists playlist)
    {
        ArrayList<Songs> playlistSongs = getMedia.getPlaylistSongs(playlist);
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
        ArrayList<Songs> albumSongs = getMedia.getAlbumSongs(album);
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

    }

    public void addLast(Artists artist)
    {
        ArrayList<Songs> artistSongs = getMedia.getArtistSongs(artist);
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
    }

    public void addLast(Playlists playlist)
    {
        ArrayList<Songs> playlistSongs = getMedia.getPlaylistSongs(playlist);
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
    }

    public void addToPlaylist(Playlists playlist, Songs song)
    {
        //GET SONGS CURRENTLY IN PLAYLIST AND ADD NEW SONG
        ArrayList<Songs> playlistSongs = getMedia.getPlaylistSongs(playlist);
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

        Log.i(TAG, song.getTitle().toUpperCase() + " ADDED TO PLAYLIST " + playlist.getPlaylist().toUpperCase());

    }

    public void addToPlaylist(Playlists playlist, Albums album)
    {
        ArrayList<Songs> albumSongs = getMedia.getAlbumSongs(album);
        for (int i = 0; i < albumSongs.size(); i++)
        {
            addToPlaylist(playlist, albumSongs.get(i));
        }
    }

    public void addToPlaylist(Playlists playlist, Artists artist)
    {
        ArrayList<Songs> artistSongs = getMedia.getArtistSongs(artist);
        for (int i = 0; i < artistSongs.size(); i++)
        {
            addToPlaylist(playlist, artistSongs.get(i));
        }
    }

    public void addToPlaylist(Playlists playlist, Playlists playlistToAdd)
    {
        ArrayList<Songs> playlistSongs = getMedia.getPlaylistSongs(playlistToAdd);
        for (int i = 0; i < playlistSongs.size(); i++)
        {
            addToPlaylist(playlist, playlistSongs.get(i));
        }
    }

    private void buildTransportControls()
    {
        ///////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////BOTTOM SHEET/////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////

        recyclerView = rootView.findViewById(R.id.currentBottomSheetQueue);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        View view = rootView.findViewById(R.id.nowPlaying);
        bottomSheetBehavior = BottomSheetBehavior.from(view);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState)
            {
                switch (newState)
                {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        if(currentState == PlaybackStateCompat.STATE_PLAYING)
                        {
                            nowPlayingArtHolder.animate().scaleX(6.75f);
                            nowPlayingArtHolder.animate().scaleY(6.75f);
                            nowPlayingArtHolder.setCardElevation(30f);
                        }
                        else
                        {
                            nowPlayingArtHolder.animate().scaleX(5);
                            nowPlayingArtHolder.animate().scaleY(5);
                            nowPlayingArtHolder.setCardElevation(0f);
                        }
                        nowPlayingArtHolder.animate().translationY(575);
                        nowPlayingArtHolder.animate().translationX(600);
                        recyclerView.setFocusable(true);
                        break;

                    case BottomSheetBehavior.STATE_COLLAPSED:
                        nowPlayingArtHolder.animate().scaleX(1);
                        nowPlayingArtHolder.animate().scaleY(1);
                        nowPlayingArtHolder.setCardElevation(0f);
                        nowPlayingArtHolder.animate().translationY(0);
                        nowPlayingArtHolder.animate().translationX(0);
                        view.scrollTo(0,0);
                        recyclerView.setFocusable(false);
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset)
            {
                currentlyPlaying.setAlpha(1f - slideOffset);
                currentBottomSheet.setAlpha(slideOffset);
                nowPlayingSlideDown.setAlpha(slideOffset);
            }
        });

        nowPlayingArt = rootView.findViewById(R.id.currentArtBottomSheet);
        nowPlayingArtHolder = rootView.findViewById(R.id.currentArtBottomSheetHolder);
        nowPlayingName = rootView.findViewById(R.id.currentNameCollapsed);
        nowPlayingArtist = rootView.findViewById(R.id.currentArtistCollapsed);
        currentBottomSheet = rootView.findViewById(R.id.currentBottomSheet);

        currentlyPlaying = rootView.findViewById(R.id.currentPlaying);
        currentlyPlaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

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
                        nowPlayingNameExpanded.setSelected(false);
                        nowPlayingArtistAlbumExpanded.setSelected(false);
                        nowPlayingButton.setBackground(context.getDrawable(R.drawable.ic_play));
                        nowPlayingButtonExpanded.setBackground(context.getDrawable(R.drawable.ic_play));
                    }
                    else
                    {
                        mediaControllerCompat.getTransportControls().play();
                        nowPlayingNameExpanded.setSelected(true);
                        nowPlayingArtistAlbumExpanded.setSelected(true);
                        nowPlayingButton.setBackground(context.getDrawable(R.drawable.ic_pause));
                        nowPlayingButtonExpanded.setBackground(context.getDrawable(R.drawable.ic_pause));
                    }
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
        nowPlayingSlideDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

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
                        nowPlayingArtHolder.animate().scaleX(5);
                        nowPlayingArtHolder.animate().scaleY(5);
                        nowPlayingNameExpanded.setSelected(false);
                        nowPlayingArtistAlbumExpanded.setSelected(false);
                        nowPlayingButton.setBackground(context.getDrawable(R.drawable.ic_play));
                        nowPlayingButtonExpanded.setBackground(context.getDrawable(R.drawable.ic_play));
                    }
                    else
                    {
                        mediaControllerCompat.getTransportControls().play();
                        nowPlayingArtHolder.setCardElevation(30f);
                        nowPlayingArtHolder.animate().scaleX(6.75f);
                        nowPlayingArtHolder.animate().scaleY(6.75f);

                        nowPlayingNameExpanded.setSelected(true);
                        nowPlayingArtistAlbumExpanded.setSelected(true);
                        nowPlayingButton.setBackground(context.getDrawable(R.drawable.ic_pause));
                        nowPlayingButtonExpanded.setBackground(context.getDrawable(R.drawable.ic_pause));
                    }
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
                setBottomSheetQueue();
            }
        });

        repeatExpanded = rootView.findViewById(R.id.currentBottomSheetRepeat);
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

        //////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////BOTTOM SHEET/////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////
    }
}

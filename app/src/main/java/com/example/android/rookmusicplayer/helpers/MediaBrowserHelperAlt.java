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
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

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

public class MediaBrowserHelperAlt implements QueueAdapter.ListItemClickListener
{
    private final String TAG = MediaBrowserHelperAlt.class.getSimpleName();

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
    private MotionLayout motionLayout;
    private ConstraintLayout currentlyPlaying;
    private boolean bottomsheetIsExpanded;
    private ConstraintSet constraintSetCollapsed = new ConstraintSet();
    private ConstraintSet constraintSetExpandedNotPlaying = new ConstraintSet();
    private ConstraintSet constraintSetExpandedPlaying = new ConstraintSet();

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
    private Button upNextQueue;
    private View upNextBackground;
    private boolean upNextIsShowing;
    private BottomSheetBehavior bottomSheetBehavior;

    //SAVING UI STATE
    private int savedPosition;
    private int savedElapsed;
    private int savedDuration;
    private int savedShuffle;
    private int savedRepeat;
    private int savedPlayState;
    private String savedNowPlayingFrom;
    private StateViewModel stateViewModel;

    public MediaBrowserHelperAlt(Context context, View rootView, StateViewModel stateViewModel)
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
                        savedDuration = savedState.get(0).getDuration();
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
                            progressBar.setProgress(currentElapsed);
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
                            savedDuration = currentDuration;
                            savedNowPlayingFrom = nowPlayingFrom;
                            SavedDetails details = new SavedDetails(savedPosition, savedShuffle, savedRepeat, savedPlayState, savedElapsed, savedDuration, savedNowPlayingFrom);
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
                    if(bottomsheetIsExpanded && currentState == PlaybackStateCompat.STATE_PLAYING)
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

                    setShuffle(shuffle);
                    setRepeat(repeat);

                    nowPlayingNameExpanded.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
                    nowPlayingNameExpanded.setSelected(true);

                    String artist = metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST);
                    String album = metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM);
                    String expanded = artist + " - " + album;
                    nowPlayingArtistAlbumExpanded.setText(expanded);
                    nowPlayingArtistAlbumExpanded.setSelected(true);

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
                            myHandler.post(updateTime);
                            nowPlayingNameExpanded.setSelected(true);
                            nowPlayingArtistAlbumExpanded.setSelected(true);
                            nowPlayingButton.setBackground(context.getResources().getDrawable(R.drawable.ic_pause));
                            nowPlayingForward.setVisibility(View.VISIBLE);
                            nowPlayingButtonExpanded.setBackground(context.getResources().getDrawable(R.drawable.ic_pause));
                            if(motionLayout.getCurrentState() == R.id.end)
                            {
                                nowPlayingArtHolder.animate().scaleX(1.3f);
                                nowPlayingArtHolder.animate().scaleY(1.3f);
                                nowPlayingArtHolder.setCardElevation(30f);
                            }
                            break;

                        case PlaybackStateCompat.STATE_PAUSED:
                            currentState = PlaybackStateCompat.STATE_PAUSED;
                            nowPlayingNameExpanded.setSelected(false);
                            nowPlayingArtistAlbumExpanded.setSelected(false);
                            nowPlayingButton.setBackground(context.getResources().getDrawable(R.drawable.ic_play));
                            nowPlayingButtonExpanded.setBackground(context.getResources().getDrawable(R.drawable.ic_play));
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

        motionLayout = rootView.findViewById(R.id.bottomSheetPlaying);
        motionLayout.setTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int i, int i1)
            {

            }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int i, int i1, float v)
            {

            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int i)
            {
                if(motionLayout.getCurrentState() == R.id.start || motionLayout.getCurrentState() == R.id.endQueue)
                {
                    nowPlayingArtHolder.animate().scaleX(1f);
                    nowPlayingArtHolder.animate().scaleY(1f);
                    nowPlayingArtHolder.setCardElevation(0f);
                }

                else
                {
                    if(currentState == PlaybackStateCompat.STATE_PLAYING)
                    {
                        nowPlayingArtHolder.animate().scaleX(1.3f);
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

        /*currentlyPlaying = rootView.findViewById(R.id.currentPlaying);
        constraintSetCollapsed.clone(currentlyPlaying);
        constraintSetExpandedNotPlaying.clone(context, R.layout.bottomsheet_expanded_not_playing);
        constraintSetExpandedPlaying.clone(context, R.layout.bottomsheet_expanded_playing);
        currentlyPlaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        View view = rootView.findViewById(R.id.nowPlaying);
        bottomSheetBehavior = BottomSheetBehavior.from(view);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState)
            {
                switch (newState)
                {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        view.setBackground(context.getDrawable(R.drawable.bottomsheet_rounded_corners));

                        if(currentState == PlaybackStateCompat.STATE_PLAYING)
                        {
                            TransitionManager.beginDelayedTransition(currentlyPlaying);
                            constraintSetExpandedPlaying.applyTo(currentlyPlaying);
                            nowPlayingArtHolder.setCardElevation(30f);
                        }

                        else
                        {
                            TransitionManager.beginDelayedTransition(currentlyPlaying);
                            constraintSetExpandedNotPlaying.applyTo(currentlyPlaying);
                            nowPlayingArtHolder.setCardElevation(0f);
                        }

                        bottomsheetIsExpanded = true;
                        recyclerView.setFocusable(true);
                        break;

                    case BottomSheetBehavior.STATE_COLLAPSED:
                        view.setBackground(context.getDrawable(R.drawable.bottomsheet_corners));
                        nowPlayingArtHolder.setCardElevation(0f);

                        TransitionManager.beginDelayedTransition(currentlyPlaying);
                        constraintSetCollapsed.applyTo(currentlyPlaying);

                        bottomsheetIsExpanded = false;
                        view.scrollTo(0,0);
                        recyclerView.setFocusable(false);
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset)
            {
                nowPlayingButton.setAlpha(1f - slideOffset);
                nowPlayingForward.setAlpha(1f - slideOffset);
                nowPlayingName.setAlpha(1f - slideOffset);
                nowPlayingArtist.setAlpha(1f - slideOffset);

                seekBar.setAlpha(slideOffset);
                volumeBar.setAlpha(slideOffset);
                nowPlayingNameExpanded.setAlpha(slideOffset);
                nowPlayingArtistAlbumExpanded.setAlpha(slideOffset);
                nowPlayingButtonExpanded.setAlpha(slideOffset);
                nowPlayingBackExpanded.setAlpha(slideOffset);
                nowPlayingForwardExpanded.setAlpha(slideOffset);
                nowPlayingSlideDown.setAlpha(slideOffset);
            }
        });*/

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
                        nowPlayingNameExpanded.setSelected(false);
                        nowPlayingArtistAlbumExpanded.setSelected(false);
                        nowPlayingButton.setBackground(context.getDrawable(R.drawable.ic_play));
                        nowPlayingButtonExpanded.setBackground(context.getDrawable(R.drawable.ic_play));
                    }

                    else
                    {
                        mediaControllerCompat.getTransportControls().play();
                        nowPlayingArtHolder.setCardElevation(30f);
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
                setBottomSheetQueue();
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

        /*upNextQueue = rootView.findViewById(R.id.upNextButton);
        upNextBackground = rootView.findViewById(R.id.upNextBackground);
        upNextQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(upNextIsShowing)
                {
                    upNextBackground.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.darkGray)));
                    upNextQueue.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.colorAccent)));
                    upNextIsShowing = false;
                }

                else
                {
                    upNextBackground.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.colorAccent)));
                    upNextQueue.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.white)));
                    upNextIsShowing = true;
                }
            }
        });*/

        //////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////BOTTOM SHEET/////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////
    }
}

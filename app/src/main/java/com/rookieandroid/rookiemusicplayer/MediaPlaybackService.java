package com.rookieandroid.rookiemusicplayer;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;

import com.rookieandroid.rookiemusicplayer.adapters.QueueAdapter;
import com.rookieandroid.rookiemusicplayer.architecture.SavedDetails;
import com.rookieandroid.rookiemusicplayer.architecture.StateViewModel;
import com.rookieandroid.rookiemusicplayer.helpers.ControlReceiver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import static com.rookieandroid.rookiemusicplayer.App.ACTIVITY_RESTORE;
import static com.rookieandroid.rookiemusicplayer.App.ADD_ALBUM_ARTIST_PLAYLIST;
import static com.rookieandroid.rookiemusicplayer.App.ADD_SONG;
import static com.rookieandroid.rookiemusicplayer.App.CHANNEL_1;
import static com.rookieandroid.rookiemusicplayer.App.CLEAR;
import static com.rookieandroid.rookiemusicplayer.App.FROM_ALBUM;
import static com.rookieandroid.rookiemusicplayer.App.FROM_PLAYLIST;
import static com.rookieandroid.rookiemusicplayer.App.GET_ARTIST_ALBUM;
import static com.rookieandroid.rookiemusicplayer.App.GET_CURRENT_POSITION;
import static com.rookieandroid.rookiemusicplayer.App.GET_PLAYBACKSTATE;
import static com.rookieandroid.rookiemusicplayer.App.GET_QUEUE_POSITION;
import static com.rookieandroid.rookiemusicplayer.App.INITIALIZE_QUEUE_CHANGE;
import static com.rookieandroid.rookiemusicplayer.App.QUEUE_CLICK;
import static com.rookieandroid.rookiemusicplayer.App.QUEUE_END;
import static com.rookieandroid.rookiemusicplayer.App.QUEUE_NEXT;
import static com.rookieandroid.rookiemusicplayer.App.RECEIVE_ARTIST_ALBUM;
import static com.rookieandroid.rookiemusicplayer.App.RECEIVE_CURRENT_POSITION;
import static com.rookieandroid.rookiemusicplayer.App.RECEIVE_PLAYBACKSTATE;
import static com.rookieandroid.rookiemusicplayer.App.RECEIVE_QUEUE_POSITION;
import static com.rookieandroid.rookiemusicplayer.App.RECENTLY_ADDED;
import static com.rookieandroid.rookiemusicplayer.App.RESTORE_SAVED_QUEUE;
import static com.rookieandroid.rookiemusicplayer.App.SAVE_QUEUE;
import static com.rookieandroid.rookiemusicplayer.App.SET_ELAPSED_TIME;
import static com.rookieandroid.rookiemusicplayer.App.SET_FROM;
import static com.rookieandroid.rookiemusicplayer.App.SET_POSITION;
import static com.rookieandroid.rookiemusicplayer.App.SET_QUEUE_ALBUM;
import static com.rookieandroid.rookiemusicplayer.App.SET_QUEUE_ARTIST;
import static com.rookieandroid.rookiemusicplayer.App.SET_QUEUE_LIBRARY;
import static com.rookieandroid.rookiemusicplayer.App.SET_QUEUE_LIBRARY_SONGS;
import static com.rookieandroid.rookiemusicplayer.App.SET_QUEUE_PLAYLIST;
import static com.rookieandroid.rookiemusicplayer.App.SET_QUEUE_SEARCH;
import static com.rookieandroid.rookiemusicplayer.App.SET_UP_NEXT;
import static com.rookieandroid.rookiemusicplayer.App.SHUFFLE_QUEUE;
import static com.rookieandroid.rookiemusicplayer.App.SKIP_NEXT;
import static com.rookieandroid.rookiemusicplayer.App.SKIP_PREVIOUS;
import static com.rookieandroid.rookiemusicplayer.App.addToQueue;
import static com.rookieandroid.rookiemusicplayer.App.albumSongs;
import static com.rookieandroid.rookiemusicplayer.App.artistSongs;
import static com.rookieandroid.rookiemusicplayer.App.librarySongs;
import static com.rookieandroid.rookiemusicplayer.App.nowPlayingFrom;
import static com.rookieandroid.rookiemusicplayer.App.playlistSongs;
import static com.rookieandroid.rookiemusicplayer.App.queueAdapter;
import static com.rookieandroid.rookiemusicplayer.App.queueDisplay;
import static com.rookieandroid.rookiemusicplayer.App.savedSongs;
import static com.rookieandroid.rookiemusicplayer.App.savedState;
import static com.rookieandroid.rookiemusicplayer.App.searchSong;
import static com.rookieandroid.rookiemusicplayer.App.songs;

public class MediaPlaybackService extends MediaBrowserServiceCompat implements AudioManager.OnAudioFocusChangeListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, QueueAdapter.QueueChange
{
    private static final String MY_MEDIA_ROOT_ID = "media_root_id";
    private static final String TAG = MediaPlaybackService.class.getSimpleName();

    private ArrayList<Songs> queue = new ArrayList<>();
    private int position;
    private int from;
    private int currentState = PlaybackStateCompat.STATE_NONE;
    private int shuffle;
    private int repeat;
    private int elapsed;
    private boolean repeatAll = false;
    private boolean repeatOne = false;
    private MediaSessionCompat mediaSession;
    private ComponentName mediaButtonReceiver;
    private PlaybackStateCompat.Builder stateBuilder;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private NotificationCompat.Builder builder;

    //SYNC UI STATE
    private StateViewModel stateViewModel;
    private int syncPosition;
    private int syncElapsed;
    private int syncDuration;
    private int syncShuffle;
    private int syncRepeat;
    private int syncPlayState;
    private String syncNowPlayingFrom;
    private int syncFrom;

    @Override
    public void onCreate()
    {
        super.onCreate();

        //MediaPlayer
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioAttributes(new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build());
        mediaPlayer.setVolume(1f, 1f);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);

        // Create a MediaSessionCompat
        mediaButtonReceiver = new ComponentName(this, ControlReceiver.class);
        mediaSession = new MediaSessionCompat(this, MediaPlaybackService.class.getSimpleName(), mediaButtonReceiver, null);

        // Enable callbacks from MediaButtons and TransportControls
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS | MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
        stateBuilder = new PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_NONE, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0)
                .setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_PLAY);
        mediaSession.setPlaybackState(stateBuilder.build());

        // MySessionCallback() has methods that handle callbacks from a media controller
        mediaSession.setCallback(mediaCallbacks);
        mediaSession.setActive(true);

        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setClass(this, ControlReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0);
        mediaSession.setMediaButtonReceiver(pendingIntent);
        // Set the session's token so that client activities can communicate with it.
        setSessionToken(mediaSession.getSessionToken());

        //BroadcastReceiver
        //Handles headphones coming unplugged. cannot be done through a manifest receiver
        IntentFilter filter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(broadcastReceiver, filter);

        stateViewModel = new StateViewModel(getApplication());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        MediaButtonReceiver.handleIntent(mediaSession, intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        //Log.i(TAG, "SERVICE BEING DESTROYED");
        abandonAudioFocus();
        unregisterReceiver(broadcastReceiver);
        mediaPlayer.release();
        mediaSession.release();
        NotificationManagerCompat.from(this).cancel(1);
    }

    /*/////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
                                       SESSION CALLBACKS
    ///////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////*/

    private MediaSessionCompat.Callback mediaCallbacks = new MediaSessionCompat.Callback()
    {
        @Override
        public void onPlay()
        {
            super.onPlay();
            if(successfullyRetrievedAudioFocus() == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
            {
                startForegroundService(new Intent(MediaPlaybackService.this, MediaPlaybackService.class));
                mediaSession.setActive(true);
                mediaPlayer.start();

                //SET PLAYBACK STATE
                currentState = PlaybackStateCompat.STATE_PLAYING;
                stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, mediaPlayer.getCurrentPosition(), 1);
                stateBuilder.setActions(PlaybackStateCompat.ACTION_SEEK_TO | PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_PLAY_PAUSE |
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);
                mediaSession.setPlaybackState(stateBuilder.build());

                //SHOW PLAY NOTIFICATION
                buildNotification(MediaPlaybackService.this, queue.get(position));
                handler.post(updateQueueState);
            }
        }


        @Override
        public void onStop()
        {
            super.onStop();
            abandonAudioFocus();
            stopService(new Intent(MediaPlaybackService.this, MediaPlaybackService.class));
            mediaSession.setActive(false);
            mediaPlayer.stop();
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras)
        {
            super.onPlayFromMediaId(mediaId, extras);
            //Log.i(TAG, "Playing from media id: " + mediaId);
            if(successfullyRetrievedAudioFocus() == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
            {
                startForegroundService(new Intent(MediaPlaybackService.this, MediaPlaybackService.class));
                mediaSession.setActive(true);
                playSong(mediaId);
                buildMetadata(queue.get(position));

                //SET PLAYBACK STATE
                currentState = PlaybackStateCompat.STATE_PLAYING;
                stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, mediaPlayer.getCurrentPosition(), 1);
                stateBuilder.setActions(PlaybackStateCompat.ACTION_SEEK_TO | PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_PLAY_PAUSE |
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);
                mediaSession.setPlaybackState(stateBuilder.build());

                //SHOW PLAY NOTIFICATION
                buildNotification(MediaPlaybackService.this, queue.get(position));
                handler.post(updateQueueState);
            }
        }

        @Override
        public void onPause()
        {
            super.onPause();
            if(mediaPlayer.isPlaying())
            {
                mediaPlayer.pause();

                //SET PLAYBACK STATE
                currentState = PlaybackStateCompat.STATE_PAUSED;
                stateBuilder.setState(PlaybackStateCompat.STATE_PAUSED, mediaPlayer.getCurrentPosition(), 0);
                stateBuilder.setActions(PlaybackStateCompat.ACTION_SEEK_TO | PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PLAY_PAUSE |
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);
                mediaSession.setPlaybackState(stateBuilder.build());

                //SHOW PAUSE NOTIFICATION
                buildNotification(MediaPlaybackService.this, queue.get(position));
            }
        }

        @Override
        public void onCommand(String command, Bundle extras, ResultReceiver cb)
        {
            super.onCommand(command, extras, cb);
            switch (command)
            {
                case GET_CURRENT_POSITION:
                    Bundle currentPosition = new Bundle();
                    currentPosition.putInt("currentPosition", getCurrentPosition());
                    cb.send(RECEIVE_CURRENT_POSITION, currentPosition);
                    break;

                case GET_ARTIST_ALBUM:
                    Bundle currentArtistAlbum = new Bundle();
                    currentArtistAlbum.putParcelable("currentArtistAlbum", getCurrentArtistAlbum());
                    cb.send(RECEIVE_ARTIST_ALBUM, currentArtistAlbum);
                    break;

                case GET_QUEUE_POSITION:
                    Bundle queuePosition = new Bundle();
                    queuePosition.putInt("queuePosition", position);
                    queuePosition.putInt("currentElapsed", mediaPlayer.getCurrentPosition());
                    queuePosition.putInt("currentFrom", from);
                    cb.send(RECEIVE_QUEUE_POSITION, queuePosition);
                    break;

                case GET_PLAYBACKSTATE:
                    Bundle playbackState = new Bundle();
                    playbackState.putInt("currentState", currentState);
                    cb.send(RECEIVE_PLAYBACKSTATE, playbackState);
                    break;
            }
        }

        @Override
        public void onCustomAction(String action, Bundle extras)
        {
            super.onCustomAction(action, extras);
            switch (action)
            {
                case ADD_SONG: addSongToQueue(extras.getParcelable("ADD_SONG"), extras.getInt("QUEUE_POSITION")); break;
                case ADD_ALBUM_ARTIST_PLAYLIST: addAlbumOrArtistToQueue(extras.getInt("QUEUE_POSITION")); break;
                case SET_POSITION: setPosition(extras.getInt("CURRENT_QUEUE_POSITION")); break;
                case CLEAR: queue.clear(); break;
                case ACTIVITY_RESTORE: activityRestore(); break;
                case SET_QUEUE_LIBRARY: queue.addAll(songs); from = extras.getInt("FROM"); break;
                case SET_QUEUE_LIBRARY_SONGS: queue.addAll(librarySongs); from = extras.getInt("FROM"); break;
                case SET_QUEUE_ARTIST: queue.addAll(artistSongs); from = extras.getInt("FROM"); break;
                case SET_QUEUE_ALBUM: queue.addAll(albumSongs); from = extras.getInt("FROM"); break;
                case SET_QUEUE_SEARCH: queue.addAll(searchSong); from = extras.getInt("FROM"); break;
                case SET_QUEUE_PLAYLIST: queue.addAll(playlistSongs); from = extras.getInt("FROM"); break;
                case SET_UP_NEXT: setQueueDisplay(); break;
                case QUEUE_CLICK: queueClick(extras.getInt("clickedIndex")); break;
                case SHUFFLE_QUEUE: mediaSession.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_ALL); break;
                case SAVE_QUEUE: saveQueue(); break;
                case RESTORE_SAVED_QUEUE: queue.addAll(savedSongs); break;
                case INITIALIZE_QUEUE_CHANGE: queueAdapter.initializeQueueChange(MediaPlaybackService.this); break;
                case SET_ELAPSED_TIME: elapsed = extras.getInt("CURRENT_ELAPSED_TIME"); setSong(queue.get(position).getId()); break;
                case SET_FROM: from = extras.getInt("CURRENT_FROM"); break;
            }
        }

        @Override
        public void onSkipToPrevious()
        {
            super.onSkipToPrevious();
            if(!queue.isEmpty())
            {
                String mediaId;
                if(mediaPlayer.getCurrentPosition() < 5000 && position > 0)
                {
                    stateBuilder.setState(PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0);
                    mediaSession.setPlaybackState(stateBuilder.build());
                    updateQueueDisplay(SKIP_PREVIOUS);
                    position--;
                }

                mediaId = queue.get(position).getId();
                if(currentState == PlaybackStateCompat.STATE_PLAYING)
                    playSong(mediaId);
                else
                    setSong(mediaId);
                buildMetadata(queue.get(position));
                buildNotification(MediaPlaybackService.this, queue.get(position));
            }
        }

        @Override
        public void onSkipToNext()
        {
            super.onSkipToNext();
            if(repeatAll && position == queue.size() - 1)
            {
                position = 0;
                setQueueDisplay();
                position = -1;
            }

            if(!queue.isEmpty() && position < queue.size() - 1)
            {
                stateBuilder.setState(PlaybackStateCompat.STATE_SKIPPING_TO_NEXT, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0);
                mediaSession.setPlaybackState(stateBuilder.build());
                position++;
                String mediaId = queue.get(position).getId();
                if(currentState == PlaybackStateCompat.STATE_PLAYING)
                    playSong(mediaId);
                else
                    setSong(mediaId);
                buildMetadata(queue.get(position));
                updateQueueDisplay(SKIP_NEXT);
                buildNotification(MediaPlaybackService.this, queue.get(position));
            }
        }

        @Override
        public void onSeekTo(long pos)
        {
            super.onSeekTo(pos);
            mediaPlayer.seekTo((int)pos);
            stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, mediaPlayer.getCurrentPosition(), 1);
            stateBuilder.setActions(PlaybackStateCompat.ACTION_SEEK_TO | PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_PLAY_PAUSE |
                    PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);
            mediaSession.setPlaybackState(stateBuilder.build());
        }

        @Override
        public void onSetRepeatMode(int repeatMode)
        {
            super.onSetRepeatMode(repeatMode);
            mediaSession.setRepeatMode(repeatMode);
            repeat = repeatMode;
            switch (repeatMode)
            {
                case PlaybackStateCompat.REPEAT_MODE_ALL: repeatAll = true; break;
                case PlaybackStateCompat.REPEAT_MODE_ONE: repeatAll = false; repeatOne = true; break;
                case PlaybackStateCompat.REPEAT_MODE_NONE: repeatAll = false; repeatOne = false; break;
            }
        }

        @Override
        public void onSetShuffleMode(int shuffleMode)
        {
            super.onSetShuffleMode(shuffleMode);
            mediaSession.setShuffleMode(shuffleMode);
            shuffle = shuffleMode;
            Songs current;
            switch (shuffleMode)
            {
                case PlaybackStateCompat.SHUFFLE_MODE_ALL:
                    if(!queue.isEmpty())
                    {
                        current = queue.get(position);
                        queue.remove(position);
                        Collections.shuffle(queue);
                        queue.add(0, current); position = 0;
                        setQueueDisplay();
                    }
                    break;

                case PlaybackStateCompat.SHUFFLE_MODE_NONE:
                    if(!queue.isEmpty())
                    {
                        current = queue.get(position);
                        if(from == FROM_ALBUM || from == FROM_PLAYLIST)
                            Collections.sort(queue, Comparator.comparing(Songs::getTrack));
                        else if(from == RECENTLY_ADDED)
                            Collections.sort(queue, Comparator.comparing(Songs::getTrack).reversed());
                        else
                            Collections.sort(queue, Comparator.comparing(Songs::getTitle));
                        position = queue.indexOf(current);
                        setQueueDisplay();
                    }
                    break;
            }
            saveQueue();
        }
    };

    /*/////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////*/


    /*/////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
                                 METHODS FOR SAVING UI STATE
    ///////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////*/

    private void saveQueue()
    {
        stateViewModel.deleteSavedQueue();
        stateViewModel.insertAll(queue);
        /*for(int i = 0; i < queue.size(); i++)
        {
            String id = queue.get(i).getId();
            String title = queue.get(i).getTitle();
            String album = queue.get(i).getAlbum();
            String albumKey = queue.get(i).getAlbumKey();
            String art = queue.get(i).getArt();
            String artist = queue.get(i).getArtist();
            String artistKey = queue.get(i).getArtistKey();
            long duration = queue.get(i).getDuration();
            String path = queue.get(i).getPath();
            int track = queue.get(i).getTrack();
            stateViewModel.insert(new SavedQueue(id, title, album, albumKey, art, artist, artistKey, duration, path, track));
        }*/

        //Log.i(TAG, "QUEUE SAVED");
    }

    private void updateSavedState()
    {
        syncPosition = position;
        syncShuffle = shuffle;
        syncRepeat = repeat;
        syncPlayState = currentState;
        syncElapsed = mediaPlayer.getCurrentPosition();
        syncDuration = mediaPlayer.getDuration();
        syncNowPlayingFrom = nowPlayingFrom;
        syncFrom = from;
        SavedDetails details = new SavedDetails(syncPosition, syncShuffle, syncRepeat, syncPlayState, syncElapsed, syncDuration, syncNowPlayingFrom, syncFrom);
        if(savedState.isEmpty())
            stateViewModel.insert(details);
        else
        {
            details.setId(savedState.get(0).getId());
            stateViewModel.update(details);
        }

        //Log.i(TAG, "SAVED STATE UPDATED");
    }

    /*/////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////*/

    private void activityRestore()
    {
        if(mediaPlayer.isPlaying())
        {
            stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, mediaPlayer.getCurrentPosition(), 1);
            stateBuilder.setActions(PlaybackStateCompat.ACTION_SEEK_TO | PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);
            mediaSession.setPlaybackState(stateBuilder.build());
        }

        else
        {
            stateBuilder.setState(PlaybackStateCompat.STATE_PAUSED, mediaPlayer.getCurrentPosition(), 0);
            stateBuilder.setActions(PlaybackStateCompat.ACTION_SEEK_TO | PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);
            mediaSession.setPlaybackState(stateBuilder.build());
        }

        buildMetadata(queue.get(position));
    }

    private void buildMetadata(Songs currentSong)
    {
        MediaMetadataCompat.Builder metadata = new MediaMetadataCompat.Builder();
        metadata.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, currentSong.getId());
        metadata.putString(MediaMetadataCompat.METADATA_KEY_TITLE, currentSong.getTitle());
        metadata.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, currentSong.getArtist());
        metadata.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, currentSong.getAlbum());
        metadata.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, currentSong.getDuration());
        metadata.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, currentSong.getPath());
        metadata.putString(MediaMetadataCompat.METADATA_KEY_ART_URI, currentSong.getArt());
        mediaSession.setMetadata(metadata.build());
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if(mediaPlayer != null && mediaPlayer.isPlaying())
                mediaPlayer.pause();
        }
    };

    /*/////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
                                  METHODS FOR AUDIO FOCUS
    ///////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////*/

    private int successfullyRetrievedAudioFocus()
    {
        int result;
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        if(audioManager != null)
        {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
            {
                AudioAttributes playbackAttributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build();

                AudioFocusRequest focusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                        .setAudioAttributes(playbackAttributes)
                        .setAcceptsDelayedFocusGain(true)
                        .setOnAudioFocusChangeListener(this)
                        .build();
                result = audioManager.requestAudioFocus(focusRequest);
            }

            else
                result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }

        else
            result = AudioManager.AUDIOFOCUS_REQUEST_FAILED;

        return result;
    }

    private void abandonAudioFocus()
    {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if(audioManager != null)
            audioManager.abandonAudioFocus(this);
    }

    @Override
    public void onAudioFocusChange(int focusChange)
    {
        switch (focusChange)
        {
            case AudioManager.AUDIOFOCUS_LOSS:
            {
                if(mediaPlayer.isPlaying())
                    mediaPlayer.stop();
                break;
            }
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
            {
                mediaPlayer.pause();
                break;
            }
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
            {
                if( mediaPlayer != null )
                    mediaPlayer.setVolume(0.3f, 0.3f);
                break;
            }
            case AudioManager.AUDIOFOCUS_GAIN:
            {
                if( mediaPlayer != null )
                {
                    if( !mediaPlayer.isPlaying() )
                        mediaPlayer.start();
                    mediaPlayer.setVolume(1.0f, 1.0f);
                }
                break;
            }
        }
    }

    /*/////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////*/


    /*/////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
                                  METHODS FOR NOTIFICATIONS
    ///////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////*/

    private void buildNotification(Context context, Songs currentSong)
    {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Long.parseLong(currentSong.getId()));
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(context, uri);
        byte[] cover = retriever.getEmbeddedPicture();
        builder = new NotificationCompat.Builder(context, CHANNEL_1);
        builder.setSmallIcon(R.drawable.ic_noartistart);
        builder.setContentTitle(currentSong.getTitle())
                .setContentText(currentSong.getArtist() + " - " + currentSong.getAlbum());

        if(cover != null)
            builder.setLargeIcon(BitmapFactory.decodeByteArray(cover, 0, cover.length));
        else
            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.noalbumart));

        builder.setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        if(currentState == PlaybackStateCompat.STATE_PLAYING)
        {
            builder.setOngoing(true);
            builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0,1,2).setMediaSession(mediaSession.getSessionToken()));
            builder.addAction(R.drawable.ic_fast_rewind, "Back",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(MediaPlaybackService.this, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS));
            builder.addAction(R.drawable.ic_pause, "Pause",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(MediaPlaybackService.this, PlaybackStateCompat.ACTION_PLAY_PAUSE));
            builder.addAction(R.drawable.ic_fast_forward, "Forward",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(MediaPlaybackService.this, PlaybackStateCompat.ACTION_SKIP_TO_NEXT));
            startForeground(1, builder.build());
        }

        else
        {
            builder.setOngoing(false);
            builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1, 2).setMediaSession(mediaSession.getSessionToken()));
            builder.addAction(R.drawable.ic_fast_rewind, "Back",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(MediaPlaybackService.this, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS));
            builder.addAction(R.drawable.ic_play, "Play",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(MediaPlaybackService.this, PlaybackStateCompat.ACTION_PLAY_PAUSE));
            builder.addAction(R.drawable.ic_fast_forward, "Forward",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(MediaPlaybackService.this, PlaybackStateCompat.ACTION_SKIP_TO_NEXT));
            NotificationManagerCompat.from(MediaPlaybackService.this).notify(1, builder.build());
        }
    }

    private Runnable updateQueueState = new Runnable()
    {
        @Override
        public void run()
        {
            if(currentState == PlaybackStateCompat.STATE_PLAYING)
            {
                //Log.i(TAG, "Time Updating to " + calculateTime(mediaPlayer.getCurrentPosition()));
                updateSavedState();
                handler.postDelayed(this, 2000);
            }

            else
            {
                //Log.i(TAG, "Time Paused at " + calculateTime(mediaPlayer.getCurrentPosition()));
                updateSavedState();
                stopForeground(false);
            }
        }
    };

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

    /*/////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////*/

    private int getCurrentPosition(){ return mediaPlayer.getCurrentPosition(); }

    private Songs getCurrentArtistAlbum() { return queue.get(position); }


    /*/////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
                                   METHODS FOR QUEUE CONTROL
    ///////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////*/

    private void setQueueDisplay()
    {
        queueDisplay.clear();
        if(queue.size() - position > 20)
            queueDisplay.addAll(queue.subList(position + 1, position + 21));
        else if(queue.size() - position < 20 && queue.size() != 1)
            queueDisplay.addAll(queue.subList(position + 1, queue.size()));
        queueAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateQueueOrder(int from, int to)
    {
        int fromPosition = queue.indexOf(queueDisplay.get(from));
        if (from < to)
            for (int i = from; i < to; i++)
                Collections.swap(queueDisplay, i, i + 1);
        else
            for (int i = from; i > to; i--)
                Collections.swap(queueDisplay, i, i - 1);
        queueAdapter.notifyItemMoved(from, to);
        Collections.swap(queue, fromPosition, fromPosition + (to - from));
        saveQueue();
    }

    @Override
    public void updateQueueDismiss(int index)
    {
        queue.remove(queueDisplay.get(index));
        saveQueue();
        queueDisplay.remove(index);
        queueAdapter.notifyItemRemoved(index);
    }

    private void updateQueueDisplay(int action)
    {
        switch (action)
        {
            case SKIP_NEXT:
                if (queue.size() - position > 0)
                {
                    queueDisplay.remove(0); queueAdapter.notifyItemRemoved(0);
                    if(queue.size() - position > 20)
                    {
                        queueDisplay.add(queue.get(position + 20));
                        queueAdapter.notifyItemInserted(queueDisplay.size() - 1);
                    }
                }
                break;

            case SKIP_PREVIOUS:
                if(queue.size() - position > 20)
                {
                    queueDisplay.remove(queueDisplay.size() - 1);
                    queueAdapter.notifyItemRemoved(queueDisplay.size() - 1);
                }
                queueDisplay.add(0, queue.get(position));
                queueAdapter.notifyItemInserted(0);
                break;
        }
    }

    private void queueClick(int index) { position += index + 1; }

    private void setPosition(int position){ this.position = position; }

    private void addSongToQueue(Songs song, int add)
    {
        switch (add)
        {
            case QUEUE_NEXT: queue.add(position + 1, song);
                queueDisplay.remove(queueDisplay.size() - 1);
                queueDisplay.add(0, queue.get(position + 1));
                queueAdapter.notifyItemRemoved(queueDisplay.size() - 1);
                queueAdapter.notifyItemInserted(0);
                break;

            case QUEUE_END: queue.add(song);
                break;
        }
        saveQueue();
    }

    private void addAlbumOrArtistToQueue(int add)
    {
        switch (add)
        {
            case QUEUE_NEXT: queue.addAll(position + 1, addToQueue);
                queueDisplay.addAll(0, addToQueue);
                if(queueDisplay.size() > 20)
                {
                    queueDisplay.subList(20, queueDisplay.size()).clear();
                }
                queueAdapter.notifyDataSetChanged();
                break;

            case QUEUE_END: queue.addAll(addToQueue); break;
        }
        saveQueue();
    }

    /*/////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////*/

    /*/////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
                                   METHODS FOR MEDIA PLAYER
    ///////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////*/

    private void playSong(String mediaId)
    {
        saveQueue();
        Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Long.parseLong(mediaId));
        try
        {
            mediaPlayer.reset();
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build());
            mediaPlayer.setDataSource(this, uri);
            mediaPlayer.prepareAsync();

            stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, mediaPlayer.getCurrentPosition(), 1);
            stateBuilder.setActions(PlaybackStateCompat.ACTION_SEEK_TO | PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);
            mediaSession.setPlaybackState(stateBuilder.build());
            handler.post(updateQueueState);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void setSong(String mediaId)
    {
        saveQueue();
        Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Long.parseLong(mediaId));
        try
        {
            mediaPlayer.reset();
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build());
            mediaPlayer.setDataSource(this, uri);
            mediaPlayer.prepareAsync();

            stateBuilder.setState(PlaybackStateCompat.STATE_PAUSED, mediaPlayer.getCurrentPosition(), 0);
            stateBuilder.setActions(PlaybackStateCompat.ACTION_SEEK_TO | PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);
            mediaSession.setPlaybackState(stateBuilder.build());
        } catch (IOException e) { e.printStackTrace(); }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer)
    {
        if(currentState != PlaybackStateCompat.STATE_NONE)
        {
            if(!repeatOne)
            {
                if(repeatAll && position == queue.size() - 1)
                    position = 0;
                else
                    position++;
            }

            String mediaId = queue.get(position).getId();
            playSong(mediaId);
            buildMetadata(queue.get(position));
            updateQueueDisplay(SKIP_NEXT);
            buildNotification(MediaPlaybackService.this, queue.get(position));
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer)
    {
        if(currentState == PlaybackStateCompat.STATE_PLAYING)
            mediaPlayer.start();
        else
            mediaPlayer.seekTo(elapsed);
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1)
    {
        mediaPlayer.reset();
        return false;
    }

    /*/////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////*/

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints)
    {
        return new BrowserRoot(MY_MEDIA_ROOT_ID, null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result)
    {
        ArrayList<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();
        MediaBrowserCompat.MediaItem mediaItem;
        result.sendResult(null);
    }
}

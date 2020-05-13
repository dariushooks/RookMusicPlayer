package com.example.android.rookmusicplayer.adapters;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android.rookmusicplayer.R;
import com.example.android.rookmusicplayer.helpers.SectionIndexFixer;
import com.example.android.rookmusicplayer.Songs;

import java.util.ArrayList;
import java.util.Locale;

import static com.example.android.rookmusicplayer.App.currentState;
import static com.example.android.rookmusicplayer.App.lettersSongs;
import static com.example.android.rookmusicplayer.App.sectionsSongs;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongViewHolder>
{
    private final String TAG = SongsAdapter.class.getSimpleName();
    private ArrayList<Songs> songs;
    private ListItemClickListener listener;
    private Context context;
    private int previousPosition;
    private int currentlyPlaying = 1;
    private ImageView currentSong;
    private ImageView previousSong;
    private int position;
    private Handler playingHandler = new Handler();

    public interface ListItemClickListener
    {
        void onListItemClick(int position);
        void onLongListItemClick(int position);
    }

    public SongsAdapter(ArrayList<Songs> songs, ListItemClickListener listener)
    {
        this.songs = songs;
        this.listener = listener;
        setHasStableIds(true);
        lettersSongs.clear(); sectionsSongs.clear();
        for (int i = 0; i < this.songs.size(); i++)
        {
            String song = this.songs.get(i).getTitle();
            if(Character.isLetter(song.charAt(0)))
            {
                String letter = song.charAt(0) + "";
                if(lettersSongs.isEmpty())
                {
                    lettersSongs.add(letter.toUpperCase());
                    sectionsSongs.add(i);
                }
                else if(!lettersSongs.contains(letter) && lettersSongs.size() < 26)
                {
                    lettersSongs.add(letter.toUpperCase());
                    sectionsSongs.add(i);
                }
            }

            else
            {
                if(!lettersSongs.contains("#") && lettersSongs.size() < 26)
                {
                    lettersSongs.add("#");
                    sectionsSongs.add(i);
                }
            }
        }

        new SectionIndexFixer().fixIndex(lettersSongs, sectionsSongs);
        /*for(int i = 0; i < 27; i++)
        {
            //Log.i(TAG, "Letter " + lettersSongs.get(i) + "\tSection " + sectionsSongs.get(i));
        }*/
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.songs_list, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position)
    {
        holder.bind(position);
    }

    @Override
    public long getItemId(int position)
    {
        return Integer.parseInt(songs.get(position).getId());
    }

    @Override
    public int getItemViewType(int position) { return Integer.parseInt(songs.get(position).getId()); }

    @Override
    public int getItemCount() { return songs.size(); }

    private Runnable updateCurrentlyPlaying = new Runnable()
    {
        @Override
        public void run()
        {
            if(currentState == PlaybackStateCompat.STATE_PLAYING)
            {
                switch(currentlyPlaying)
                {
                    case 1:
                        currentSong.setBackgroundColor(context.getColor(R.color.nowPlaying));
                        currentSong.setImageResource(R.drawable.ic_currentlyplaying2);
                        currentlyPlaying = 2;
                        break;

                    case 2:
                        currentSong.setBackgroundColor(context.getColor(R.color.nowPlaying));
                        currentSong.setImageResource(R.drawable.ic_currentlyplaying3);
                        currentlyPlaying = 3;
                        break;

                    case 3:
                        currentSong.setBackgroundColor(context.getColor(R.color.nowPlaying));
                        currentSong.setImageResource(R.drawable.ic_currentlyplaying4);
                        currentlyPlaying = 4;
                        break;

                    case 4:
                        currentSong.setBackgroundColor(context.getColor(R.color.nowPlaying));
                        currentSong.setImageResource(R.drawable.ic_currentlyplaying1);
                        currentlyPlaying = 1;
                        break;
                }
                playingHandler.postDelayed(this, 300);
            }

            else
            {
                currentSong.setBackgroundColor(context.getColor(R.color.nowPlaying));
                currentSong.setImageResource(R.drawable.ic_currentlyplaying1);
                currentlyPlaying = 1;
                playingHandler.post(this);
            }
        }
    };

    class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener
    {

        private ImageView albumArt;
        private ImageView songPlaying;
        private TextView songName;
        private TextView songArtist;
        private TextView songDuration;

        public SongViewHolder(@NonNull View itemView)
        {
            super(itemView);
            albumArt = itemView.findViewById(R.id.songArt);
            songPlaying = itemView.findViewById(R.id.songPlaying);
            songName = itemView.findViewById(R.id.songName);
            songArtist = itemView.findViewById(R.id.songArtist);
            songDuration = itemView.findViewById(R.id.songDuration);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }


        public void bind(int position)
        {
            Uri artUri = Uri.parse(songs.get(position).getArt());
            Glide.with(context).load(artUri).placeholder(R.drawable.noalbumart).fallback(R.drawable.noalbumart).error(R.drawable.noalbumart).into(albumArt);

            songName.setText(songs.get(position).getTitle());
            songArtist.setText(songs.get(position).getArtist());

            String duration;
            long minutes = (songs.get(position).getDuration() / 1000) / 60;
            long seconds = (songs.get(position).getDuration() / 1000) % 60;
            if(seconds > 9)
                duration = String.format(Locale.US, "%d:%d", minutes, seconds);
            else
                duration = String.format(Locale.US, "%d:%d%d", minutes, 0, seconds);
            songDuration.setText(duration);
        }


        @Override
        public void onClick(View view)
        {
            /*if(previousSong == null)
            {
                previousSong = songPlaying;
                previousPosition = getAdapterPosition();
            }

            else
            {
                playingHandler.removeCallbacks(updateCurrentlyPlaying);
                previousSong.setBackgroundColor(context.getColor(R.color.transparent));
                previousSong.setImageResource(0);
                previousSong = songPlaying;
                previousPosition = getAdapterPosition();
            }*/

            position = getAdapterPosition();
            listener.onListItemClick(position);
            /*currentSong = songPlaying;
            currentSong.setBackgroundColor(context.getColor(R.color.nowPlaying));
            currentSong.setImageResource(R.drawable.ic_currentlyplaying2);
            currentlyPlaying = 1;
            playingHandler.post(updateCurrentlyPlaying);*/
        }

        @Override
        public boolean onLongClick(View view)
        {
            int clickedPosition = getAdapterPosition();
            listener.onLongListItemClick(clickedPosition);
            return true;
        }
    }
}

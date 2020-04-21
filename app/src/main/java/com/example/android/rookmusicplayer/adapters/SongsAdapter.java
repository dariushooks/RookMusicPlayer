package com.example.android.rookmusicplayer.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android.rookmusicplayer.R;
import com.example.android.rookmusicplayer.helpers.SectionIndexFixer;
import com.example.android.rookmusicplayer.Songs;

import java.util.ArrayList;
import java.util.Locale;

import static com.example.android.rookmusicplayer.App.calculateSampleSize;
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
        for(int i = 0; i < 27; i++)
        {
            //Log.i(TAG, "Letter " + lettersSongs.get(i) + "\tSection " + sectionsSongs.get(i));
        }
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.songs_list, parent, false);
        SongViewHolder viewHolder = new SongViewHolder(view);
        return viewHolder;
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
                //notifyItemChanged(position);
                playingHandler.postDelayed(this, 300);
            }

            else
            {
                //playingHandler.removeCallbacks(this);
                currentSong.setBackgroundColor(context.getColor(R.color.nowPlaying));
                currentSong.setImageResource(R.drawable.ic_currentlyplaying1);
                currentlyPlaying = 1;
                playingHandler.post(this);
            }
        }
    };

    class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener
    {

        private RelativeLayout container;
        private ImageView albumArt;
        private ImageView songPlaying;
        private TextView songName;
        private TextView songArtist;
        private TextView songDuration;

        public SongViewHolder(@NonNull View itemView)
        {
            super(itemView);
            container = itemView.findViewById(R.id.songContainer);
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
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(songs.get(position).getPath());
            byte[] cover = retriever.getEmbeddedPicture();
            if(cover != null)
            {
                Glide.with(context).load(cover).override(45, 50).into(albumArt);
               /* albumArt.setImageBitmap(BitmapFactory.decodeByteArray(cover, 0, cover.length, options));
                //Log.i(TAG, songs.get(position).getTitle() + " Before: " + "Width: " + options.outWidth + "\tHeight: " + options.outHeight);
                options.inSampleSize = calculateSampleSize(options, 45, 50);
                options.inJustDecodeBounds = false;
                albumArt.setImageBitmap(BitmapFactory.decodeByteArray(cover, 0, cover.length, options));*/
                //Log.i(TAG, songs.get(position).getTitle() + " After: " + "Width: " + options.outWidth + "\tHeight: " + options.outHeight);
            }
            else
                albumArt.setImageDrawable(context.getDrawable(R.drawable.noalbumart));
            retriever.release();

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
            if(previousSong == null)
            {
                previousSong = songPlaying;
                previousPosition = getAdapterPosition();
            }

            else
            {
                playingHandler.removeCallbacks(updateCurrentlyPlaying);
                previousSong.setBackgroundColor(context.getColor(R.color.transparent));
                previousSong.setImageResource(0);
                //notifyItemChanged(previousPosition);
                previousSong = songPlaying;
                previousPosition = getAdapterPosition();
            }

            position = getAdapterPosition();
            listener.onListItemClick(position);
            currentSong = songPlaying;
            currentSong.setBackgroundColor(context.getColor(R.color.nowPlaying));
            currentSong.setImageResource(R.drawable.ic_currentlyplaying2);
            currentlyPlaying = 1;
            playingHandler.post(updateCurrentlyPlaying);
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

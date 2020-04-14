package com.example.android.rookmusicplayer.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.rookmusicplayer.R;
import com.example.android.rookmusicplayer.helpers.SectionIndexFixer;
import com.example.android.rookmusicplayer.Songs;

import java.util.ArrayList;
import java.util.Locale;

import static com.example.android.rookmusicplayer.App.calculateSampleSize;
import static com.example.android.rookmusicplayer.App.lettersSongs;
import static com.example.android.rookmusicplayer.App.sectionsSongs;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongViewHolder>
{
    private final String TAG = SongsAdapter.class.getSimpleName();
    private ArrayList<Songs> songs;
    private ListItemClickListener listener;
    private Context context;

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
    public int getItemViewType(int position)
    {
        return Integer.parseInt(songs.get(position).getId());
    }

    @Override
    public int getItemCount()
    {
        return songs.size();
    }

    class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener
    {

        private RelativeLayout container;
        private ImageView albumArt;
        private TextView songName;
        private TextView songArtist;
        private TextView songDuration;

        public SongViewHolder(@NonNull View itemView)
        {
            super(itemView);
            container = itemView.findViewById(R.id.songContainer);
            albumArt = itemView.findViewById(R.id.songArt);
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
                albumArt.setImageBitmap(BitmapFactory.decodeByteArray(cover, 0, cover.length, options));
                //Log.i(TAG, songs.get(position).getTitle() + " Before: " + "Width: " + options.outWidth + "\tHeight: " + options.outHeight);
                options.inSampleSize = calculateSampleSize(options, 45, 50);
                options.inJustDecodeBounds = false;
                albumArt.setImageBitmap(BitmapFactory.decodeByteArray(cover, 0, cover.length, options));
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
            int clickedPosition = getAdapterPosition();
            listener.onListItemClick(clickedPosition);
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

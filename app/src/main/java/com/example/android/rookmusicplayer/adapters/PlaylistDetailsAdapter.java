package com.example.android.rookmusicplayer.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.rookmusicplayer.R;
import com.example.android.rookmusicplayer.Songs;

import java.util.ArrayList;
import java.util.Locale;

public class PlaylistDetailsAdapter extends RecyclerView.Adapter<PlaylistDetailsAdapter.PlaylistDetailsViewHolder>
{
    private ArrayList<Songs> songs;
    private ListItemClickListener listener;
    private Context context;
    private ImageView albumArt;
    private TextView songName;
    private TextView songArtist;
    private TextView songDuration;

    public interface ListItemClickListener
    {
        void onListItemClick(int position);
        void onLongListItemClick(int position);
    }

    public PlaylistDetailsAdapter(ArrayList<Songs> songs, ListItemClickListener listener)
    {
        this.songs = songs;
        this.listener = listener;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public PlaylistDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.songs_list, parent, false);
        PlaylistDetailsViewHolder viewHolder = new PlaylistDetailsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistDetailsViewHolder holder, int position)
    {
        holder.bind(position);
    }

    @Override
    public int getItemCount() { return songs.size(); }

    @Override
    public int getItemViewType(int position) { return Integer.parseInt(songs.get(position).getId()); }

    @Override
    public long getItemId(int position) { return Integer.parseInt(songs.get(position).getId()); }

    class PlaylistDetailsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener
    {

        public PlaylistDetailsViewHolder(@NonNull View itemView)
        {
            super(itemView);
            albumArt = itemView.findViewById(R.id.songArt);
            songName = itemView.findViewById(R.id.songName);
            songArtist = itemView.findViewById(R.id.songArtist);
            songDuration = itemView.findViewById(R.id.songDuration);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bind(int position)
        {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(songs.get(position).getPath());
            byte[] cover = retriever.getEmbeddedPicture();
            if(cover != null)
                albumArt.setImageBitmap(BitmapFactory.decodeByteArray(cover, 0, cover.length));
            else
                albumArt.setImageDrawable(context.getDrawable(R.drawable.noalbumart));
            retriever.release();
            /////////////////////////////////////////////////////////////////////////
            /////////////////////////////////////////////////////////////////////////
            /////////////////////////////////////////////////////////////////////////

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

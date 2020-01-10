package com.example.android.rookmusicplayer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.rookmusicplayer.helpers.GetMedia;
import com.example.android.rookmusicplayer.Playlists;
import com.example.android.rookmusicplayer.R;

import java.util.ArrayList;

public class PlaylistsAdapter extends RecyclerView.Adapter<PlaylistsAdapter.PlaylistViewHolder>
{
    private final String TAG = PlaylistsAdapter.class.getSimpleName();
    private ArrayList<Playlists> playlists;
    private ListItemClickListener listener;
    private Context context;
    public interface ListItemClickListener
    {
        void onListItemClick(Playlists playlist);
        void onLongListItemClick(Playlists playlist);
    }

    public PlaylistsAdapter(ArrayList<Playlists> playlists, ListItemClickListener listener)
    {
        this.playlists = playlists;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.playlists_grid, parent, false);
        PlaylistViewHolder viewHolder = new PlaylistViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position)
    {
        holder.bind(position);
    }



    @Override
    public int getItemCount() { return playlists.size(); }

    class PlaylistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener
    {
        ImageView playlistArt;
        TextView playlistName;
        TextView playlistCount;

        public PlaylistViewHolder(@NonNull View itemView)
        {
            super(itemView);
            playlistArt = itemView.findViewById(R.id.playlistArt);
            playlistName = itemView.findViewById(R.id.gridPlaylist);
            playlistCount = itemView.findViewById(R.id.gridPlaylistCount);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bind(int position)
        {
            playlistArt.setImageDrawable(context.getDrawable(R.drawable.playlistart));
            playlistName.setText(playlists.get(position).getPlaylist());
            GetMedia getMedia = new GetMedia(context);
            int pCount = getMedia.getPlaylistSongs(playlists.get(position)).size();
            String count;

            if(pCount == 1)
            {
                count = pCount + " song";
                playlistCount.setText(count);
            }

            else
            {
                count = pCount + " songs";
                playlistCount.setText(count);
            }
        }

        @Override
        public void onClick(View view)
        {
            int clickedPosition = getAdapterPosition();
            listener.onListItemClick(playlists.get(clickedPosition));
        }

        @Override
        public boolean onLongClick(View view)
        {
            int clickedPosition = getAdapterPosition();
            listener.onLongListItemClick(playlists.get(clickedPosition));
            return true;
        }
    }
}

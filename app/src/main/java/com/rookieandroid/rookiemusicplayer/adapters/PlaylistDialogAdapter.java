package com.rookieandroid.rookiemusicplayer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rookieandroid.rookiemusicplayer.Playlists;
import com.rookieandroid.rookiemusicplayer.R;

import java.util.ArrayList;

public class PlaylistDialogAdapter extends RecyclerView.Adapter<PlaylistDialogAdapter.PlaylistDialogViewHolder>
{
    private ArrayList<Playlists> playlists;
    private ListItemClickListener listener;
    private Context context;
    public interface ListItemClickListener
    {
        void onListItemClick(Playlists playlist);
    }

    public PlaylistDialogAdapter(ArrayList<Playlists> playlists, ListItemClickListener listener)
    {
        this.playlists = playlists;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlaylistDialogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.playlists_list, parent, false);
        PlaylistDialogViewHolder viewHolder = new PlaylistDialogViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistDialogViewHolder holder, int position)
    {
        holder.bind(position);
    }



    @Override
    public int getItemCount() { return playlists.size(); }

    class PlaylistDialogViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView playlistName;

        public PlaylistDialogViewHolder(@NonNull View itemView)
        {
            super(itemView);
            playlistName = itemView.findViewById(R.id.playlist);
            itemView.setOnClickListener(this);
        }

        public void bind(int position)
        {
            playlistName.setText(playlists.get(position).getPlaylist());
        }

        @Override
        public void onClick(View view)
        {
            int clickedPosition = getAdapterPosition();
            listener.onListItemClick(playlists.get(clickedPosition));
        }

    }
}

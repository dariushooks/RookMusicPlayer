package com.rookieandroid.rookiemusicplayer.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.RecyclerView;

import com.rookieandroid.rookiemusicplayer.MainActivity;
import com.rookieandroid.rookiemusicplayer.helpers.GetMedia;
import com.rookieandroid.rookiemusicplayer.Playlists;
import com.rookieandroid.rookiemusicplayer.R;

import java.util.ArrayList;

import static com.rookieandroid.rookiemusicplayer.App.GET_PLAYLIST_SONGS;
import static com.rookieandroid.rookiemusicplayer.App.PLAYLIST_MEDIA_LOADER;

public class PlaylistsAdapter extends RecyclerView.Adapter<PlaylistsAdapter.PlaylistViewHolder>
{
    private final String TAG = PlaylistsAdapter.class.getSimpleName();
    private Playlists playlist;
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

    class PlaylistViewHolder extends RecyclerView.ViewHolder implements LoaderManager.LoaderCallbacks<ArrayList>, View.OnClickListener, View.OnLongClickListener
    {
        private ImageView playlistArt;
        private TextView playlistName;
        private TextView playlistCount;

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
            playlist = playlists.get(position);
            LoaderManager.getInstance((MainActivity) context).initLoader(PLAYLIST_MEDIA_LOADER, null, this);
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

        @NonNull
        @Override
        public Loader<ArrayList> onCreateLoader(int id, @Nullable Bundle args)
        {
            return new GetMedia(context, GET_PLAYLIST_SONGS, -1, playlist);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<ArrayList> loader, ArrayList data)
        {
            int pCount = data.size();
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
        public void onLoaderReset(@NonNull Loader<ArrayList> loader) {}
    }
}

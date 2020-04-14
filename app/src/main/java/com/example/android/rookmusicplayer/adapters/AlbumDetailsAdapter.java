package com.example.android.rookmusicplayer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.rookmusicplayer.R;
import com.example.android.rookmusicplayer.Songs;

import java.util.ArrayList;
import java.util.Locale;

public class AlbumDetailsAdapter extends RecyclerView.Adapter<AlbumDetailsAdapter.AlbumDetailsViewHolder>
{
    private ArrayList<Songs> songs;
    private Context context;
    private ListItemClickListener listener;

    public interface ListItemClickListener
    {
        void onListItemClick(int position);
        void onLongListItemClick(int position);
    }

    public AlbumDetailsAdapter(ArrayList<Songs> songs, ListItemClickListener listener)
    {
        this.songs = songs;
        this.listener = listener;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public AlbumDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.album_songs_list, parent, false);
        AlbumDetailsViewHolder viewHolder = new AlbumDetailsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumDetailsViewHolder holder, int position)
    {
        holder.bind(position);
    }

    @Override
    public int getItemCount() { return songs.size(); }

    @Override
    public int getItemViewType(int position) { return Integer.parseInt(songs.get(position).getId()); }

    @Override
    public long getItemId(int position) { return Integer.parseInt(songs.get(position).getId()); }

    class AlbumDetailsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener
    {

        private TextView trackName;
        private TextView trackDuration;
        private TextView trackNumber;
        private TextView trackNumberOffset;

        public AlbumDetailsViewHolder(@NonNull View itemView)
        {
            super(itemView);
            trackName = itemView.findViewById(R.id.trackName);
            trackDuration = itemView.findViewById(R.id.trackDuration);
            trackNumber = itemView.findViewById(R.id.trackNumber);
            trackNumberOffset = itemView.findViewById(R.id.trackNumberOffset);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bind(int position)
        {
            trackName.setText(songs.get(position).getTitle());

            String duration;
            long minutes = (songs.get(position).getDuration() / 1000) / 60;
            long seconds = (songs.get(position).getDuration() / 1000) % 60;
            if(seconds > 9)
                duration = String.format(Locale.US, "%d:%d", minutes, seconds);
            else
                duration = String.format(Locale.US, "%d:%d%d", minutes, 0, seconds);
            trackDuration.setText(duration);

            setTrackNumber(songs.get(position).getTrack());
            /*String trackNum = songs.get(position).getTrack();
            String[] track;
            if(trackNum != null)
            {
                if(trackNum.contains("/"))
                {
                    track = trackNum.split("/");
                    setTrackNumber(track[0]);
                }

                else
                    setTrackNumber(trackNum);
            }*/
        }

        private void setTrackNumber(int trackNum)
        {
            if(trackNum == 0 || trackNum == -1)
                trackNumber.setText("");

            else if(trackNum > 1000)
            {
                if((trackNum - 1000) > 9)
                    trackNumberOffset.setVisibility(View.GONE);
                trackNumber.setText(String.valueOf(trackNum - 1000));
            }

            else
            {
                if(trackNum > 9)
                    trackNumberOffset.setVisibility(View.GONE);
                trackNumber.setText(trackNum + "");
            }
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

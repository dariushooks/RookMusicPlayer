package com.rookieandroid.rookiemusicplayer.adapters;

import android.content.Context;
import android.os.Handler;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rookieandroid.rookiemusicplayer.R;
import com.rookieandroid.rookiemusicplayer.Songs;

import java.util.ArrayList;
import java.util.Locale;

import static com.rookieandroid.rookiemusicplayer.App.currentState;

public class AlbumDetailsAdapter extends RecyclerView.Adapter<AlbumDetailsAdapter.AlbumDetailsViewHolder>
{
    private ArrayList<Songs> songs;
    private Context context;
    private ListItemClickListener listener;
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
        return new AlbumDetailsViewHolder(view);
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

    private Runnable updateTrackPlaying = new Runnable()
    {
        @Override
        public void run()
        {
            if(currentState == PlaybackStateCompat.STATE_PLAYING)
            {
                switch(currentlyPlaying)
                {
                    case 1:
                        currentSong.setBackgroundColor(context.getColor(R.color.black));
                        currentSong.setImageResource(R.drawable.ic_currentlyplaying2);
                        currentlyPlaying = 2;
                        break;

                    case 2:
                        currentSong.setBackgroundColor(context.getColor(R.color.black));
                        currentSong.setImageResource(R.drawable.ic_currentlyplaying3);
                        currentlyPlaying = 3;
                        break;

                    case 3:
                        currentSong.setBackgroundColor(context.getColor(R.color.black));
                        currentSong.setImageResource(R.drawable.ic_currentlyplaying4);
                        currentlyPlaying = 4;
                        break;

                    case 4:
                        currentSong.setBackgroundColor(context.getColor(R.color.black));
                        currentSong.setImageResource(R.drawable.ic_currentlyplaying1);
                        currentlyPlaying = 1;
                        break;
                }
                playingHandler.postDelayed(this, 300);
            }

            else
            {
                currentSong.setBackgroundColor(context.getColor(R.color.black));
                currentSong.setImageResource(R.drawable.ic_currentlyplaying1);
                currentlyPlaying = 1;
                playingHandler.post(this);
            }
        }
    };

    class AlbumDetailsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener
    {

        private TextView trackName;
        private TextView trackDuration;
        private TextView trackNumber;
        private TextView trackNumberOffset;
        private ImageView trackPlaying;

        public AlbumDetailsViewHolder(@NonNull View itemView)
        {
            super(itemView);
            trackName = itemView.findViewById(R.id.trackName);
            trackDuration = itemView.findViewById(R.id.trackDuration);
            trackNumber = itemView.findViewById(R.id.trackNumber);
            trackNumberOffset = itemView.findViewById(R.id.trackNumberOffset);
            trackPlaying = itemView.findViewById(R.id.trackPlaying);
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
            /*if(previousSong == null)
            {
                previousSong = trackPlaying;
                previousPosition = getAdapterPosition();
            }

            else
            {
                playingHandler.removeCallbacks(updateTrackPlaying);
                previousSong.setBackgroundColor(context.getColor(R.color.transparent));
                previousSong.setImageResource(0);
                previousSong = trackPlaying;
                previousPosition = getAdapterPosition();
            }*/

            position = getAdapterPosition();
            listener.onListItemClick(position);
            /*currentSong = trackPlaying;
            currentSong.setBackgroundColor(context.getColor(R.color.black));
            currentSong.setImageResource(R.drawable.ic_currentlyplaying2);
            currentlyPlaying = 1;
            playingHandler.post(updateTrackPlaying);*/
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

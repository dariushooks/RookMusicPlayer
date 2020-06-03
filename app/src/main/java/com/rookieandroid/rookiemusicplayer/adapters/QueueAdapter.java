package com.rookieandroid.rookiemusicplayer.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rookieandroid.rookiemusicplayer.R;

import static com.rookieandroid.rookiemusicplayer.App.queueDisplay;

public class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.QueueViewHolder>
{
    private static String TAG = QueueAdapter.class.getSimpleName();
    private QueueAdapter.ListItemClickListener listener;
    private QueueChange queueChange;
    private Context context;

    public interface ListItemClickListener
    {
        void QueueListItemClick(int position);
    }

    //Keeps the queue updated on drags and swipes
    public interface QueueChange
    {
        void updateQueueOrder(int from, int to);
        void updateQueueDismiss(int index);
    }

    public QueueAdapter(QueueAdapter.ListItemClickListener listener)
    {
        this.listener = listener;
        setHasStableIds(true);
    }

    public void initializeQueueChange(QueueChange queueChange) { this.queueChange = queueChange; }

    @NonNull
    @Override
    public QueueAdapter.QueueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.queue_list, parent, false);
        QueueAdapter.QueueViewHolder viewHolder = new QueueAdapter.QueueViewHolder(view);
        return viewHolder;
    }

    @Override
    public long getItemId(int position) { return Integer.parseInt(queueDisplay.get(position).getId()); }

    @Override
    public int getItemViewType(int position) { return Integer.parseInt(queueDisplay.get(position).getId()); }

    @Override
    public void onBindViewHolder(@NonNull QueueAdapter.QueueViewHolder holder, int position) { holder.bind(position); }

    @Override
    public int getItemCount() { return queueDisplay.size(); }

    public boolean onItemMove(int fromPosition, int toPosition)
    {
        queueChange.updateQueueOrder(fromPosition, toPosition);
        return true;
    }

    public void onItemDismiss(int position) { queueChange.updateQueueDismiss(position); }

    class QueueViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        private ImageView albumArt;
        private TextView songName;
        private TextView songArtist;
        private ImageView order;

        public QueueViewHolder(@NonNull View itemView)
        {
            super(itemView);
            albumArt = itemView.findViewById(R.id.songArt);
            songName = itemView.findViewById(R.id.songName);
            songArtist = itemView.findViewById(R.id.songArtist);
            order = itemView.findViewById(R.id.order);
            itemView.setOnClickListener(this);
        }

        public void bind(int position)
        {
            Uri uri = Uri.parse(queueDisplay.get(position).getArt());
            Glide.with(context).load(uri).placeholder(R.drawable.noalbumart).fallback(R.drawable.noalbumart).error(R.drawable.noalbumart).into(albumArt);

            songName.setText(queueDisplay.get(position).getTitle());
            songArtist.setText(queueDisplay.get(position).getArtist());
        }

        @Override
        public void onClick(View view)
        {
            int clickedPosition = getAdapterPosition();
            listener.QueueListItemClick(clickedPosition);
        }

    }
}

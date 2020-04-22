package com.example.android.rookmusicplayer.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android.rookmusicplayer.R;

import java.io.IOException;

import static com.example.android.rookmusicplayer.App.calculateSampleSize;
import static com.example.android.rookmusicplayer.App.queueDisplay;

public class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.QueueViewHolder>
{
    private static String TAG = QueueAdapter.class.getSimpleName();
    private QueueAdapter.ListItemClickListener listener;
    private QueueChange queueChange;
    private Context context;

    public interface ListItemClickListener
    {
        void QueueListItemClick(int position);
        void OrderLongClick(int position);
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

            order.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view)
                {
                    int clickedPostion = getAdapterPosition();
                    listener.OrderLongClick(clickedPostion);
                    return true;
                }
            });
        }

        public void bind(int position)
        {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            try
            {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(queueDisplay.get(position).getArt()));
                if(bitmap != null)
                {
                    Glide.with(context).load(bitmap).into(albumArt);
               /* albumArt.setImageBitmap(BitmapFactory.decodeByteArray(cover, 0, cover.length, options));
                //Log.i(TAG, songs.get(position).getTitle() + " Before: " + "Width: " + options.outWidth + "\tHeight: " + options.outHeight);
                options.inSampleSize = calculateSampleSize(options, 45, 50);
                options.inJustDecodeBounds = false;
                albumArt.setImageBitmap(BitmapFactory.decodeByteArray(cover, 0, cover.length, options));*/
                    //Log.i(TAG, songs.get(position).getTitle() + " After: " + "Width: " + options.outWidth + "\tHeight: " + options.outHeight);
                }
                else
                    albumArt.setImageDrawable(context.getDrawable(R.drawable.noalbumart));
            } catch (IOException e) { e.printStackTrace(); }

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

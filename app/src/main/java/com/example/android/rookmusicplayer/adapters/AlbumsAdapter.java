package com.example.android.rookmusicplayer.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.rookmusicplayer.Albums;
import com.example.android.rookmusicplayer.R;

import java.io.IOException;
import java.util.ArrayList;


public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.AlbumViewHolder>
{
    private String TAG = AlbumsAdapter.class.getSimpleName();
    private ArrayList<Albums> albums;
    private ListItemClickListener listener;
    private Context context;
    public interface ListItemClickListener
    {
        void onListItemClick(Albums album, ImageView art);
        void onLongItemClick(Albums album);
    }

    public AlbumsAdapter(ArrayList<Albums> albums, ListItemClickListener listener)
    {
        this.albums = albums;
        this.listener = listener;
        //setHasStableIds(true);
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.albums_grid, parent, false);
        AlbumViewHolder viewHolder = new AlbumViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position)
    {
        holder.bind(position);
        ViewCompat.setTransitionName(holder.albumArt, albums.get(position).getAlbum());
    }



    @Override
    public int getItemCount() { return albums.size(); }

    class AlbumViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener
    {
        private ImageView albumArt;
        private TextView albumName;
        private TextView albumArtist;

        public AlbumViewHolder(@NonNull View itemView)
        {
            super(itemView);
            albumArt = itemView.findViewById(R.id.gridArt);
            albumName = itemView.findViewById(R.id.gridAlbum);
            albumArtist = itemView.findViewById(R.id.gridArtist);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bind(int position)
        {

            try
            {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(albums.get(position).getArt()));
                //Log.i(TAG, albums.get(position).getAlbum() + " Before: " + "Width: " + bitmap.getWidth() + "\tHeight: " + bitmap.getHeight());
                //Bitmap art = calculateBitmapSize(bitmap, 400, 175);
                //albumArt.setImageBitmap(art);
                //Log.i(TAG, albums.get(position).getAlbum() + " After: " + "Width: " + art.getWidth() + "\tHeight: " + art.getHeight());
                if(bitmap != null)
                   albumArt.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 450, 200, true));
                else
                    albumArt.setImageDrawable(context.getDrawable(R.drawable.noalbumart));
            } catch (IOException e) { e.printStackTrace(); }
            albumName.setText(albums.get(position).getAlbum());
            albumArtist.setText(albums.get(position).getArtist());

        }

        @Override
        public void onClick(View view)
        {
            int clickedPosition = getAdapterPosition();
            listener.onListItemClick(albums.get(clickedPosition), albumArt);
        }

        @Override
        public boolean onLongClick(View view)
        {
            int clickedPosition = getAdapterPosition();
            listener.onLongItemClick(albums.get(clickedPosition));
            return true;
        }
    }
}

package com.example.android.rookmusicplayer.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
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

import com.bumptech.glide.Glide;
import com.example.android.rookmusicplayer.Albums;
import com.example.android.rookmusicplayer.R;
import com.example.android.rookmusicplayer.helpers.SectionIndexFixer;

import java.io.IOException;
import java.util.ArrayList;

import static com.example.android.rookmusicplayer.App.lettersAlbums;
import static com.example.android.rookmusicplayer.App.sectionsAlbums;


public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.AlbumViewHolder>
{
    private final int ALBUM_TYPE = 1;
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
        setHasStableIds(true);
        lettersAlbums.clear(); sectionsAlbums.clear();
        for (int i = 0; i < this.albums.size(); i++)
        {
            String album = this.albums.get(i).getAlbum();
            if(Character.isLetter(album.charAt(0)))
            {
                String letter = album.charAt(0) + "";
                if(lettersAlbums.isEmpty())
                {
                    lettersAlbums.add(letter.toUpperCase());
                    sectionsAlbums.add(i);
                }

                else if(!lettersAlbums.contains(letter) && lettersAlbums.size() < 26)
                {
                    lettersAlbums.add(letter.toUpperCase());
                    sectionsAlbums.add(i);
                }
            }

            else
            {
                if(!lettersAlbums.contains("#") && lettersAlbums.size() < 26)
                {
                    lettersAlbums.add("#");
                    sectionsAlbums.add(i);
                }
            }
        }

        new SectionIndexFixer().fixIndex(lettersAlbums, sectionsAlbums);
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.albums_grid, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position)
    {
        holder.bind(position);
        ViewCompat.setTransitionName(holder.albumArt, albums.get(position).getAlbum());
    }

    @Override
    public long getItemId(int position) { return Long.parseLong(albums.get(position).getId()); }

    @Override
    public int getItemViewType(int position) { return ALBUM_TYPE; }

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
            Uri albumArtUri = Uri.parse(albums.get(position).getArt());
            Glide.with(context).load(albumArtUri).placeholder(R.drawable.noalbumart).fallback(R.drawable.noalbumart).error(R.drawable.noalbumart).into(albumArt);
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

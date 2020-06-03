package com.rookieandroid.rookiemusicplayer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rookieandroid.rookiemusicplayer.Albums;
import com.rookieandroid.rookiemusicplayer.AlbumsSections;
import com.rookieandroid.rookiemusicplayer.R;

import java.util.ArrayList;

import static com.rookieandroid.rookiemusicplayer.App.lettersAlbums;
import static com.rookieandroid.rookiemusicplayer.App.sectionsAlbums;

public class AlbumsSectionsAdapter extends RecyclerView.Adapter<AlbumsSectionsAdapter.AlbumsSectionsViewHolder>
{
    private final String TAG = AlbumsSectionsAdapter.class.getSimpleName();
    private ArrayList<AlbumsSections> albumsSections;
    private Context context;
    private AlbumsAdapter.ListItemClickListener listener;
    private RecyclerView.RecycledViewPool viewPool;

    public AlbumsSectionsAdapter(ArrayList<AlbumsSections> albumsSections, AlbumsAdapter.ListItemClickListener listener)
    {
        this.albumsSections = albumsSections;
        this.listener = listener;
        viewPool = new RecyclerView.RecycledViewPool();
        int letter = 65;
        lettersAlbums.clear(); sectionsAlbums.clear();
        for(int i = 0; i < albumsSections.size(); i++)
        {
            if(i > 0)
            {
                if (!albumsSections.get(i).getSectionedAlbums().isEmpty())
                {
                    lettersAlbums.add((char)(letter) + "");
                    sectionsAlbums.add(i);
                }

                else
                {
                    lettersAlbums.add("!");
                    sectionsAlbums.add(-1);
                }

                letter++;
            }

            else if(i == 0)
            {
                if(!albumsSections.get(i).getSectionedAlbums().isEmpty())
                {
                    lettersAlbums.add("#");
                    sectionsAlbums.add(i);
                }

                else
                {
                    lettersAlbums.add("!");
                    sectionsAlbums.add(-1);
                }
            }

            //Log.i(TAG, "Letter: " + lettersAlbums.get(i) + "\tPosition: " + sectionsAlbums.get(i));
        }
    }

    @NonNull
    @Override
    public AlbumsSectionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.albums_sections, parent, false);
        return new AlbumsSectionsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumsSectionsViewHolder holder, int position)
    {
        holder.bind(position);
    }

    @Override
    public int getItemCount() { return albumsSections.size(); }

    class AlbumsSectionsViewHolder extends RecyclerView.ViewHolder
    {
        private TextView header;
        private RecyclerView recyclerView;

        public AlbumsSectionsViewHolder(@NonNull View itemView)
        {
            super(itemView);
            header = itemView.findViewById(R.id.sectionIndex);
            recyclerView = itemView.findViewById(R.id.sectionsList);
        }

        public void bind(int position)
        {
            String section;
            int letter = 65;

            if(position < 1)
                header.setText("#");

            else if(position == 1)
            {
                section = (char) letter + "";
                header.setText(section);
            }

            else
            {
                letter++;
                section = (char) letter + "";
                header.setText(section);
            }

            ArrayList<Albums> albums = albumsSections.get(position).getSectionedAlbums();
            GridLayoutManager layoutManager = new GridLayoutManager(context, 2);
            //LinearLayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
            layoutManager.setInitialPrefetchItemCount(albums.size() + 1);
            AlbumsAdapter albumsAdapter = new AlbumsAdapter(albums, listener, null);
            albumsSections.get(position).setAlbumsAdapter(albumsAdapter);
            recyclerView.setRecycledViewPool(viewPool);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(albumsAdapter);
        }
    }
}

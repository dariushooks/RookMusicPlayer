package com.rookieandroid.rookiemusicplayer.adapters;

import android.content.Context;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rookieandroid.rookiemusicplayer.Artists;
import com.rookieandroid.rookiemusicplayer.R;
import com.rookieandroid.rookiemusicplayer.fragments.ArtistsFragment;
import com.rookieandroid.rookiemusicplayer.helpers.SectionIndexFixer;

import java.util.ArrayList;

import static com.rookieandroid.rookiemusicplayer.App.lettersArtists;
import static com.rookieandroid.rookiemusicplayer.App.sectionsArtists;

public class ArtistsAdapter extends RecyclerView.Adapter<ArtistsAdapter.ArtistViewHolder>
{
    private String TAG = ArtistsAdapter.class.getSimpleName();
    private ArrayList<Artists> artists;
    private ArtistsAdapter.ListItemClickListener listener;
    private ArtistsFragment fragment;
    private Context context;
    public interface ListItemClickListener
    {
        void onListItemClick(int position, TextView artist);
        void onLongListItemClick(int position);
    }

    public ArtistsAdapter(ArrayList<Artists> artists, ArtistsAdapter.ListItemClickListener listener, ArtistsFragment fragment)
    {
        this.artists = artists;
        this.listener = listener;
        this.fragment = fragment;
        //setHasStableIds(true);
        lettersArtists.clear(); sectionsArtists.clear();
        for (int i = 0; i < this.artists.size(); i++)
        {
            String artist = this.artists.get(i).getArtist();
            if(Character.isLetter(artist.charAt(0)))
            {
                String letter = artist.charAt(0) + "";
                if(lettersArtists.isEmpty())
                {
                    lettersArtists.add(letter.toUpperCase());
                    sectionsArtists.add(i);
                }

                else if(!lettersArtists.contains(letter) && lettersArtists.size() < 26)
                {
                    lettersArtists.add(letter.toUpperCase());
                    sectionsArtists.add(i);
                }
            }

            else
            {
                if(!lettersArtists.contains("#") && lettersArtists.size() < 26)
                {
                    lettersArtists.add("#");
                    sectionsArtists.add(i);
                }
            }
        }

        new SectionIndexFixer().fixIndex(lettersArtists, sectionsArtists);
        /*for(int i = 0; i < 27; i++)
        {
            Log.i(TAG, "Letter " + lettersArtists.get(i) + "\tSection " + sectionsArtists.get(i));
        }*/
    }

    ArtistsAdapter(ArrayList<Artists> artists, ArtistsAdapter.ListItemClickListener listener, String query)
    {
        this.artists = artists;
        this.listener = listener;
    }



    @NonNull
    @Override
    public ArtistsAdapter.ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.artists_list, parent, false);
        ArtistsAdapter.ArtistViewHolder viewHolder = new ArtistsAdapter.ArtistViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistsAdapter.ArtistViewHolder holder, int position)
    {
        holder.bind(position);
        ViewCompat.setTransitionName(holder.artistName, artists.get(position).getArtist());
    }


    @Override
    public int getItemCount()
    {
        return artists.size();
    }

    class ArtistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener
    {
        private ImageView artistArt;
        private TextView artistName;

        public ArtistViewHolder(@NonNull View itemView)
        {
            super(itemView);
            artistArt = itemView.findViewById(R.id.artistArt);
            artistName = itemView.findViewById(R.id.artistName);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bind(int position)
        {
            Glide.with(fragment).load(R.drawable.ic_noartistart).into(artistArt);
            artistName.setText(artists.get(position).getArtist());
        }

        @Override
        public void onClick(View view)
        {
            ((Fade) fragment.getExitTransition()).excludeTarget(view, true);
            int clickedPosition = getAdapterPosition();
            listener.onListItemClick(clickedPosition, artistName);
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

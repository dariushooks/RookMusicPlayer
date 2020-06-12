package com.rookieandroid.rookiemusicplayer.helpers;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.rookieandroid.rookiemusicplayer.R;
import com.rookieandroid.rookiemusicplayer.Songs;

import static com.rookieandroid.rookiemusicplayer.App.TO_ALBUM;
import static com.rookieandroid.rookiemusicplayer.App.TO_ARTIST;

public class GoToDialog extends AlertDialog
{
    private Context context;
    private Songs artist_album;
    private TextView artistName;
    private TextView albumName;
    private RelativeLayout goArtist;
    private RelativeLayout goAlbum;
    private GoTo goTo;

    public interface GoTo
    {
        void goTo(Songs artist_album, int to);
    }

    public GoToDialog(Context context, Songs artist_album)
    {
        super(context);
        this.context = context;
        this.artist_album = artist_album;
        goTo = (GoTo) context;
    }

    public void OpenDialog()
    {
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        final View alertLayout = getLayoutInflater().inflate(R.layout.dialog_goto,null);

        goArtist = alertLayout.findViewById(R.id.goToArtist);
        artistName = alertLayout.findViewById(R.id.artist);
        artistName.setText(artist_album.getArtist());

        goAlbum = alertLayout.findViewById(R.id.goToAlbum);
        albumName = alertLayout.findViewById(R.id.album);
        albumName.setText(artist_album.getAlbum());

        setClickListeners(alertDialog);

        alertDialog.setView(alertLayout);
        new Dialog(context);
        alertDialog.show();
        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 20);
        alertDialog.getWindow().setBackgroundDrawable(inset);
        alertDialog.getWindow().setElevation(20.0f);
    }

    private void setClickListeners(AlertDialog alertDialog)
    {
        goArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                goTo.goTo(artist_album, TO_ARTIST);
                alertDialog.dismiss();
            }
        });

        goAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                goTo.goTo(artist_album, TO_ALBUM);
                alertDialog.dismiss();
            }
        });
    }

}

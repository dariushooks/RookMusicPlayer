package com.rookieandroid.rookiemusicplayer.architecture;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rookieandroid.rookiemusicplayer.Albums;
import com.rookieandroid.rookiemusicplayer.Artists;
import com.rookieandroid.rookiemusicplayer.Playlists;
import com.rookieandroid.rookiemusicplayer.Songs;

import java.util.List;

public class LibraryViewModel extends ViewModel
{
    private MutableLiveData<List<Songs>> songs = new MutableLiveData<>();
    private MutableLiveData<List<Artists>> artists = new MutableLiveData<>();
    private MutableLiveData<List<Albums>> albums = new MutableLiveData<>();
    private MutableLiveData<List<Playlists>> playlists = new MutableLiveData<>();

    public void setSongs(List<Songs> input) { songs.postValue(input);}
    public void setArtists(List<Artists> input) { artists.postValue(input);}
    public void setAlbums(List<Albums> input) { albums.postValue(input);}
    public void setPlaylists(List<Playlists> input) { playlists.postValue(input); }

    public LiveData<List<Songs>> getSongs() { return songs;}
    public LiveData<List<Artists>> getArtists() { return artists; }
    public LiveData<List<Albums>> getAlbums() { return albums; }
    public LiveData<List<Playlists>> getPlaylists() { return playlists; }
}

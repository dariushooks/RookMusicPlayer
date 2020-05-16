package com.example.android.rookmusicplayer.architecture;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.android.rookmusicplayer.Songs;

import java.util.List;

public class LibraryViewModel extends ViewModel
{
    private MutableLiveData<List<Songs>> songs = new MutableLiveData<>();

    public void setSongs(List<Songs> input) { songs.setValue(input); }

    public LiveData<List<Songs>> getSongs() { return songs; }
}

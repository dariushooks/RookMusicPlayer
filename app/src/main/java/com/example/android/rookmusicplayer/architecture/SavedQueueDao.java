package com.example.android.rookmusicplayer.architecture;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.android.rookmusicplayer.Songs;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface SavedQueueDao
{
    @Insert
    void insertAll(ArrayList<Songs> savedQueue);

    @Query("DELETE FROM saved_queue")
    void deleteSavedQueue();

    @Query("SELECT * FROM saved_queue")
    LiveData<List<Songs>> getSavedQueue();
}

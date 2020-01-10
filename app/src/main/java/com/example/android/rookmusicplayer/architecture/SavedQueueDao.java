package com.example.android.rookmusicplayer.architecture;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.android.rookmusicplayer.Songs;

import java.util.List;

@Dao
public interface SavedQueueDao
{
    @Insert
    void insert(SavedQueue savedQueue);

    @Delete
    void delete(SavedQueue savedQueue);

    @Update
    void update(SavedQueue savedQueue);

    @Query("DELETE FROM saved_queue")
    void deleteSavedQueue();

    @Query("SELECT id, title, album, albumKey, art, artist, artistKey, duration, path, track FROM saved_queue")
    LiveData<List<Songs>> getSavedQueue();
}

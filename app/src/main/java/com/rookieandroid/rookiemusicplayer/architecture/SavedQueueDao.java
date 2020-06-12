package com.rookieandroid.rookiemusicplayer.architecture;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.rookieandroid.rookiemusicplayer.Songs;
import java.util.List;

@Dao
public interface SavedQueueDao
{
    @Insert
    void insertAll(List<Songs> savedQueue);

    @Query("DELETE FROM saved_queue")
    void deleteSavedQueue();

    @Query("SELECT * FROM saved_queue")
    LiveData<List<Songs>> getSavedQueue();
}

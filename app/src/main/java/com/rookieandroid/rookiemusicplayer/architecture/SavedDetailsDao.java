package com.rookieandroid.rookiemusicplayer.architecture;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.rookieandroid.rookiemusicplayer.SavedStateDetails;

import java.util.List;

@Dao
public interface SavedDetailsDao
{
    @Insert
    void insert(SavedDetails savedDetails);

    @Delete
    void delete(SavedDetails savedDetails);

    @Update
    void update(SavedDetails savedDetails);

    @Query("DELETE FROM saved_details")
    void deleteSavedDetails();

    @Query("SELECT * FROM saved_details")
    LiveData<List<SavedStateDetails>> getSavedDetails();
}

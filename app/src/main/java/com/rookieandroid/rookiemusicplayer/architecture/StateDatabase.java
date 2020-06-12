package com.rookieandroid.rookiemusicplayer.architecture;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.rookieandroid.rookiemusicplayer.Songs;

@Database(entities = {Songs.class, SavedDetails.class}, version = 1, exportSchema = false)
public abstract class StateDatabase extends RoomDatabase
{
    private static StateDatabase instance;

    public abstract SavedQueueDao savedQueueDao();
    public abstract SavedDetailsDao savedDetailsDao();

    public static synchronized StateDatabase getInstance(Context context)
    {
        if(instance == null)
        {
            instance = Room.databaseBuilder(context.getApplicationContext(), StateDatabase.class, "state_database")
                    .fallbackToDestructiveMigration().addCallback(roomCallBack).build();
        }

        return instance;
    }

    private static RoomDatabase.Callback roomCallBack = new RoomDatabase.Callback()
    {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db)
        {
            super.onCreate(db);
        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db)
        {
            super.onOpen(db);
        }
    };
}

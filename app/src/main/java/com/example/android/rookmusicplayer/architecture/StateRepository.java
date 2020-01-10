package com.example.android.rookmusicplayer.architecture;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.android.rookmusicplayer.SavedStateDetails;
import com.example.android.rookmusicplayer.Songs;

import java.util.List;

public class StateRepository
{
    private SavedQueueDao savedQueueDao;
    private SavedDetailsDao savedDetailsDao;
    private LiveData<List<Songs>> savedQueue;
    private LiveData<List<SavedStateDetails>> savedStateDetails;

    public StateRepository(Application application)
    {
        StateDatabase stateDatabase = StateDatabase.getInstance(application);
        savedQueueDao = stateDatabase.savedQueueDao();
        savedDetailsDao = stateDatabase.savedDetailsDao();
        savedQueue = savedQueueDao.getSavedQueue();
        savedStateDetails = savedDetailsDao.getSavedDetails();
    }

    public void insert(SavedQueue savedQueue)
    {
        new InsertQueueAsyncTask(savedQueueDao).execute(savedQueue);
    }

    public void update(SavedQueue savedQueue)
    {
        new UpdateQueueAsyncTask(savedQueueDao).execute(savedQueue);
    }

    public void delete(SavedQueue savedQueue)
    {
        new DeleteQueueAsyncTask(savedQueueDao).execute(savedQueue);
    }

    public void insert(SavedDetails savedDetails)
    {
        new InsertDetailsAsyncTask(savedDetailsDao).execute(savedDetails);
    }

    public void update(SavedDetails savedDetails)
    {
        new UpdateDetailsAsyncTask(savedDetailsDao).execute(savedDetails);
    }

    public void delete(SavedDetails savedDetails)
    {
        new DeleteDetailsAsyncTask(savedDetailsDao).execute(savedDetails);
    }

    public void deleteSavedQueue()
    {
        new DeleteSavedQueueAsyncTask(savedQueueDao).execute();
    }

    public void deleteSavedDetails()
    {
        new DeleteDetailsAsyncTask(savedDetailsDao).execute();
    }

    public LiveData<List<Songs>> getSavedQueue()
    {
        return savedQueue;
    }

    public LiveData<List<SavedStateDetails>> getSavedStateDetails()
    {
        return  savedStateDetails;
    }

    private static class InsertQueueAsyncTask extends AsyncTask<SavedQueue, Void, Void>
    {
        private SavedQueueDao savedQueueDao;

        private InsertQueueAsyncTask(SavedQueueDao savedQueueDao)
        {
            this.savedQueueDao = savedQueueDao;
        }

        @Override
        protected Void doInBackground(SavedQueue... savedQueues)
        {
            savedQueueDao.insert(savedQueues[0]);
            return null;
        }
    }

    private static class UpdateQueueAsyncTask extends AsyncTask<SavedQueue, Void, Void>
    {
        private SavedQueueDao savedQueueDao;

        private UpdateQueueAsyncTask(SavedQueueDao savedQueueDao)
        {
            this.savedQueueDao = savedQueueDao;
        }

        @Override
        protected Void doInBackground(SavedQueue... savedQueues)
        {
            savedQueueDao.update(savedQueues[0]);
            return null;
        }
    }

    private static class DeleteQueueAsyncTask extends AsyncTask<SavedQueue, Void, Void>
    {
        private SavedQueueDao savedQueueDao;

        private DeleteQueueAsyncTask(SavedQueueDao savedQueueDao)
        {
            this.savedQueueDao = savedQueueDao;
        }

        @Override
        protected Void doInBackground(SavedQueue... savedQueues)
        {
            savedQueueDao.delete(savedQueues[0]);
            return null;
        }
    }

    private static class DeleteSavedQueueAsyncTask extends AsyncTask<Void, Void, Void>
    {
        private SavedQueueDao savedQueueDao;

        private DeleteSavedQueueAsyncTask(SavedQueueDao savedQueueDao)
        {
            this.savedQueueDao = savedQueueDao;
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            savedQueueDao.deleteSavedQueue();
            return null;
        }
    }

    private static class InsertDetailsAsyncTask extends AsyncTask<SavedDetails, Void, Void>
    {
        private SavedDetailsDao savedDetailsDao;

        private InsertDetailsAsyncTask(SavedDetailsDao savedDetailsDao)
        {
            this.savedDetailsDao = savedDetailsDao;
        }

        @Override
        protected Void doInBackground(SavedDetails... savedDetails)
        {
            savedDetailsDao.insert(savedDetails[0]);
            return null;
        }
    }

    private static class UpdateDetailsAsyncTask extends AsyncTask<SavedDetails, Void, Void>
    {
        private SavedDetailsDao savedDetailsDao;

        private UpdateDetailsAsyncTask(SavedDetailsDao savedDetailsDao)
        {
            this.savedDetailsDao = savedDetailsDao;
        }

        @Override
        protected Void doInBackground(SavedDetails... savedDetails)
        {
            savedDetailsDao.update(savedDetails[0]);
            return null;
        }
    }

    private static class DeleteDetailsAsyncTask extends AsyncTask<SavedDetails, Void, Void>
    {
        private SavedDetailsDao savedDetailsDao;

        private DeleteDetailsAsyncTask(SavedDetailsDao savedDetailsDao)
        {
            this.savedDetailsDao = savedDetailsDao;
        }

        @Override
        protected Void doInBackground(SavedDetails... savedDetails)
        {
            savedDetailsDao.delete(savedDetails[0]);
            return null;
        }
    }

    private static class DeleteSavedDetailsAsyncTask extends AsyncTask<Void, Void, Void>
    {
        private SavedDetailsDao savedDetailsDao;

        private DeleteSavedDetailsAsyncTask(SavedDetailsDao savedDetailsDao)
        {
            this.savedDetailsDao = savedDetailsDao;
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            savedDetailsDao.deleteSavedDetails();
            return null;
        }
    }
}

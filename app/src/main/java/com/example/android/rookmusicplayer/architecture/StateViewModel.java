package com.example.android.rookmusicplayer.architecture;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.android.rookmusicplayer.SavedStateDetails;
import com.example.android.rookmusicplayer.Songs;

import java.util.List;

public class StateViewModel extends AndroidViewModel
{
    private StateRepository stateRepository;
    private LiveData<List<Songs>> savedQueue;
    private LiveData<List<SavedStateDetails>> savedStateDetails;


    public StateViewModel(@NonNull Application application)
    {
        super(application);
        stateRepository = new StateRepository(application);
        savedQueue = stateRepository.getSavedQueue();
        savedStateDetails = stateRepository.getSavedStateDetails();
    }

    public void insert(SavedQueue savedQueue)
    {
        stateRepository.insert(savedQueue);
    }

    public void update(SavedQueue savedQueue)
    {
        stateRepository.update(savedQueue);
    }

    public void delete(SavedQueue savedQueue)
    {
        stateRepository.delete(savedQueue);
    }

    public void insert(SavedDetails savedDetails)
    {
        stateRepository.insert(savedDetails);
    }

    public void update(SavedDetails savedDetails)
    {
        stateRepository.update(savedDetails);
    }

    public void delete(SavedDetails savedDetails)
    {
        stateRepository.delete(savedDetails);
    }

    public void deleteSavedQueue()
    {
        stateRepository.deleteSavedQueue();
    }

    public void deleteSavedDetails()
    {
        stateRepository.deleteSavedDetails();
    }

    public LiveData<List<Songs>> getSavedQueue()
    {
        return savedQueue;
    }

    public LiveData<List<SavedStateDetails>> getSavedStateDetails()
    {
        return savedStateDetails;
    }
}

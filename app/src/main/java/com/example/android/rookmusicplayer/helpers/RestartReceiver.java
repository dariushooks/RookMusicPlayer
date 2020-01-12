package com.example.android.rookmusicplayer.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.android.rookmusicplayer.MediaPlaybackService;

public class RestartReceiver extends BroadcastReceiver
{
    private final String TAG = RestartReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.i(TAG, "Restarting Service");
        String action = intent.getAction();
        if(action.equals("keepServiceRunning"))
        {
            context.startService(new Intent(context, MediaPlaybackService.class));
        }
    }
}

package com.example.enigma.communications;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class MessagingServiceWorker extends Worker {
    private final String TAG = "MessagingServiceWorker";

    private final Context context;

    public MessagingServiceWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "doWork called for: " + this.getId());
        Log.d(TAG, "Service Running: " + MessagingService.isServiceRunning);

        if(!MessagingService.isServiceRunning)
        {
            Intent intent = new Intent(this.context, MessagingService.class);
            ContextCompat.startForegroundService(context, intent);
        }
        return Result.success();
    }

    @Override
    public void onStopped() {
        Log.d(TAG, "onStopped called for: " + this.getId());

        super.onStopped();
    }
}

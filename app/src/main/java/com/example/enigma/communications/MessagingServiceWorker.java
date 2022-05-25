package com.example.enigma.communications;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class MessagingServiceWorker extends Worker {

    private final Context context;

    public MessagingServiceWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        if(!MessagingService.isRunning())
        {
            Intent intent = new Intent(this.context, MessagingService.class);
            ContextCompat.startForegroundService(context, intent);
        }
        return Result.success();
    }
}

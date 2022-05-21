package com.example.enigma.communications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class ServiceRestarter extends BroadcastReceiver {
    private final String TAG = "ServiceRestarter";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive called");

        WorkManager workManager = WorkManager.getInstance(context);
        OneTimeWorkRequest startServiceRequest = new OneTimeWorkRequest.Builder(
                MessagingServiceWorker.class).build();
        workManager.enqueue(startServiceRequest);
    }
}

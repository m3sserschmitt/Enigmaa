package com.example.enigma;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.example.enigma.communications.MessagingService;
import com.example.enigma.communications.MessagingServiceWorker;

import java.util.concurrent.TimeUnit;

public class ChatsFragment extends Fragment {
    private final String TAG = "MainActivity";

    public ChatsFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstance)
    {
//        startMessagingServiceViaWorker();
//        startMessagingService();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
    {
        inflater.inflate(R.menu.chats_toolbar_menu, menu);

        MenuItem switchItem = menu.findItem(R.id.connection_switch);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch connectionSwitch = (Switch) switchItem.getActionView();

        connectionSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked)
            {
                startMessagingServiceViaWorker();
                startMessagingService();
            } else {
                stopMessagingService();
            }
        });
    }

    public void startMessagingService()
    {
        Log.d(TAG, "startService called");
        if(!MessagingService.isServiceRunning)
        {
            Intent messagingServiceIntent = new Intent(requireActivity(), MessagingService.class);
            ContextCompat.startForegroundService(requireActivity(), messagingServiceIntent);
        }
    }

    private void startMessagingServiceViaWorker() {

        String UNIQUE_WORK_NAME = "StartMyServiceViaWorker";
        WorkManager workManager = WorkManager.getInstance(requireActivity());

        PeriodicWorkRequest request =
                new PeriodicWorkRequest.Builder(
                        MessagingServiceWorker.class,
                        16,
                        TimeUnit.MINUTES)
                        .build();

        workManager.enqueueUniquePeriodicWork(UNIQUE_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request);
    }

    public void stopMessagingService()
    {
        Log.d(TAG, "stopService called");
        if(MessagingService.isServiceRunning)
        {
            Intent messagingServiceIntent = new Intent(requireActivity(), MessagingService.class);
            requireActivity().stopService(messagingServiceIntent);
        }
    }
}
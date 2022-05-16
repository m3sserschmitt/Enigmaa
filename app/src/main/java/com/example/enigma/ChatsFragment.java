package com.example.enigma;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.example.enigma.communications.MessagingService;

public class ChatsFragment extends Fragment {

    private MessagingService messagingService;

    public ChatsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        messagingService = new MessagingService(requireActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chats, container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
    {
        inflater.inflate(R.menu.toolbar_menu, menu);

        MenuItem switchItem = menu.findItem(R.id.connection_switch);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch connectionSwitch = (Switch) switchItem.getActionView();

        connectionSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked)
            {
                if(!isMessagingServiceRunning(messagingService.getClass()))
                {
                    Intent messagingServiceIntent = new Intent(requireActivity(), messagingService.getClass());
                    requireActivity().startService(messagingServiceIntent);
                } else if(!messagingService.isClientConnected()){
                    messagingService.initAndConnectClientAsync();
                }
            } else {
                messagingService.shutdownClient();
            }
        });

        connectionSwitch.setChecked(messagingService.isClientConnected());
    }

    private boolean isMessagingServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) requireActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}
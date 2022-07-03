/*  Enigma - Onion Routing based messaging app.
    Copyright (C) 2022  Romulus-Emanuel Ruja <romulus-emanuel.ruja@tutanota.com>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package com.example.enigma;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.enigma.communications.MessagingService;
import com.example.enigma.communications.MessagingServiceWorker;
import com.example.enigma.databinding.ActivityMainBinding;
import com.example.enigma.setup.InitialSetupActivity;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private interface SetupDoneListener {
        void initialSetupDone();
    }

    private void startMessagingService()
    {
        if(!MessagingService.isRunning())
        {
            Intent messagingServiceIntent = new Intent(this, MessagingService.class);
            ContextCompat.startForegroundService(this, messagingServiceIntent);
        }
    }

    private void startMessagingServiceViaWorker() {

        String UNIQUE_WORK_NAME = "StartMyServiceViaWorker";
        WorkManager workManager = WorkManager.getInstance(this);

        PeriodicWorkRequest request =
                new PeriodicWorkRequest.Builder(
                        MessagingServiceWorker.class,
                        16,
                        TimeUnit.MINUTES)
                        .build();

        workManager.enqueueUniquePeriodicWork(UNIQUE_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request);
    }

    private void stopMessagingService()
    {
        if(MessagingService.isRunning())
        {
            Intent messagingServiceIntent = new Intent(this, MessagingService.class);
            stopService(messagingServiceIntent);
        }
    }

    private final SetupDoneListener setupDoneListener = () -> {
        startMessagingServiceViaWorker();
        startMessagingService();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_chats, R.id.navigation_contacts, R.id.navigation_add_contact)
                .build();

        NavController navController = Navigation.findNavController(this,
                R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        initialSetup();
    }

    @Override
    protected void onUserLeaveHint() {
        MessagingService.setNoSessionOnFocus();
    }

    private void initialSetup()
    {
        LocalAppStorage localAppStorage = new LocalAppStorage(this);
        String privateKeyPath = localAppStorage.getPrivateKeyPath();
        String guardHostname = localAppStorage.getGuardHostname();

        if(privateKeyPath == null || guardHostname == null)
        {
            Intent initialSetupActivity = new Intent(this, InitialSetupActivity.class);

            ActivityResultLauncher<Intent> initialSetupActivityRegister = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if(result.getResultCode() == Activity.RESULT_OK){
                            Toast.makeText(getApplicationContext(), "Success",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Something went wrong",
                                    Toast.LENGTH_SHORT).show();
                        }

                        setupDoneListener.initialSetupDone();
                    }
            );

            initialSetupActivityRegister.launch(initialSetupActivity);
        } else {
            setupDoneListener.initialSetupDone();
        }
    }
}
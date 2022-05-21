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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.enigma.circuit.CircuitBuilder;
import com.example.enigma.communications.MessagingService;
import com.example.enigma.communications.MessagingServiceWorker;
import com.example.enigma.communications.ServiceRestarter;
import com.example.enigma.database.AppDatabase;
import com.example.enigma.database.Node;
import com.example.enigma.databinding.ActivityMainBinding;
import com.example.enigma.setup.InitialSetupActivity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("enigma");
    }

    public interface SetupDoneListener {
        
    }

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

    private void initialSetup()
    {
        SharedPreferences sharedPreferences = this.getSharedPreferences(
                getString(R.string.shared_preferences), MODE_PRIVATE);

        String privateKey = sharedPreferences.getString("privateKey", null);
        String guardHostname = sharedPreferences.getString("guardHostname", null);

        if(privateKey == null || guardHostname == null)
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
                    }
            );

            initialSetupActivityRegister.launch(initialSetupActivity);
        }
    }
}
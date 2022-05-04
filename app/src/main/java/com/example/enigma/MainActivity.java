package com.example.enigma;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.enigma.database.AppDatabase;
import com.example.enigma.database.Edge;
import com.example.enigma.database.EdgeDao;
import com.example.enigma.database.Node;
import com.example.enigma.database.NodeDao;
import com.example.enigma.database.NodeWithNeighbor;
import com.example.enigma.databinding.ActivityMainBinding;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("enigma");
    }

    private void initialSetup()
    {
        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.enigma",
                MODE_PRIVATE);

        String privateKey = sharedPreferences.getString("privateKey", null);
        String guardHostname = sharedPreferences.getString("guardHostname", null);

        if(privateKey == null || guardHostname == null)
        {
            Intent initialSetupActivity = new Intent(this,
                    InitialSetupActivity.class);

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initialSetup();
    }

    public native int initializeClient(String publicKeyPath, String privateKeyPath, String hostname,
                                       String port, boolean useTls, String serverPublicKey);
}
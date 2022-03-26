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

import com.example.enigma.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'enigma' library on application startup.
    static {
        System.loadLibrary("enigma");
    }

    private void checkKeys()
    {
        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.enigma",
                MODE_PRIVATE);
        String privateKeyFile = sharedPreferences.getString("privateKey", null);

        if(privateKeyFile == null)
        {
            Intent initialSetupActivity = new Intent(this,
                    InitialSetupActivity.class);

            ActivityResultLauncher<Intent> keyGeneratorActivity = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if(result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();

                            if(data == null)
                            {
                                Toast.makeText(getApplicationContext(),
                                        "Something went wrong",
                                        Toast.LENGTH_SHORT).show();

                                return;
                            }

                            String publicKey = data.getStringExtra("publicKey");
                            String privateKey = data.getStringExtra("privateKey");
                            String hostname = data.getStringExtra("hostname");

                            boolean success = publicKey != null && privateKey != null
                                    && hostname != null;

                            if(success)
                            {
                                Toast.makeText(getApplicationContext(), "Success",
                                        Toast.LENGTH_SHORT).show();

                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                editor.putString("publicKey", publicKey);
                                editor.putString("privateKey", privateKey);
                                editor.putString("hostname", hostname);

                                editor.apply();

                            }else {
                                Toast.makeText(getApplicationContext(), "Failure",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Something went wrong",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
            );

            keyGeneratorActivity.launch(initialSetupActivity);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.enigma.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        this.checkKeys();
    }

    public native int initializeClient(String publicKeyPath, String privateKeyPath, String hostname, String port, boolean useTls, String serverPublicKey);
}
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

    // Used to load the 'enigma' library on application startup.
    static {
        System.loadLibrary("enigma");
    }

    private void initialSetup()
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

        this.initialSetup();

//        ExecutorService executor = Executors.newSingleThreadExecutor();

//        executor.execute(() -> {
//            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
//
//            NodeDao nodeDao = db.nodeDao();
//            EdgeDao edgeDao = db.edgeDao();
//
//            Node node1 = new Node();
//            Node node2 = new Node();
//            Node node3 = new Node();
//
//            node1.setAddress("node-1");
//            node2.setAddress("node-2");
//            node3.setAddress("node-3");
//
//            nodeDao.insertAll(node1, node2, node3);
//
//            Edge edge1 = new Edge();
//            Edge edge2 = new Edge();
//
//            edge1.setOrigin("node-1");
//            edge1.setSecondNode("node-2");
//            edge2.setOrigin("node-1");
//            edge2.setSecondNode("node-3");
//
//            edgeDao.insertAll(edge1, edge2);
//
//            NodeWithNeighbor neighbors = nodeDao.getNeighbors("node-1");
//
//            Log.i("edge", neighbors.getEdges().get(0).getSecondNode());
//            Log.i("edge", neighbors.getEdges().get(1).getSecondNode());
//        });

    }

    public native int initializeClient(String publicKeyPath, String privateKeyPath, String hostname, String port, boolean useTls, String serverPublicKey);
}
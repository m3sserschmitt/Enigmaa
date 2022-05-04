package com.example.enigma;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.enigma.database.AppDatabase;
import com.example.enigma.databinding.FragmentGuardSetupBinding;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GuardSetupFragment extends Fragment {

    private FragmentGuardSetupBinding binding;
    private FragmentActivity activity;
    private SharedPreferences sharedPreferences;

    private String guardHostname;

    private boolean readGuardHostname()
    {
        guardHostname = binding.hostnameEditText.getText().toString();

        if(guardHostname.equals(""))
        {
            Toast.makeText(activity, "Enter a valid hostname", Toast.LENGTH_SHORT).show();

            return false;
        }

        sharedPreferences.edit().putString("guardHostname", guardHostname).apply();

        return true;
    }

    private void terminateActivity()
    {
        activity.setResult(Activity.RESULT_OK);
        activity.finish();
    }

    @Nullable
    private String download(@NonNull String host)
    {
        URL url;
        HttpURLConnection urlConnection;
        StringBuilder result = new StringBuilder();

        try {
            url = new URL("http", host, 8080, "get_network_graph");
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = urlConnection.getInputStream();
            InputStreamReader reader = new InputStreamReader(in);

            int data = reader.read();
            char currentCharacter;

            while(data != -1)
            {
                currentCharacter = (char) data;
                result.append(currentCharacter);

                data = reader.read();
            }
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }

        return result.toString();
    }

    private boolean parseNetworkGraph(@Nullable String networkGraph)
    {
        AppDatabase databaseInstance = AppDatabase.getInstance(activity);
        NetworkGraphParser graphParser = new NetworkGraphParser(databaseInstance);

        if(!graphParser.parse(networkGraph))
        {
            return false;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("guardAddress", graphParser.getGuardAddress());
        editor.putString("guardPublicKey", graphParser.getGuardPublicKey());

        editor.apply();

        return true;
    }

    private void downloadNetworkState()
    {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            final String networkGraph = download(guardHostname);
            final boolean success = parseNetworkGraph(networkGraph);

            handler.post(() -> {

                if(success)
                {
                    terminateActivity();
                } else {
                    String errorMessage;

                    if(networkGraph == null)
                    {
                        errorMessage = "Errors occurred while downloading network state";
                    } else {
                        errorMessage = "Errors occurred while processing network graph";
                    }

                    Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void setupRequiredDataAndExit()
    {
        if(!readGuardHostname())
        {
            return;
        }

        downloadNetworkState();
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentGuardSetupBinding.inflate(inflater, container, false);
        activity = requireActivity();
        sharedPreferences = activity.getSharedPreferences("com.example.enigma",
                Context.MODE_PRIVATE);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.setHostnameButton.setOnClickListener(view1 -> setupRequiredDataAndExit());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
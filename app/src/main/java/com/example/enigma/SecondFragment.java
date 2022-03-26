package com.example.enigma;

import android.app.Activity;
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

import com.example.enigma.databinding.FragmentSecondBinding;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private FragmentActivity activity;

    private void setUserInputData()
    {
        String hostname = binding.hostnameEditText.getText().toString();

        if(hostname.equals(""))
        {
            Toast.makeText(activity, "Enter a valid hostname", Toast.LENGTH_SHORT).show();

            return;
        }

        InitialSetupActivity.resultData.putExtra("hostname", hostname);
    }

    private void terminateActivity()
    {
        activity.setResult(Activity.RESULT_OK, InitialSetupActivity.resultData);
        activity.finish();
    }

    /*
    private boolean saveFile(String content, String saveFileName)
    {
        FileOutputStream outputStream;

        try {
            outputStream = activity.openFileOutput(saveFileName, Context.MODE_PRIVATE);
            outputStream.write(content.getBytes());
            outputStream.close();
        } catch (Exception e)
        {
            return false;
        }

        return true;
    }
    */

    @Nullable
    private String download(String host, int port, String path)
    {
        URL url;
        HttpURLConnection urlConnection;
        StringBuilder result = new StringBuilder();

        try {
            url = new URL("http", host, port, path);
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

            Log.i("exception when downloading resource", e.toString());
            e.printStackTrace();

            return null;
        }

        return result.toString();
    }

    private boolean downloadAndParseGraph(String host, int port, String path
            /*, String saveFileName*/)
    {
        String jsonContent = this.download(host, port, path);

        if(jsonContent == null)
        {
            return false;
        }

        NetworkGraphParser graphParser = new NetworkGraphParser();

        return graphParser.parse(jsonContent);
    }

    private void downloadRequiredData()
    {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            String host = InitialSetupActivity.resultData.getStringExtra("hostname");

            final boolean success = downloadAndParseGraph(host, 8080,
                    "get_network_graph"/*,
                    "network_graph.json"*/);

            handler.post(() -> {

                if(success)
                {
                    terminateActivity();
                } else {
                    Toast.makeText(activity, "Errors occurred while processing network graph",
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void setupRequiredDataAndExit()
    {
        this.setUserInputData();
        this.downloadRequiredData();
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);

        activity = Objects.requireNonNull(getActivity());

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
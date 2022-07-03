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

package com.example.enigma.setup;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.enigma.LocalAppStorage;
import com.example.enigma.NetworkGraphParser;
import com.example.enigma.database.AppDatabase;
import com.example.enigma.databinding.FragmentGuardSetupBinding;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;

public class GuardSetupFragment extends Fragment {

    private FragmentGuardSetupBinding binding;

    private String guardHostname;
    private String directoryPortNumber;
    private String onionServicePortNumber;

    private enum Status {SUCCESS, DOWNLOAD_FAILED, GRAPH_PARSING_FAILED}

    private interface DownloadCompletedListener {
        void downloadCompleted(String content, Handler handler);
    }

    private interface SetupDoneListener {
        void setupDone(Status status, Handler handler);
    }

    private final SetupDoneListener setupDoneListener = (status, handler) -> handler.post(() -> {
        switch (status)
        {
            case SUCCESS:
                Toast.makeText(requireActivity(), "Success", Toast.LENGTH_SHORT).show();
                break;
            case GRAPH_PARSING_FAILED:
                Toast.makeText(requireActivity(),
                        "Errors occurred while processing network graph",
                        Toast.LENGTH_SHORT).show();
                break;
            case DOWNLOAD_FAILED:
                Toast.makeText(requireActivity(),
                        "Errors occurred while downloading network state",
                        Toast.LENGTH_SHORT).show();
        }
        finishSetup();
    });

    private final DownloadCompletedListener downloadCompletedListener = (content, handler) -> {
        if(content == null)
        {
            setupDoneListener.setupDone(Status.DOWNLOAD_FAILED, handler);
            return;
        }

        parseNetworkGraph(content, handler);
    };

    private boolean readGuardHostname()
    {
        guardHostname = binding.hostnameEditText.getText().toString();
        directoryPortNumber = binding.directoryPortNumberEditText.getText().toString();
        onionServicePortNumber = binding.onionServicePortNumberTextEdit.getText().toString();

        Context context = requireActivity();

        if(guardHostname.equals(""))
        {
            Toast.makeText(context, "Enter a valid hostname", Toast.LENGTH_SHORT).show();

            return false;
        } else if(directoryPortNumber.equals(""))
        {
            Toast.makeText(context, "Enter a valid directory port number", Toast.LENGTH_SHORT)
                    .show();

            return false;
        } else if(onionServicePortNumber.equals(""))
        {
            Toast.makeText(context, "Enter a valid directory port number", Toast.LENGTH_SHORT)
                    .show();
        }

        return true;
    }

    private void finishSetup()
    {
        LocalAppStorage localAppStorage = new LocalAppStorage(requireActivity());

        localAppStorage.setGuardHostname(guardHostname);
        localAppStorage.setDirectoryPortNumber(directoryPortNumber);
        localAppStorage.setOnionServicePortNumber(onionServicePortNumber);

        FragmentActivity activity = requireActivity();

        activity.setResult(Activity.RESULT_OK);
        activity.finish();
    }

    @Nullable
    private String download(@NonNull String host, int port)
    {
        URL url;
        HttpURLConnection urlConnection;
        StringBuilder result = new StringBuilder();

        try {
            // ToDo: use https!!
            url = new URL("http", host, port, "get_network_graph");
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

    private void parseNetworkGraph(@Nullable String networkGraph, Handler handler)
    {
        AppDatabase databaseInstance = AppDatabase.getInstance(requireActivity());
        NetworkGraphParser graphParser = new NetworkGraphParser(databaseInstance);

        if(!graphParser.parse(networkGraph))
        {
            setupDoneListener.setupDone(Status.GRAPH_PARSING_FAILED, handler);
        }

        LocalAppStorage localAppStorage = new LocalAppStorage(requireActivity());

        localAppStorage.setGuardAddress(graphParser.getGuardAddress());
        localAppStorage.setGuardPublicKeyPEM(graphParser.getGuardPublicKey());

        setupDoneListener.setupDone(Status.SUCCESS, handler);
    }

    private void downloadNetworkState()
    {
        Handler handler = new Handler(Looper.getMainLooper());

        Executors.newSingleThreadExecutor().execute(() -> {
            String networkGraph = download(guardHostname, Integer.parseInt(directoryPortNumber));
            downloadCompletedListener.downloadCompleted(networkGraph, handler);
        });
    }

    private void setup()
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

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.setHostnameButton.setOnClickListener(view1 -> setup());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

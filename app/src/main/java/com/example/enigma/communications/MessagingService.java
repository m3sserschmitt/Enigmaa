package com.example.enigma.communications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.enigma.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessagingService extends Service {

    private SharedPreferences sharedPreferences;

    private String publicKey;
    private String privateKey;

    private String guardAddress;

    private boolean clientInitialized;

    @Override
    public void onCreate()
    {
        super.onCreate();
        sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences), MODE_PRIVATE);

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
        {
            startService();
        } else {
            startForeground(1, new Notification());
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void startService()
    {
        String notificationChannelId = "messaging.permanence";
        String channelName = "Background Messaging Service";

        NotificationChannel channel = new NotificationChannel(notificationChannelId, channelName,
                NotificationManager.IMPORTANCE_NONE);
        channel.setLightColor(Color.BLUE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(channel);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,
                notificationChannelId);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        super.onStartCommand(intent, flags, startId);

        if(!clientInitialized) {
            String guardHostname = sharedPreferences.getString("guardHostname", null);
            String guardPublicKey = sharedPreferences.getString("guardPublicKey", null);

            loadKeys();
            guardAddress = initializeClient(publicKey, privateKey, guardHostname, "3000",
                    true, guardPublicKey);

            clientInitialized = guardAddress != null;

            if(clientInitialized)
            {
                Toast.makeText(this, "Messaging service started", Toast.LENGTH_SHORT).show();
            }
        }

        return START_STICKY;
    }



    @Override
    public void onDestroy()
    {
        super.onDestroy();

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, ServiceRestarter.class);
        this.sendBroadcast(broadcastIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Nullable
    private String readFile(String file)
    {
        File filesDir = getFilesDir();
        File filePath = new File(filesDir, file);

        StringBuilder content = new StringBuilder();

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
            String line;

            while((line = bufferedReader.readLine()) != null)
            {
                content.append(line);
                content.append("\n");
            }

            bufferedReader.close();

        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }

        return content.toString();
    }

    private void loadKeys()
    {
        publicKey = readFile("public.pem");
        privateKey = readFile("private.pem");

        if(publicKey == null || privateKey == null)
        {
            Toast.makeText(this, "Error while loading Private Key", Toast.LENGTH_SHORT).show();
        }
    }

    public String getGuardAddress()
    {
        return guardAddress;
    }

    public native String initializeClient(String publicKeyPEM, String privateKeyPEM, String hostname,
                                          String port, boolean useTls, String guardPublicKeyPEM);

}

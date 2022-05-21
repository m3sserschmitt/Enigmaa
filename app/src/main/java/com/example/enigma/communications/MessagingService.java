package com.example.enigma.communications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.enigma.FileUtils;
import com.example.enigma.MainActivity;
import com.example.enigma.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessagingService extends Service {

    static {
        System.loadLibrary("enigma");
    }

    private final String TAG = "MessagingService";
    public static boolean isServiceRunning;
    private final String CHANNEL_ID = "MESSAGING_SERVICE_NOTIFICATION_CHANNEL";

    private SharedPreferences sharedPreferences;
    private FileUtils fileUtils;

    private String publicKey;
    private String privateKey;

    private String guardAddress;

    public MessagingService()
    {
        Log.d(TAG, "constructor called");
        isServiceRunning = false;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.d(TAG, "onCreate called");

        createNotificationChannel();
        isServiceRunning = true;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d(TAG, "onStartCommand called");

        sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences), MODE_PRIVATE);
        fileUtils = FileUtils.getInstance(this);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Messaging Service Running")
                        .setContentText("Connected to enigma4 onion service")
                                .setContentIntent(pendingIntent)
                                        .build();

        initAndConnectClientAsync();

        startForeground(1, notification);

        return START_STICKY;
    }

    private void createNotificationChannel()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String appName = getString(R.string.app_name);
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    appName,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy called");
        isServiceRunning = false;
        stopForeground(true);

        //Executors.newSingleThreadExecutor().execute(this::nativeShutdownClient);

        Intent broadcastIntent = new Intent(this, ServiceRestarter.class);
        sendBroadcast(broadcastIntent);

        super.onDestroy();
    }

    public boolean initializeClient()
    {
        loadKeys();
        return nativeInitializeClient(publicKey, privateKey, true);
    }

    public void initializeClientAsync()
    {
        Executors.newSingleThreadExecutor().execute(this::initializeClient);
    }

    public boolean connectClient()
    {
        String guardHostname = sharedPreferences.getString("guardHostname", null);
        String guardPublicKey = sharedPreferences.getString("guardPublicKey", null);

        guardAddress = nativeOpenConnection(guardHostname, "3000", guardPublicKey);

        return guardAddress != null;
    }

    public void connectClientAsync()
    {
        Executors.newSingleThreadExecutor().execute(this::connectClient);
    }

    public boolean initAndConnectClient()
    {
        return initializeClient() && connectClient();
    }

    public void initAndConnectClientAsync()
    {
        Executors.newSingleThreadExecutor().execute(this::initAndConnectClient);
    }

    private void loadKeys()
    {
        publicKey = fileUtils.readFile("public.pem");
        privateKey = fileUtils.readFile("private.pem");
    }

    public String getGuardAddress()
    {
        return guardAddress;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    private native boolean nativeInitializeClient(String publicKeyPEM, String privateKeyPEM, boolean useTls);

    private native String nativeOpenConnection(String hostname, String port, String guardPublicKeyPEM);

}

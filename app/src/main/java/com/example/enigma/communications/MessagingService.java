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
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.enigma.FileUtils;
import com.example.enigma.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessagingService extends Service {

    private final Context context;
    private final FileUtils fileUtils;

    private String publicKey;
    private String privateKey;

    private String guardAddress;

    private boolean clientInitialized;
    private boolean clientConnected;

    public MessagingService()
    {
        context = this;
        fileUtils = FileUtils.getInstance(context);
        clientConnected = false;
        clientInitialized = false;
    }

    public MessagingService(Context context)
    {
        this.context = context;
        fileUtils = FileUtils.getInstance(context);
        clientConnected = false;
        clientInitialized = false;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

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

    public boolean initializeClient()
    {
        if(!clientInitialized) {

            loadKeys();
            clientInitialized = initializeClient(publicKey, privateKey, true);
        }

        return clientInitialized;
    }

    public void initializeClientAsync()
    {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(this::initializeClient);
    }

    public boolean connectClient()
    {
        if(clientInitialized && !clientConnected)
        {
            SharedPreferences sharedPreferences = context.getSharedPreferences(
                    context.getString(R.string.shared_preferences), MODE_PRIVATE);
            String guardHostname = sharedPreferences.getString("guardHostname", null);
            String guardPublicKey = sharedPreferences.getString("guardPublicKey", null);

            guardAddress = openConnection(guardHostname, "3000", guardPublicKey);

            clientConnected = guardAddress != null;
        }

        return clientInitialized && clientConnected;
    }

    public void connectClientAsync()
    {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(this::connectClient);
    }

    public boolean initAndConnectClient()
    {
        return initializeClient() && connectClient();
    }

    public void initAndConnectClientAsync()
    {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            initializeClient();
            connectClient();
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        super.onStartCommand(intent, flags, startId);

        initAndConnectClientAsync();

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

    private void loadKeys()
    {
        publicKey = fileUtils.readFile("public.pem");
        privateKey = fileUtils.readFile("private.pem");
    }

    public String getGuardAddress()
    {
        return guardAddress;
    }

    public void shutdownConnection()
    {
        closeConnection();
        clientConnected = false;
    }

    public void shutdownClient()
    {
        closeClient();
        clientInitialized = false;
        clientConnected = false;
    }

    public boolean isClientConnected() {
        return clientCreated() && clientIsConnected();
    }

    public boolean isClientInitialized() {
        return clientCreated();
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    private native boolean initializeClient(String publicKeyPEM, String privateKeyPEM, boolean useTls);

    private native String openConnection(String hostname, String port, String guardPublicKeyPEM);

    private native void closeConnection();

    private native void closeClient();

    private native boolean clientCreated();

    private native boolean clientIsConnected();
}

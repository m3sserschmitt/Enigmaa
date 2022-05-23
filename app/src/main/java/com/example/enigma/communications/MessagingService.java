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
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.enigma.ChatActivity;
import com.example.enigma.FileUtils;
import com.example.enigma.MainActivity;
import com.example.enigma.R;
import com.example.enigma.database.AppDatabase;
import com.example.enigma.database.Contact;
import com.example.enigma.database.ContactDao;
import com.example.enigma.database.Message;
import com.example.enigma.database.MessageDao;

import android.util.Base64;
import android.widget.Toast;

import java.io.File;
import java.sql.Time;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessagingService extends Service {

    static {
        System.loadLibrary("enigma");
    }

    private final String TAG = "MessagingService";
    private final int SERVICE_NOTIFICATION_ID = -1;
    private final String SERVICE_NOTIFICATION_CHANNEL_ID = "MESSAGING_SERVICE_NOTIFICATION_CHANNEL";
    private final String NEW_MESSAGE_NOTIFICATION_CHANNEL_ID = "NEW_MESSAGE_NOTIFICATION_CHANNEL";

    public static boolean isServiceRunning;

    private String publicKey;
    private String privateKey;

    private String guardAddress;

    public MessagingService()
    {
        Log.d(TAG, "constructor called");
        isServiceRunning = false;
    }

    private interface ClientConnectionListener {
        void clientConnected();
    }

    private interface ContactsLoadedListener {
        void contactsLoaded();
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.d(TAG, "onCreate called");

        createNotificationChannel(SERVICE_NOTIFICATION_CHANNEL_ID);
        createNotificationChannel(NEW_MESSAGE_NOTIFICATION_CHANNEL_ID);

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

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, SERVICE_NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Messaging Service Running")
                        .setContentText("Connected to enigma4 onion service")
                            .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                                .setContentIntent(pendingIntent)
                                        .build();

        startForeground(SERVICE_NOTIFICATION_ID, notification);
        initAndConnectClientAsync();
        return START_STICKY;
    }

    private void createNotificationChannel(String channelId)
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String appName = getString(R.string.app_name);
            NotificationChannel serviceChannel = new NotificationChannel(
                    channelId,
                    appName,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private void createNotification(String title, String content, int id)
    {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        //notificationIntent.putExtra("name", "");
        //notificationIntent.putExtra("address", "");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this,
                NEW_MESSAGE_NOTIFICATION_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(null, id, notification);
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
        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.shared_preferences), MODE_PRIVATE);

        String guardHostname = sharedPreferences.getString("guardHostname", null);
        String guardPublicKey = sharedPreferences.getString("guardPublicKey", null);
        String onionPortNumber = sharedPreferences.getString("onionPortNumber", null);

        if(guardHostname == null || guardPublicKey == null || onionPortNumber == null)
        {
            return false;
        }

        guardAddress = nativeOpenConnection(guardHostname, onionPortNumber, guardPublicKey);

        return guardAddress != null;
    }

    public void connectClientAsync()
    {
        Executors.newSingleThreadExecutor().execute(this::connectClient);
    }

    private String getMessageDecoded()
    {
        return new String(readLastMessage());
    }

    private void insertNewMessageInDatabase(String content, String sessionId)
    {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase databaseInstance = AppDatabase.getInstance(this);
            MessageDao messageDao = databaseInstance.messageDao();
            ContactDao contactDao = databaseInstance.contactDao();

            Contact sender = contactDao.findBySessionId(sessionId);

            if(sender != null)
            {
                messageDao.insertAll(new Message(sender.getAddress(), sessionId, content));
            }
        });
    }

    private void notifyUser(String messageContent, String sessionId)
    {
        Executors.newSingleThreadExecutor().execute(() -> {

            AppDatabase databaseInstance = AppDatabase.getInstance(this);
            ContactDao contactDao = databaseInstance.contactDao();
            Contact sender = contactDao.findBySessionId(sessionId);

            if(sender != null)
            {
                String name = sender.getNickName();
                createNotification(name, messageContent, (int)sender.getId());
            }
        });
    }

    private void startClientListener()
    {
        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(checkNewMessage())
                {
                    String sessionId = readLastSessionId();
                    String messageContent = getMessageDecoded();

                    insertNewMessageInDatabase(messageContent, sessionId);
                    notifyUser(messageContent, sessionId);
                }
            }
        }, 0, 1000);
    }

    public boolean initAndConnectClient()
    {
        return initializeClient() && connectClient();
    }

    private final ClientConnectionListener clientConnectionListener = this::loadContactsSessionsFromDatabase;

    private final ContactsLoadedListener contactsLoadedListener = this::startClientListener;

    private void loadContactsSessionsFromDatabase()
    {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase databaseInstance = AppDatabase.getInstance(this);
            ContactDao contactDao = databaseInstance.contactDao();

            List<Contact> contacts = contactDao.getAll();

            for(Contact contact : contacts)
            {
                byte[] sessionId = Base64.decode(contact.getSessionId(), Base64.DEFAULT);
                byte[] sessionKey = Base64.decode(contact.getSessionKey(), Base64.DEFAULT);

                if(loadContact(contact.getAddress(), sessionId, sessionKey) < 0)
                {
                    return;
                }
            }

            contactsLoadedListener.contactsLoaded();
        });
    }

    public void initAndConnectClientAsync()
    {
        Handler handler = new Handler(Looper.getMainLooper());

        Executors.newSingleThreadExecutor().execute(() -> {
            final boolean clientConnected = initAndConnectClient();
            if(clientConnected)
            {
                clientConnectionListener.clientConnected();
            }

            handler.post(() -> {
                if(!clientConnected)
                {
                    Toast.makeText(this, "Could not connect Guard Node",
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void loadKeys()
    {
        FileUtils fileUtils = FileUtils.getInstance(this);

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

    private native boolean checkNewMessage();

    private native int loadContact(String address, byte[] sessionId, byte[] sessionKey);

    private native String readLastSessionId();

    private native byte[] readLastMessage();
}

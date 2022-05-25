package com.example.enigma.communications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;

public class MessagingService extends Service {

    static {
        System.loadLibrary("enigma");
    }

    private static String sessionOnFocus;

    private final String SERVICE_NOTIFICATION_CHANNEL_ID = "MESSAGING_SERVICE_NOTIFICATION_CHANNEL";
    private final String NEW_MESSAGE_NOTIFICATION_CHANNEL_ID = "NEW_MESSAGE_NOTIFICATION_CHANNEL";

    private static boolean isServiceRunning;
    private static HashMap<String, onMessageReceivedListener> listeners = new HashMap<>();

    private interface ClientConnectionListener {
        void clientConnected();
    }

    private interface ContactsLoadedListener {
        void contactsLoaded();
    }

    public interface onMessageReceivedListener {
        void onMessage(String messageContent, String sessionId);
    }

    public MessagingService()
    {
        isServiceRunning = false;
    }

    public static boolean isRunning()
    {
        return isServiceRunning;
    }

    public static void setOnNewMessageListener(onMessageReceivedListener listener, Class<?> cls)
    {
        listeners.put(cls.getName(), listener);
    }

    public static void setSessionOnFocus(@NonNull String sessionOnFocus)
    {
        MessagingService.sessionOnFocus = sessionOnFocus;
    }

    public static void setAllSessionsOnFocus()
    {
        MessagingService.sessionOnFocus = "";
    }

    public static void setNoSessionOnFocus()
    {
        MessagingService.sessionOnFocus = null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

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
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, SERVICE_NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Messaging Service Running")
                        .setContentText("Connected to enigma4 onion service")
                            .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                                .setContentIntent(pendingIntent)
                                        .build();

        int SERVICE_NOTIFICATION_ID = -1;
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
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private void createNotification(String title, String content, int id)
    {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this,
                NEW_MESSAGE_NOTIFICATION_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_MAX)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(null, id, notification);
    }


    @Override
    public void onDestroy() {
        isServiceRunning = false;
        stopForeground(true);

        //Executors.newSingleThreadExecutor().execute(this::nativeShutdownClient);

        Intent broadcastIntent = new Intent(this, ServiceRestarter.class);
        sendBroadcast(broadcastIntent);

        super.onDestroy();
    }

    public boolean initializeClient()
    {
        FileUtils fileUtils = FileUtils.getInstance(this);

        String publicKey = Objects.requireNonNull(fileUtils.readFile("public.pem")).trim();
        String privateKey = Objects.requireNonNull(fileUtils.readFile("private.pem")).trim();

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

        return nativeOpenConnection(guardHostname, onionPortNumber, guardPublicKey) != null;
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

            if(sender != null && sender.getAddress() != null)
            {
                messageDao.insertAll(new Message(sender.getAddress(), sessionId, content));
            }
        });
    }

    private void notifyUser(String messageContent, String sessionId)
    {
        if(sessionOnFocus == null || (!sessionOnFocus.equals(sessionId)) && !sessionOnFocus.equals(""))
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
    }

    private void callbacks(String messageContent, String sessionId)
    {
        Set<Map.Entry<String, onMessageReceivedListener>> set = listeners.entrySet();

        for (Map.Entry<String, onMessageReceivedListener> entry : set) {
            entry.getValue().onMessage(messageContent, sessionId);
        }
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
                    callbacks(messageContent, sessionId);
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

    private native boolean nativeInitializeClient(String publicKeyPEM, String privateKeyPEM, boolean useTls);

    private native String nativeOpenConnection(String hostname, String port, String guardPublicKeyPEM);

    private native boolean checkNewMessage();

    private native int loadContact(String address, byte[] sessionId, byte[] sessionKey);

    private native String readLastSessionId();

    private native byte[] readLastMessage();
}

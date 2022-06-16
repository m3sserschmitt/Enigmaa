package com.example.enigma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.enigma.circuit.CircuitBuilder;
import com.example.enigma.communications.MessagingService;
import com.example.enigma.database.AppDatabase;
import com.example.enigma.database.Circuit;
import com.example.enigma.database.CircuitDao;
import com.example.enigma.database.Contact;
import com.example.enigma.database.ContactDao;
import com.example.enigma.database.Message;
import com.example.enigma.database.MessageDao;
import com.example.enigma.database.NodeDao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {

    private MessageAdapter messageAdapter;
    private RecyclerView messagesRecyclerView;
    private EditText messageInputEditText;

    private String contactName;
    private String foreignAddress;
    private String sessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();

        contactName = intent.getStringExtra("name");
        foreignAddress = intent.getStringExtra("address");
        sessionId = intent.getStringExtra("sessionId");

        setTitle(contactName);

        messagesRecyclerView = findViewById(R.id.messages_recycler_view);
        LinearLayoutManager layoutManager = (LinearLayoutManager) messagesRecyclerView.getLayoutManager();
        layoutManager.setStackFromEnd(true);

        messageInputEditText = findViewById(R.id.message_edit_text);

        getMessagesFromDatabase();
        getCircuit(foreignAddress);
        MessagingService.setOnNewMessageListener(onMessageReceivedListener, this.getClass());
        MessagingService.setSessionOnFocus(sessionId);
    }

    private final MessagingService.onMessageReceivedListener onMessageReceivedListener =
            (messageContent, contact) -> {
        if(sessionId.equals(contact.getSessionId()))
        {
            messageAdapter.addNewMessage(contactName, messageContent, true);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> messagesRecyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1));
        }
    };

    private byte[] decodeSequence(String sequence)
    {
        return Base64.decode(sequence, Base64.DEFAULT);
    }

    private boolean loadClientCircuit(@NonNull List<Circuit> path, @NonNull AppDatabase databaseInstance)
    {
        NodeDao nodeDao = databaseInstance.nodeDao();
        ContactDao contactDao = databaseInstance.contactDao();

        String previousAddress;
        boolean ok = true;

        for(int i = 0; i < path.size() && ok; i++)
        {
            Circuit item = path.get(i);

            if(i == 0)
            {
                //previousAddress = getSharedPreferences(getString(R.string.shared_preferences), MODE_PRIVATE).getString("guardAddress", "");
                previousAddress = OnionServices.getInstance().getClientGuardAddress();
            } else {
                previousAddress = path.get(i - 1).getAddress();
            }

            if(i == path.size() - 1)
            {
                Contact contact = contactDao.findByAddress(foreignAddress);

                byte[] sessionId = decodeSequence(contact.getSessionId());
                byte[] sessionKey = decodeSequence(contact.getSessionKey());

                ok = OnionServices.getInstance().loadLastNodeInCircuit(foreignAddress,
                        previousAddress, sessionId, sessionKey) >= 0;
            } else {
                ok = OnionServices.getInstance().loadNode(previousAddress,
                        nodeDao.getPublicKey(item.getAddress()), true) != null;
            }
        }

        return ok;
    }

    private void getCircuit(String destination)
    {
        Handler handler = new Handler(Looper.getMainLooper());

        Executors.newSingleThreadExecutor().execute(() -> {
            if(!OnionServices.getInstance().circuitLoaded(foreignAddress))
            {
                AppDatabase databaseInstance = AppDatabase.getInstance(this);

                CircuitDao circuitDao = databaseInstance.circuitDao();
                List<Circuit> route = circuitDao.getRoute(destination);

                if(route.size() == 0)
                {
                    CircuitBuilder circuitBuilder = CircuitBuilder.getInstance();
                    String guardAddress = getSharedPreferences(getString(R.string.shared_preferences),
                            MODE_PRIVATE).getString("guardAddress", null);
                    circuitBuilder.importGraph(databaseInstance);
                    circuitBuilder.buildShortestCircuit(guardAddress);

                    route = circuitBuilder.getShortestPath(destination, databaseInstance);
                }

                final boolean success = loadClientCircuit(route, databaseInstance);
                handler.post(() -> {
                   if(!success)
                   {
                       Toast.makeText(this,
                               "Error while loading circuit to specified address",
                               Toast.LENGTH_SHORT).show();
                   }
                });
            }
        });
    }

    private void populateRecyclerView(List<MessageItem> items)
    {
        if(messageAdapter == null)
        {
            messageAdapter = new MessageAdapter(items);
        }

        messagesRecyclerView.setHasFixedSize(true);
        messagesRecyclerView.setAdapter(messageAdapter);
    }

    private void getMessagesFromDatabase()
    {
        List<MessageItem> messagesList = new ArrayList<>();

        Handler handler = new Handler(Looper.getMainLooper());

        Executors.newSingleThreadExecutor().execute(() -> {
            MessageDao messageDao = AppDatabase.getInstance(this).messageDao();

            final List<Message> messages = messageDao.getConversation(sessionId);

            for(Message message : messages)
            {
                if(message.getSender().equals(foreignAddress))
                {
                    messagesList.add(new MessageItem(contactName, message.getContent(), true));
                } else {
                    messagesList.add(new MessageItem("You", message.getContent(), false));
                }
            }

            handler.post(() -> populateRecyclerView(messagesList));
        });
    }

    private void insertMessageInDatabase(@NonNull String sessionId, @NonNull String content)
    {
        Executors.newSingleThreadExecutor().execute(() -> {
            Message message = new Message("You", sessionId, content);

            AppDatabase databaseInstance = AppDatabase.getInstance(this);
            MessageDao messageDao = databaseInstance.messageDao();

            messageDao.insertAll(message);
        });
    }

    public void onSendButtonClicked(View view)
    {
        String text = messageInputEditText.getText().toString().trim();
        if(text.length() != 0)
        {
            if(OnionServices.getInstance().sendMessage(text, foreignAddress) < 0)
            {
                Toast.makeText(this, "Message could not be delivered", Toast.LENGTH_SHORT).show();
                return;
            }

            messageAdapter.addNewMessage("You", text, false);
            messagesRecyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
            messageInputEditText.setText("");
            insertMessageInDatabase(sessionId, text);
        }
    }
}
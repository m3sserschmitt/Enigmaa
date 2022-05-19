package com.example.enigma;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.enigma.circuit.CircuitBuilder;
import com.example.enigma.database.AppDatabase;
import com.example.enigma.database.Circuit;
import com.example.enigma.database.CircuitDao;
import com.example.enigma.database.Message;
import com.example.enigma.database.MessageDao;
import com.example.enigma.database.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {

    private AppDatabase databaseInstance;
    private MessageAdapter messageAdapter;

    private RecyclerView messagesRecyclerView;

    private String contactName;
    private String foreignAddress;

    private EditText messageInputEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();

        contactName = intent.getStringExtra("name");
        foreignAddress = intent.getStringExtra("address");

        setTitle(contactName);

        databaseInstance = AppDatabase.getInstance(this);

        messagesRecyclerView = findViewById(R.id.messages_recycler_view);
        messageInputEditText = findViewById(R.id.message_edit_text);

        getMessagesFromDatabase();
        buildCircuit(foreignAddress);
    }

    private void buildCircuit(String destination)
    {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
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

                List<Node> path = circuitBuilder.getShortestPath(destination);
                for(Node n: path)
                {
                    Log.i("node:", n.getAddress());
                }
            }

            handler.post(() -> {

            });
        });
    }

    public void onSendButtonClicked(View view)
    {

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
            MessageDao messageDao = databaseInstance.messageDao();

            final List<Message> messages = messageDao.getConversation(foreignAddress);

            for(Message message : messages)
            {
                if(message.getSender().equals(foreignAddress))
                {
                    messagesList.add(new MessageItem(contactName, message.getContent()));
                } else {
                    messagesList.add(new MessageItem("You", message.getContent()));
                }
            }

            handler.post(() -> {
                populateRecyclerView(messagesList);
            });
        });
    }

    private native boolean circuitExists(String destination);
}
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

package com.example.enigma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.enigma.circuit.CircuitBuilder;
import com.example.enigma.communications.MessagingService;
import com.example.enigma.database.AppDatabase;
import com.example.enigma.database.Circuit;
import com.example.enigma.database.CircuitDao;
import com.example.enigma.database.Contact;
import com.example.enigma.database.Message;
import com.example.enigma.database.MessageDao;
import com.example.enigma.database.NodeDao;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {

    private MessageAdapter messageAdapter;
    private RecyclerView messagesRecyclerView;
    private EditText messageInputEditText;

    private Contact targetContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        String sessionId = getIntent().getStringExtra("sessionId");

        messagesRecyclerView = findViewById(R.id.messages_recycler_view);
        LinearLayoutManager layoutManager = (LinearLayoutManager) messagesRecyclerView.getLayoutManager();
        layoutManager.setStackFromEnd(true);

        messageInputEditText = findViewById(R.id.message_edit_text);

        getContactDetailsFromDatabase(sessionId);

        MessagingService.setOnNewMessageListener(onMessageReceivedListener, this.getClass());
        MessagingService.setSessionOnFocus(sessionId);
    }

    private void getContactDetailsFromDatabase(String sessionId)
    {
        Handler handler = new Handler(Looper.getMainLooper());
        Executors.newSingleThreadExecutor().execute(() -> {
            targetContact = AppDatabase.getInstance(this).contactDao().findBySessionId(sessionId);

            handler.post(() -> {
                setTitle(targetContact.getNickName());
                getMessagesFromDatabase();
                setupPath();
            });
        });
    }

    private final MessagingService.onMessageReceivedListener onMessageReceivedListener =
            (messageContent, contact) -> {
        if(targetContact.getSessionId().equals(contact.getSessionId()))
        {
            String currentTargetAddress = targetContact.getAddress();
            String currentTargetGuardAddress = targetContact.getGuardAddress();

            if(!currentTargetAddress.equals(contact.getAddress())
            || !currentTargetGuardAddress.equals(contact.getGuardAddress()))
            {
                targetContact.setGuardAddress(contact.getGuardAddress());
                targetContact.setAddress(contact.getAddress());
                setupPath();
            }

            messageAdapter.addNewMessage(targetContact.getNickName(), messageContent, true);

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> messagesRecyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1));
        }
    };

    private boolean loadPath(@NonNull List<Circuit> path)
    {
        int pathSize = path.size();
        boolean ok = false;

        if(pathSize >= 2)
        {
            AppDatabase databaseInstance = AppDatabase.getInstance(this);

            NodeDao nodeDao = databaseInstance.nodeDao();

            ok = true;

            for(int i = 1; i < pathSize - 1 && ok; i ++) {
                ok = OnionServices.getInstance().loadNode(path.get(i - 1).getAddress(),
                        nodeDao.getPublicKeyPEM(path.get(i).getAddress()), true) != null;
            }

            if(ok) {
                ok = OnionServices.getInstance().loadLastNodeInCircuit(targetContact.getAddress(),
                        path.get(pathSize - 2).getAddress(), targetContact.getDecodedSessionId(),
                        targetContact.getDecodedSessionKey()) >= 0;
            }
        }

        return ok;
    }

    @NonNull
    private List<Circuit> buildPath(@NonNull List<Circuit> path)
    {
        if(path.size() == 0)
        {
            AppDatabase databaseInstance = AppDatabase.getInstance(this);

            LocalAppStorage localAppStorage = new LocalAppStorage(this);
            String guardAddress = localAppStorage.getGuardAddress();

            CircuitBuilder circuitBuilder = CircuitBuilder.getInstance();
            circuitBuilder.importGraph(databaseInstance);
            circuitBuilder.buildShortestCircuit(guardAddress);

            path = circuitBuilder.getShortestPath(targetContact.getAddress(), databaseInstance);
        }

        return path;
    }

    private void setupPath()
    {
        String foreignAddress = targetContact.getAddress();

        if(foreignAddress.equals(OnionServices.getDefaultAddress()))
        {
            return;
        }

        if(!OnionServices.getInstance().circuitLoaded(foreignAddress)) {
            Handler handler = new Handler(Looper.getMainLooper());

            Executors.newSingleThreadExecutor().execute(() -> {

                AppDatabase databaseInstance = AppDatabase.getInstance(this);

                CircuitDao circuitDao = databaseInstance.circuitDao();
                List<Circuit> path = circuitDao.getRoute(foreignAddress);
                Collections.reverse(path);

                path = buildPath(path);
                final boolean success = loadPath(path);

                handler.post(() -> {
                    if (!success) {
                        Toast.makeText(this,
                                "Error while loading circuit to specified address",
                                Toast.LENGTH_SHORT).show();
                    }
                });

            });
        }
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

            final List<Message> messages = messageDao.getConversation(targetContact.getSessionId());

            for(Message message : messages)
            {
                if(message.getSender().equals(targetContact.getAddress()))
                {
                    messagesList.add(new MessageItem(targetContact.getNickName(),
                            message.getContent(), true));
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

    private static class MessageBuilder
    {
        JSONObject messageContent;

        public MessageBuilder(LocalAppStorage localAppStorage, String content)
        {
            messageContent = new JSONObject();
            try {
                messageContent.put("address", localAppStorage.getLocalAddress());
                messageContent.put("guardAddress", localAppStorage.getGuardAddress());
                messageContent.put("message", content);
            } catch (Exception e)
            {
                messageContent = null;
            }
        }

        @NonNull
        @Override
        public String toString()
        {
            return messageContent.toString();
        }
    }

    @NonNull
    private String buildJsonMessage(String messageContent)
    {
        MessageBuilder messageBuilder = new MessageBuilder(
                new LocalAppStorage(this), messageContent);

        return messageBuilder.toString();
    }

    public void onSendButtonClicked(View view)
    {
        String text = messageInputEditText.getText().toString().trim();
        if(text.length() != 0)
        {
            if(OnionServices.getInstance().sendMessage(buildJsonMessage(text), targetContact.getAddress()) < 0)
            {
                Toast.makeText(this, "Message could not be delivered", Toast.LENGTH_SHORT).show();
                return;
            }

            messageAdapter.addNewMessage("You", text, false);
            messagesRecyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
            messageInputEditText.setText("");
            insertMessageInDatabase(targetContact.getSessionId(), text);
        }
    }
}
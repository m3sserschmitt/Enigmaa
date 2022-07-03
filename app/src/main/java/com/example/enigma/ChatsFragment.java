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

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.enigma.communications.MessagingService;
import com.example.enigma.database.AppDatabase;
import com.example.enigma.database.Contact;
import com.example.enigma.database.ContactDao;
import com.example.enigma.database.Message;
import com.example.enigma.database.MessageDao;
import com.example.enigma.databinding.FragmentChatsBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class ChatsFragment extends Fragment {

    private FragmentChatsBinding binding;
    private ContactAdapter contactAdapter;

    private MessagingService.onMessageReceivedListener onMessageReceivedListener;

    public ChatsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatsBinding.inflate(inflater, container, false);

        contactAdapter = new ContactAdapter(requireContext());
        onMessageReceivedListener = (messageContent, contact) ->
                contactAdapter.updateItemAdditionalInfo(messageContent, contact);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        getSessionsFromDatabase();
        MessagingService.setOnNewMessageListener(onMessageReceivedListener, this.getClass());
        MessagingService.setAllSessionsOnFocus();
    }

    private void populateRecyclerView(List<ContactItem> items)
    {
        contactAdapter.setItems(items);
        binding.chatsRecyclerView.setHasFixedSize(true);
        binding.chatsRecyclerView.setAdapter(contactAdapter);
    }

    private void getSessionsFromDatabase()
    {
        Handler handler = new Handler(Looper.getMainLooper());
        List<ContactItem> items = new ArrayList<>();

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase databaseInstance = AppDatabase.getInstance(requireActivity());
            MessageDao messageDao = databaseInstance.messageDao();
            ContactDao contactDao = databaseInstance.contactDao();

            final List<String> sessions = messageDao.getConversations();
            final List<Contact> contacts = contactDao.getContacts(sessions);

            for(Contact contact : contacts)
            {
                Message lastMessage = messageDao.getLastMessage(contact.getSessionId());
                String address = contact.getAddress();

                String content = lastMessage.getContent();
                if(!lastMessage.getSender().equals(address))
                {
                    content = "You: " + content;
                }

                items.add(new ContactItem(contact.getNickName(), address,
                        content, contact.getSessionId()));
            }

            handler.post(() -> populateRecyclerView(items));
        });
    }
}
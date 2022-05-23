package com.example.enigma;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.enigma.database.AppDatabase;
import com.example.enigma.database.Contact;
import com.example.enigma.database.ContactDao;
import com.example.enigma.database.MessageDao;
import com.example.enigma.databinding.FragmentChatsBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class ChatsFragment extends Fragment {

    private FragmentChatsBinding binding;
    private ContactAdapter contactAdapter;

    public ChatsFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatsBinding.inflate(inflater, container, false);

        getSessionsFromDatabase();

        return binding.getRoot();
    }

    private void populateRecyclerView(List<ContactItem> items)
    {
        if(contactAdapter == null)
        {
            contactAdapter = new ContactAdapter(requireContext(), items);
        }

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
                items.add(new ContactItem(contact.getAddress(), contact.getNickName(),
                        contact.getSessionId()));
            }

            handler.post(() -> populateRecyclerView(items));
        });
    }
}
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
import com.example.enigma.databinding.FragmentContactsBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class ContactsFragment extends Fragment {

    private ContactAdapter contactAdapter;

    private FragmentContactsBinding binding;

    private MessagingService.onMessageReceivedListener onMessageReceivedListener;

    public ContactsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentContactsBinding.inflate(inflater, container, false);
        onMessageReceivedListener = (content, contact) ->
                contactAdapter.updateItemAdditionalInfo(contact.getAddress(), contact);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        getContactsFromDatabase();
        MessagingService.setOnNewMessageListener(onMessageReceivedListener, this.getClass());
        MessagingService.setNoSessionOnFocus();
    }

    private void populateRecyclerView(List<ContactItem> items)
    {
        contactAdapter = new ContactAdapter(requireContext(), items);
        binding.contactsRecyclerView.setHasFixedSize(true);
        binding.contactsRecyclerView.setAdapter(contactAdapter);
    }

    private void getContactsFromDatabase()
    {
        List<ContactItem> contactsList = new ArrayList<>();

        Handler handler = new Handler(Looper.getMainLooper());

        Executors.newSingleThreadExecutor().execute(() -> {
            ContactDao contactDao = AppDatabase.getInstance(requireContext()).contactDao();
            final List<Contact> contacts = contactDao.getAll();

            for(Contact contact : contacts)
            {
                String contactAddress = contact.getAddress();
                String additionalInfo = contactAddress;

                if(contactAddress.equals(OnionServices.getDefaultAddress()))
                {
                    additionalInfo = "Pending confirmation";
                }

                contactsList.add(new ContactItem(contact.getNickName(), contact.getAddress(),
                        additionalInfo, contact.getSessionId()));
            }

            handler.post(() -> populateRecyclerView(contactsList));
        });
    }
}
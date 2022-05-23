package com.example.enigma;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ContactAdapter
        extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder>
        implements Filterable
{
    private final Context context;

    private final List<ContactItem> contactsList;
    private final List<ContactItem> contactsListFull;

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView addressTextView;

        private String tag;

        public ContactViewHolder(Context context, @NonNull View itemView)
        {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.contact_name_text_view);
            addressTextView = itemView.findViewById(R.id.contact_address_text_view);

            itemView.setOnClickListener(v -> {
                Intent chatActivityIntent = new Intent(context, ChatActivity.class);
                chatActivityIntent.putExtra("address", addressTextView.getText().toString());
                chatActivityIntent.putExtra("name", nameTextView.getText().toString());
                chatActivityIntent.putExtra("sessionId", tag);

                context.startActivity(chatActivityIntent);
            });
        }

        public void setTag(String tag)
        {
            this.tag = tag;
        }
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact, parent,
                false);

        return new ContactViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        ContactItem currentItem = contactsList.get(position);
        holder.nameTextView.setText(currentItem.getNickName());
        holder.addressTextView.setText(currentItem.getAddress());
        holder.tag = currentItem.getSessionId();
    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }

    ContactAdapter(Context context, List<ContactItem> contacts)
    {
        this.context = context;

        contactsList = contacts;
        contactsListFull = new ArrayList<>(contacts);
    }

    private final Filter contactsFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ContactItem> filteredContacts = new ArrayList<>();

            if(constraint == null || constraint.length() == 0)
            {
                filteredContacts.addAll(contactsListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase(Locale.ROOT).trim();

                for(ContactItem item : contactsListFull)
                {
                    if(item.getNickName().toLowerCase(Locale.ROOT).contains(filterPattern))
                    {
                        filteredContacts.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredContacts;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            contactsList.clear();
            contactsList.addAll((ArrayList)results.values);
            notifyDataSetChanged();
        }
    };

    @Override
    public Filter getFilter() { return contactsFilter; }
}

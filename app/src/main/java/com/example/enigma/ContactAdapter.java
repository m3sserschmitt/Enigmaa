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

import com.example.enigma.database.Contact;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ContactAdapter
        extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder>
        implements Filterable
{
    private final Context context;

    private List<ContactItem> contactsList;
    private List<ContactItem> contactsListFull;

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView additionalInfoTextView;

        private String address;
        private String name;
        private String sessionId;

        public ContactViewHolder(Context context, @NonNull View itemView)
        {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.contact_name_text_view);
            additionalInfoTextView = itemView.findViewById(R.id.contact_additional_info_text_view);

            itemView.setOnClickListener(v -> {
                Intent chatActivityIntent = new Intent(context, ChatActivity.class);

//                chatActivityIntent.putExtra("address", address);
//                chatActivityIntent.putExtra("name", name);
                chatActivityIntent.putExtra("sessionId", sessionId);

                context.startActivity(chatActivityIntent);
            });
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

        holder.nameTextView.setText(currentItem.getName());
        holder.additionalInfoTextView.setText(currentItem.getAdditionalInfo());

        holder.sessionId = currentItem.getSessionId();
        holder.address = currentItem.getAddress();
        holder.name = currentItem.getName();
    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }

    public ContactAdapter(Context context, List<ContactItem> contacts)
    {
        this.context = context;

        contactsList = contacts;
        contactsListFull = new ArrayList<>(contacts);
    }

    public ContactAdapter(Context context)
    {
        this.context = context;

        contactsList = new ArrayList<>();
        contactsListFull = new ArrayList<>();
    }

    public void setItems(List<ContactItem> items)
    {
        contactsList = items;
        contactsListFull = new ArrayList<>(items);
    }

    public void updateItemAdditionalInfo(String content, @NonNull Contact contact)
    {
        String sessionId = contact.getSessionId();
        boolean updated = false;

        for(int i = 0; i < contactsList.size(); i++)
        {
            if(contactsList.get(i).getSessionId().equals(sessionId))
            {
                ContactItem item = contactsList.get(i);
                contactsList.remove(i);
                notifyItemRemoved(i);

                item.setAdditionalInfo(content);
                contactsList.add(0, item);
                notifyItemInserted(0);
                updated = true;
                break;
            }
        }

        if(!updated)
        {
            contactsList.add(0, new ContactItem(contact.getNickName(), contact.getAddress(), content,
                    contact.getSessionId()));
            notifyItemInserted(0);
        }
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
                    if(item.getName().toLowerCase(Locale.ROOT).contains(filterPattern))
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

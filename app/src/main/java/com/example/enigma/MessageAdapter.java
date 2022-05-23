package com.example.enigma;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageAdapter
extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private final List<MessageItem> messages;

    public static class MessageViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView senderTextView;
        private final TextView contentTextView;

        public MessageViewHolder(@NonNull View itemView)
        {
            super(itemView);

            senderTextView = itemView.findViewById(R.id.message_sender_text_view);
            contentTextView = itemView.findViewById(R.id.message_content_text_view);
        }
    }

    MessageAdapter(List<MessageItem> messages)
    {
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message, parent,
                false);

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        MessageItem currentItem = messages.get(position);
        holder.contentTextView.setText(currentItem.getContent());
        holder.senderTextView.setText(currentItem.getName());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void addNewMessage(String name, String content)
    {
        MessageItem newItem = new MessageItem(name, content);
        messages.add(newItem);

        notifyDataSetChanged();
    }
}

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
        String name = currentItem.getName();

        if(!currentItem.isForeign())
        {
            holder.senderTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            holder.contentTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        }

        holder.senderTextView.setText(name);
        holder.contentTextView.setText(currentItem.getContent());
    }

    @Override
    public void onViewRecycled(@NonNull MessageViewHolder holder)
    {
        holder.senderTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        holder.contentTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void addNewMessage(String name, String content, boolean isForeign)
    {
        MessageItem newItem = new MessageItem(name, content, isForeign);
        messages.add(newItem);

        notifyItemInserted(messages.size() - 1);
    }
}

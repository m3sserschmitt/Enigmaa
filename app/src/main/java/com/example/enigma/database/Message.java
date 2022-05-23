package com.example.enigma.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "messages")
public class Message {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @Nullable
    private String sender;

    @NonNull
    private String sessionId;

    @NonNull
    private String content;

    private long timestamp;

    public Message(@Nullable String sender, @NonNull String sessionId, @NonNull String content)
    {
        this.sender = sender;
        this.sessionId = sessionId;
        this.content = content;
        this.timestamp = new Date().getTime();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setContent(@NonNull String content) {
        this.content = content;
    }

    public void setSessionId(@NonNull String destination) {
        this.sessionId = destination;
    }

    public void setSender(@Nullable String from) {
        this.sender = from;
    }

    public long getId() {
        return id;
    }

    @NonNull
    public String getContent() {
        return content;
    }

    @NonNull
    public String getSessionId() {
        return sessionId;
    }

    @Nullable
    public String getSender() {
        return sender;
    }

    public void setId(long id) {
        this.id = id;
    }
}

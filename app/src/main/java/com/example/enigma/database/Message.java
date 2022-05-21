package com.example.enigma.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "messages")
public class Message {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private String sender = "";

    @NonNull
    private String destination = "";

    private String content;

    private long timestamp;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDestination(@NonNull String destination) {
        this.destination = destination;
    }

    public void setSender(@NonNull String from) {
        this.sender = from;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    @NonNull
    public String getDestination() {
        return destination;
    }

    @NonNull
    public String getSender() {
        return sender;
    }

    public void setId(long id) {
        this.id = id;
    }
}

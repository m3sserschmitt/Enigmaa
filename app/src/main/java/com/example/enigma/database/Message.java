package com.example.enigma.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "messages", primaryKeys = {"id"})
public class Message {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private String from = "";

    @NonNull
    private String destination = "";

    private String content;

    public void setContent(String content) {
        this.content = content;
    }

    public void setDestination(@NonNull String destination) {
        this.destination = destination;
    }

    public void setFrom(@NonNull String from) {
        this.from = from;
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
    public String getFrom() {
        return from;
    }

    public void setId(int id) {
        this.id = id;
    }
}

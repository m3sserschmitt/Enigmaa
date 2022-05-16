package com.example.enigma.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "circuits")
public class Circuit {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private String destination = "";

    @NonNull
    private String address = "";

    private int index;

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getDestination() {
        return destination;
    }

    public void setDestination(@NonNull String destination) {
        this.destination = destination;
    }

    public void setAddress(@NonNull String address) {
        this.address = address;
    }

    public int getIndex() {
        return index;
    }

    public long getId() {
        return id;
    }

    @NonNull
    public String getAddress() {
        return address;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}

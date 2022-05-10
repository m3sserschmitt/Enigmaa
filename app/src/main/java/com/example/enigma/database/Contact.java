package com.example.enigma.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(tableName = "contacts", primaryKeys = {"address"})
public class Contact {

    @NonNull
    private String address = "";

    @NonNull
    private String session = "";

    @NonNull
    private String sessionKey = "";

    @NonNull
    public String getAddress()
    {
        return address;
    }

    @NonNull
    public String getSession() {
        return session;
    }

    @NonNull
    public String getSessionKey() {
        return sessionKey;
    }

    public void setAddress(@NonNull String address) {
        this.address = address;
    }

    public void setSession(@NonNull String session) {
        this.session = session;
    }

    public void setSessionKey(@NonNull String sessionKey) {
        this.sessionKey = sessionKey;
    }
}

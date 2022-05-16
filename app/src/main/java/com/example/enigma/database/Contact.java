package com.example.enigma.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(tableName = "contacts", primaryKeys = {"address"})
public class Contact {

    @NonNull private String address = "";

    @NonNull private String sessionId = "";

    @NonNull private String sessionKey = "";

    @NonNull String guardAddress = "";

    @NonNull String nickName = "";

    @NonNull public String getAddress()
    {
        return address;
    }

    @NonNull public String getSessionId() {
        return sessionId;
    }

    @NonNull public String getSessionKey() {
        return sessionKey;
    }

    public void setGuardAddress(@NonNull String guardAddress) {
        this.guardAddress = guardAddress;
    }

    @NonNull public String getGuardAddress() {
        return guardAddress;
    }

    public void setAddress(@NonNull String address) {
        this.address = address;
    }

    public void setSessionId(@NonNull String session) {
        this.sessionId = session;
    }

    public void setSessionKey(@NonNull String sessionKey) {
        this.sessionKey = sessionKey;
    }

    @NonNull public String getNickName() {
        return nickName;
    }

    public void setNickName(@NonNull String nickName) {
        this.nickName = nickName;
    }
}

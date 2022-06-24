package com.example.enigma.database;

import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.enigma.OnionServices;

@Entity(tableName = "contacts")
public class Contact {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private String sessionId;

    @NonNull
    private String address;

    @NonNull
    private String sessionKey;

    @NonNull
    String guardAddress;

    @NonNull
    String nickName;

    public Contact(@NonNull String address,
                   @NonNull String sessionId,
                   @NonNull String sessionKey,
                   @NonNull String guardAddress,
                   @NonNull String nickName)
    {
        this.address = address;
        this.sessionId = sessionId;
        this.sessionKey = sessionKey;
        this.guardAddress = guardAddress;
        this.nickName = nickName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getAddress()
    {
        return address;
    }

    @NonNull
    public String getSessionId() {
        return sessionId;
    }

    @NonNull
    public String getSessionKey() {
        return sessionKey;
    }

    public void setGuardAddress(@NonNull String guardAddress) {
        this.guardAddress = guardAddress;
    }

    @NonNull
    public String getGuardAddress() {
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

    @NonNull
    public String getNickName() {
        return nickName;
    }

    public void setNickName(@NonNull String nickName) {
        this.nickName = nickName;
    }

    @NonNull
    public byte[] getDecodedSessionId()
    {
        return Base64.decode(sessionId, Base64.DEFAULT);
    }

    @NonNull
    public byte[] getDecodedSessionKey()
    {
        return Base64.decode(sessionKey, Base64.DEFAULT);
    }
}

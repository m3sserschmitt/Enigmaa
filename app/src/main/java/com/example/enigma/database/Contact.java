package com.example.enigma.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "contacts")
public class Contact {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull private String sessionId;

    @Nullable
    private String address;

    @NonNull private String sessionKey;

    @Nullable String guardAddress;

    @NonNull String nickName;

    public Contact(@Nullable String address,
                   @NonNull String sessionId,
                   @NonNull String sessionKey,
                   @Nullable String guardAddress,
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

    @Nullable
    public String getAddress()
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

    @Nullable
    public String getGuardAddress() {
        return guardAddress;
    }

    public void setAddress(@Nullable String address) {
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
}

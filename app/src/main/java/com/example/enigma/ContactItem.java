package com.example.enigma;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ContactItem {

    @NonNull
    private String name;

    @NonNull
    private String address;

    @Nullable
    private String additionalInfo;

    @NonNull
    private String sessionId;

    public ContactItem(@NonNull String name, @NonNull String address,
                       @Nullable String additionalInfo,
                       @NonNull String sessionId)
    {
        this.name = name;
        this.address = address;
        this.additionalInfo = additionalInfo;
        this.sessionId = sessionId;
    }

    @NonNull
    public String getAddress() {
        return address;
    }

    public void setAddress(@NonNull String address) {
        this.address = address;
    }

    @NonNull
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(@NonNull String sessionId) {
        this.sessionId = sessionId;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @Nullable
    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(@Nullable String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
}

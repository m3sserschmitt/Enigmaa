package com.example.enigma.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "nodes")
public class Node {

    @PrimaryKey
    @NonNull
    private String address = "";

    private String ip;

    private String publicKey;

    @NonNull
    public String getAddress() {
        return address;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getIp()
    {
        return ip;
    }

    public void setAddress(@NonNull String address) {
        this.address = address;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}

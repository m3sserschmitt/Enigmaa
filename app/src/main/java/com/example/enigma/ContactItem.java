package com.example.enigma;

public class ContactItem {

    private String address;

    private String nickName;

    private String sessionId;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public ContactItem(String address, String name, String sessionId)
    {
        this.address = address;
        this.nickName = name;
        this.sessionId = sessionId;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getNickName() {
        return nickName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

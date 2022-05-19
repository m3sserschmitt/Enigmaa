package com.example.enigma;

public class ContactItem {
    private String address;

    private String nickName;

    public ContactItem(String address, String name)
    {
        this.address = address;
        this.nickName = name;
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

package com.example.enigma;

public class MessageItem {
    private String name;

    private String content;

    private final boolean foreign;

    public MessageItem(String name, String content, boolean foreign) {
        this.name = name;
        this.content = content;
        this.foreign = foreign;
    }

    public boolean isForeign() {
        return foreign;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

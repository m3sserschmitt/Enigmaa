package com.example.enigma.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(tableName = "edges", primaryKeys = {"origin", "secondNode"})
public class Edge {

    @NonNull
    private String origin = "";

    @NonNull
    private String secondNode = "";

    @NonNull
    public String getOrigin() {
        return origin;
    }

    @NonNull
    public String getSecondNode()
    {
        return secondNode;
    }

    public void setOrigin(@NonNull String origin) {
        this.origin = origin;
    }

    public void setSecondNode(@NonNull String secondNode) {
        this.secondNode = secondNode;
    }
}

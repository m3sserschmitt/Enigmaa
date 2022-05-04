package com.example.enigma.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(tableName = "edges", primaryKeys = {"source", "target"})
public class Edge {

    @NonNull
    private String source = "";

    @NonNull
    private String target = "";

    @NonNull
    public String getSource() {
        return source;
    }

    @NonNull
    public String getTarget()
    {
        return target;
    }

    public void setSource(@NonNull String source) {
        this.source = source;
    }

    public void setTarget(@NonNull String target) {
        this.target = target;
    }
}

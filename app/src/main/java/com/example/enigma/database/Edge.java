package com.example.enigma.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(tableName = "edges", primaryKeys = {"source", "target"})
public class Edge {

    @NonNull
    private String source = "";

    @NonNull
    private String target = "";

    @Ignore
    private Node targetNode;

    @Ignore
    private int weight = 1;

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

    public int getWeight()
    {
        return this.weight;
    }

    public void setWeight(int weight)
    {
        this.weight = weight;
    }

    public Node getTargetNode() {
        return targetNode;
    }

    public void setTargetNode(Node targetNode) {
        this.targetNode = targetNode;
    }
}

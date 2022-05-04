package com.example.enigma.database;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class NodeWithNeighbor {
    @Embedded
    protected Node node;

    @Relation(
            parentColumn = "address",
            entityColumn = "source"
    )

    protected List<Edge> edges;

    public List<Edge> getEdges() {
        return edges;
    }
}

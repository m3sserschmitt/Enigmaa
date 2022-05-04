package com.example.enigma.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface NodeDao {

    @Query("SELECT * FROM nodes")
    List<Node> getAll();

    @Query("SELECT * FROM nodes WHERE address = :address")
    Node findByAddress(String address);

    @Insert
    void insertAll(Node... nodes);

    @Delete
    void delete(Node node);

    @Transaction
    @Query("SELECT * FROM nodes")
    List<NodeWithNeighbor> getNodesWithNeighbors();

    @Transaction
    @Query("SELECT * FROM nodes WHERE address = :origin")
    NodeWithNeighbor getNeighbors(String origin);

    @Query("DELETE FROM nodes")
    void clear();
}

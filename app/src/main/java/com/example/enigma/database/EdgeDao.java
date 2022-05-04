package com.example.enigma.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface EdgeDao {

    @Query("SELECT * from edges")
    List<Edge> getAll();

    @Insert
    void insertAll(Edge... edges);

    @Delete
    void delete(Edge edge);

    @Query("DELETE FROM edges")
    void clear();
}

package com.example.enigma.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface CircuitDao {

    @Query("SELECT * FROM circuits")
    List<Circuit> getAll();

    @Query("SELECT * FROM circuits WHERE destination=:destination ORDER BY 'index'")
    List<Circuit> getRoute(String destination);

    @Insert
    void insertAll(Circuit... circuits);

    @Delete
    void delete(Circuit circuit);

    @Query("DELETE FROM circuits")
    void clear();
}

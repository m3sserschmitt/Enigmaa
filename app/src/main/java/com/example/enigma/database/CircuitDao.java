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

    @Insert
    void insertAll(Contact... contacts);

    @Delete
    void delete(Contact contact);

    @Query("DELETE FROM circuits")
    void clear();
}

package com.example.enigma.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface ContactDao {
    @Query("SELECT * FROM contacts")
    List<Contact> getAll();

    @Insert
    void insertAll(Contact... contacts);

    @Delete
    void delete(Contact contacts);

    @Query("DELETE FROM contacts")
    void clear();

    @Transaction
    @Query("SELECT * FROM contacts WHERE address = :address")
    ContactWithCircuits getCircuitForContact(String address);
}

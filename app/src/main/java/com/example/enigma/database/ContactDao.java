package com.example.enigma.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ContactDao {
    @Query("SELECT * FROM contacts")
    List<Contact> getAll();

    @Query("SELECT * FROM contacts WHERE address=:address")
    Contact findByAddress(String address);

    @Query("SELECT * FROM contacts WHERE sessionId=:sessionId")
    Contact findBySessionId(String sessionId);

    @Query("SELECT * FROM contacts WHERE sessionId IN (:sessions)")
    List<Contact> getContacts(List<String> sessions);

    @Update
    void update(Contact contact);

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

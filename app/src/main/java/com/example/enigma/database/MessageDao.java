package com.example.enigma.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MessageDao {

    @Query("SELECT * FROM messages")
    List<Message> getAll();

    @Query("SELECT * FROM messages WHERE sender=:address OR destination=:address")
    List<Message> getConversation(String address);

    @Insert
    void insertAll(Message... messages);


}

package com.example.enigma.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MessageDao {

    @Query("SELECT * FROM messages")
    List<Message> getAll();

    @Query("SELECT * FROM messages WHERE sessionId=:sessionId")
    List<Message> getConversation(String sessionId);

    @Query("SELECT DISTINCT sessionId FROM messages")
    List<String> getConversations();

    @Insert
    void insertAll(Message... messages);
}


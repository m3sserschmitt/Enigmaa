/*  Enigma - Onion Routing based messaging app.
    Copyright (C) 2022  Romulus-Emanuel Ruja <romulus-emanuel.ruja@tutanota.com>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

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

    @Query("SELECT * FROM messages WHERE sessionId=:sessionId ORDER BY ROWID DESC LIMIT 1")
    Message getLastMessage(String sessionId);

    @Insert
    void insertAll(Message... messages);
}


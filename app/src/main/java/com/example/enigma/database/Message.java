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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "messages")
public class Message {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private String sender;

    @NonNull
    private String sessionId;

    @NonNull
    private String content;

    private long timestamp;

    public Message(@NonNull String sender, @NonNull String sessionId, @NonNull String content)
    {
        this.sender = sender;
        this.sessionId = sessionId;
        this.content = content;
        this.timestamp = new Date().getTime();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setContent(@NonNull String content) {
        this.content = content;
    }

    public void setSessionId(@NonNull String destination) {
        this.sessionId = destination;
    }

    public void setSender(@NonNull String from) {
        this.sender = from;
    }

    public long getId() {
        return id;
    }

    @NonNull
    public String getContent() {
        return content;
    }

    @NonNull
    public String getSessionId() {
        return sessionId;
    }

    @NonNull
    public String getSender() {
        return sender;
    }

    public void setId(long id) {
        this.id = id;
    }
}

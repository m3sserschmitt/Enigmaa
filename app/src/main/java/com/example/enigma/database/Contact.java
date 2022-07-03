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

import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.enigma.OnionServices;

@Entity(tableName = "contacts")
public class Contact {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private String sessionId;

    @NonNull
    private String address;

    @NonNull
    private String sessionKey;

    @NonNull
    String guardAddress;

    @NonNull
    String nickName;

    public Contact(@NonNull String address,
                   @NonNull String sessionId,
                   @NonNull String sessionKey,
                   @NonNull String guardAddress,
                   @NonNull String nickName)
    {
        this.address = address;
        this.sessionId = sessionId;
        this.sessionKey = sessionKey;
        this.guardAddress = guardAddress;
        this.nickName = nickName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getAddress()
    {
        return address;
    }

    @NonNull
    public String getSessionId() {
        return sessionId;
    }

    @NonNull
    public String getSessionKey() {
        return sessionKey;
    }

    public void setGuardAddress(@NonNull String guardAddress) {
        this.guardAddress = guardAddress;
    }

    @NonNull
    public String getGuardAddress() {
        return guardAddress;
    }

    public void setAddress(@NonNull String address) {
        this.address = address;
    }

    public void setSessionId(@NonNull String session) {
        this.sessionId = session;
    }

    public void setSessionKey(@NonNull String sessionKey) {
        this.sessionKey = sessionKey;
    }

    @NonNull
    public String getNickName() {
        return nickName;
    }

    public void setNickName(@NonNull String nickName) {
        this.nickName = nickName;
    }

    @NonNull
    public byte[] getDecodedSessionId()
    {
        return Base64.decode(sessionId, Base64.DEFAULT);
    }

    @NonNull
    public byte[] getDecodedSessionKey()
    {
        return Base64.decode(sessionKey, Base64.DEFAULT);
    }
}

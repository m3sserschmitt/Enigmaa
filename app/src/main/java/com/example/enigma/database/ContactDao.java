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

    @Query("SELECT * FROM contacts WHERE sessionId IN (:sessions) AND address NOT NULL")
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

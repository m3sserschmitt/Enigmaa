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
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "circuits")
public class Circuit {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private String destination = "";

    @NonNull
    private String address = "";

    private int index;

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getDestination() {
        return destination;
    }

    public void setDestination(@NonNull String destination) {
        this.destination = destination;
    }

    public void setAddress(@NonNull String address) {
        this.address = address;
    }

    public int getIndex() {
        return index;
    }

    public long getId() {
        return id;
    }

    @NonNull
    public String getAddress() {
        return address;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}

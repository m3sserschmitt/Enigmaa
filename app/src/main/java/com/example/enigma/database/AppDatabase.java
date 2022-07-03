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

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Node.class, Edge.class, Contact.class, Circuit.class, Message.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static final String databaseName = "enigma.db";
    private static volatile AppDatabase instance;

    @NonNull
    private static AppDatabase create(final Context context)
    {
        return Room.databaseBuilder(context, AppDatabase.class, databaseName).build();
    }

    public static synchronized AppDatabase getInstance(Context context)
    {
        if(instance == null)
        {
            instance = create(context);
        }
        return instance;
    }

    protected AppDatabase() { }

    public abstract NodeDao nodeDao();

    public abstract EdgeDao edgeDao();

    public abstract ContactDao contactDao();

    public abstract CircuitDao circuitDao();

    public abstract MessageDao messageDao();
}

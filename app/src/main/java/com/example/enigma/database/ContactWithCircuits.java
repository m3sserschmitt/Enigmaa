package com.example.enigma.database;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class ContactWithCircuits {
    @Embedded protected Contact contact;

    @Relation(
            parentColumn = "address",
            entityColumn = "destination"
    )
    public List<Circuit> circuits;
}

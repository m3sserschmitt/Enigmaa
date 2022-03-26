package com.example.enigma;

import android.content.Context;

import java.io.FileInputStream;

public class NetworkGraphParser {

    public boolean parse(String jsonGraph)
    {

        return true;
    }

    public boolean parseFile(Context context, String filePath)
    {
        StringBuilder data = new StringBuilder();

        try {
             FileInputStream inputStream = context.openFileInput(filePath);

             int readData = inputStream.read();

             while(readData != -1)
             {
                 data.append((char)readData);

                 readData = inputStream.read();
             }

        } catch (Exception e)
        {
            return false;
        }

        return this.parse(data.toString());
    }
}

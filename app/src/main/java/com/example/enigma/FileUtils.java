package com.example.enigma;

import android.content.Context;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class FileUtils {
    private final File filesDir;
    private static FileUtils instance;

    private FileUtils(Context context)
    {
        filesDir = context.getFilesDir();
    }

    public static FileUtils getInstance(Context context)
    {
        if(instance == null)
        {
            instance = new FileUtils(context);
        }

        return instance;
    }

    @Nullable
    public String readFile(String file)
    {
        File filePath = new File(filesDir, file);

        StringBuilder content = new StringBuilder();

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
            String line;

            while((line = bufferedReader.readLine()) != null)
            {
                content.append(line);
                content.append("\n");
            }

            bufferedReader.close();

        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }

        return content.toString();
    }
}

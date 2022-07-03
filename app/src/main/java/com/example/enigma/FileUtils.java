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

package com.example.enigma;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class FileUtils {
    private final File filesDir;
    private static FileUtils instance;

    private FileUtils(@NonNull Context context)
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

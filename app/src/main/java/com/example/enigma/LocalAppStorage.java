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
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.Contract;

public class LocalAppStorage {

    private final SharedPreferences sharedPreferences;

    public LocalAppStorage(@NonNull Context context)
    {
        sharedPreferences = context.getSharedPreferences("com.example.enigma",
                Context.MODE_PRIVATE);
    }

    @NonNull
    public static String getDefaultPublicKeyFile(@NonNull Context context)
    {
        return context.getFilesDir().toString() + "/public.pem";
    }

    @NonNull
    public static String getDefaultPrivateKeyFile(@NonNull Context context)
    {
        return context.getFilesDir().toString() + "/private.pem";
    }

    @NonNull
    @Contract(pure = true)
    public static String getDefaultPublicKeyFileName()
    {
        return "public.pem";
    }

    @NonNull
    @Contract(pure = true)
    public static String getDefaultPrivateKeyFileName()
    {
        return "private.pem";
    }

    public void setStringValue(@NonNull String key, @NonNull String value)
    {
        sharedPreferences.edit().putString(key, value).apply();
    }

    @Nullable
    public String getStringValue(@NonNull String key)
    {
        return sharedPreferences.getString(key, null);
    }

    public void setPublicKeyPath(String publicKeyPath)
    {
        setStringValue("publicKeyPath", publicKeyPath);
    }

    @Nullable
    public String getPublicKeyPath()
    {
        return getStringValue("publicKeyPath");
    }

    public void setPrivateKeyPath(String privateKeyPath)
    {
        setStringValue("privateKeyPath", privateKeyPath);
    }

    @Nullable
    public String getPrivateKeyPath()
    {
        return getStringValue("privateKeyPath");
    }

    public void setLocalAddress(String localAddress)
    {
        setStringValue("localAddress", localAddress);
    }

    @Nullable
    public String getLocalAddress()
    {
        return getStringValue("localAddress");
    }

    public void setGuardAddress(String guardAddress)
    {
        setStringValue("guardAddress", guardAddress);
    }

    @Nullable
    public String getGuardAddress()
    {
        return getStringValue("guardAddress");
    }

    public void setGuardHostname(String guardHostname)
    {
        setStringValue("guardHostname", guardHostname);
    }

    @Nullable
    public String getGuardHostname()
    {
        return getStringValue("guardHostname");
    }

    public void setDirectoryPortNumber(String directoryPortNumber)
    {
        setStringValue("directoryPortNumber", directoryPortNumber);
    }

    @Nullable
    public String getDirectoryPortNumber()
    {
        return getStringValue("directoryPortNumber");
    }

    public void setOnionServicePortNumber(@NonNull String onionServicePortNumber)
    {
        setStringValue("onionServicePortNumber", onionServicePortNumber);
    }

    @Nullable
    public String getOnionServicePortNumber()
    {
        return getStringValue("onionServicePortNumber");
    }

    public void setGuardPublicKeyPEM(@NonNull String guardPublicKeyPEM)
    {
        setStringValue("guardPublicKeyPEM", guardPublicKeyPEM);
    }

    @Nullable
    public String getGuardPublicKeyPEM()
    {
        return getStringValue("guardPublicKeyPEM");
    }
}

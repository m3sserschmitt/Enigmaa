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

    public void setPublicKeyPath(String publicKeyPath)
    {
        sharedPreferences.edit().putString("publicKey", publicKeyPath).apply();
    }

    public void setPrivateKeyPath(String privateKeyPath)
    {
        sharedPreferences.edit().putString("privateKey", privateKeyPath).apply();
    }

    public void setLocalAddress(String localAddress)
    {
        sharedPreferences.edit().putString("localAddress", localAddress).apply();
    }

    @Nullable
    public String getLocalAddress()
    {
        return sharedPreferences.getString("localAddress", null);
    }

    public void setGuardAddress(String guardAddress)
    {
        sharedPreferences.edit().putString("guardAddress", guardAddress).apply();
    }

    @Nullable
    public String getGuardAddress()
    {
        return sharedPreferences.getString("guardAddress", null);
    }
}

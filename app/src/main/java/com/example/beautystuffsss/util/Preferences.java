package com.example.beautystuffsss.util;


import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
    SharedPreferences preferences;

    public Preferences(Context context) {
        preferences = context.getSharedPreferences(Constants.prefName, Context.MODE_PRIVATE);
    }

    public void saveString(String key, String value) {
        preferences.edit().putString(key, value).apply();
    }

    public String getString(String key) {
        return preferences.getString(key, Constants.defaultString);
    }

    public void saveFloat(String key, float value) {
        preferences.edit().putFloat(key, value).apply();
    }

    public float getFloat(String key) {
        return preferences.getFloat(key, Constants.defaultFloat);
    }

    public void saveInt(String key, int value) {
        preferences.edit().putInt(key, value).apply();
    }

    public int getInt(String key) {
        return preferences.getInt(key, Constants.defaultInt);
    }

    public void saveBoolean(String key, boolean value) {
        preferences.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key) {
        return preferences.getBoolean(key, false);
    }
}

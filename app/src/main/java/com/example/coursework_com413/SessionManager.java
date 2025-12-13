package com.example.coursework_com413;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREFS = "session_prefs";
    private static final String KEY_USERNAME = "username";

    public static void setUsername(Context context, String username) {
        SharedPreferences sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        sp.edit().putString(KEY_USERNAME, username).apply();
    }

    public static String getUsername(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return sp.getString(KEY_USERNAME, null);
    }

    public static void clear(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        sp.edit().clear().apply();
    }
}

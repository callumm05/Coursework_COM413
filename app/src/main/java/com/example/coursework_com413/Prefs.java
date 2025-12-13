package com.example.coursework_com413;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {

    private static final String PREFS = "app_prefs";
    private static final String KEY_DEFAULT_SORT = "default_sort";

    public static void setDefaultSort(Context context, int sortMode) {
        SharedPreferences sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        sp.edit().putInt(KEY_DEFAULT_SORT, sortMode).apply();
    }

    public static int getDefaultSort(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return sp.getInt(KEY_DEFAULT_SORT, 0); // default: Name
    }
}

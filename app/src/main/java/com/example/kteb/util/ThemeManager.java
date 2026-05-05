package com.example.kteb.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

public class ThemeManager {

    private static final String PREFS_NAME = "theme_prefs";
    private static final String KEY_HEADER_COLOR = "header_color";
    private static final String KEY_BG_COLOR = "bg_color";
    private static final String KEY_ACCENT_COLOR = "accent_color";

    private static final int DEFAULT_HEADER = Color.parseColor("#8B7500");
    private static final int DEFAULT_BG = Color.parseColor("#C0C0C0");
    private static final int DEFAULT_ACCENT = Color.parseColor("#8B7500");

    public static int getHeaderColor(Context ctx) {
        return getPrefs(ctx).getInt(KEY_HEADER_COLOR, DEFAULT_HEADER);
    }

    public static int getBgColor(Context ctx) {
        return getPrefs(ctx).getInt(KEY_BG_COLOR, DEFAULT_BG);
    }

    public static int getAccentColor(Context ctx) {
        return getPrefs(ctx).getInt(KEY_ACCENT_COLOR, DEFAULT_ACCENT);
    }

    private static SharedPreferences getPrefs(Context ctx) {
        return ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}

package com.nexus.ffpanel;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsRepository {
    private static final String PREFS_NAME = "nexus_prefs";

    public static final String KEY_GAMING_MODE = "gaming_mode";
    public static final String KEY_PANEL_ACTIVE = "panel_active";
    public static final String KEY_SELECTED_GAME = "selected_game";
    public static final String KEY_FLOATING_PANEL_SIZE = "floating_panel_size";
    public static final String KEY_PANEL_OPACITY = "panel_opacity";
    public static final String KEY_HAPTIC_FEEDBACK = "haptic_feedback";
    public static final String KEY_THEME_DARK = "theme_dark";
    public static final String KEY_AMOLED = "amoled";

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        return prefs(context).getBoolean(key, defaultValue);
    }

    public static void putBoolean(Context context, String key, boolean value) {
        prefs(context).edit().putBoolean(key, value).apply();
    }

    public static String getString(Context context, String key, String defaultValue) {
        return prefs(context).getString(key, defaultValue);
    }

    public static void putString(Context context, String key, String value) {
        prefs(context).edit().putString(key, value).apply();
    }

    public static int getInt(Context context, String key, int defaultValue) {
        return prefs(context).getInt(key, defaultValue);
    }

    public static void putInt(Context context, String key, int value) {
        prefs(context).edit().putInt(key, value).apply();
    }

    public static void resetDefaults(Context context) {
        SharedPreferences.Editor editor = prefs(context).edit();
        editor.putBoolean(KEY_GAMING_MODE, false);
        editor.putBoolean(KEY_PANEL_ACTIVE, false);
        editor.putString(KEY_SELECTED_GAME, "");
        editor.putInt(KEY_FLOATING_PANEL_SIZE, 80);
        editor.putInt(KEY_PANEL_OPACITY, 80);
        editor.putBoolean(KEY_HAPTIC_FEEDBACK, true);
        editor.putBoolean(KEY_THEME_DARK, true);
        editor.putBoolean(KEY_AMOLED, true);
        editor.apply();
    }
}

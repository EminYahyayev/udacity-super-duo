package com.ewintory.alexandria.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Utilities and constants related to app preferences.
 */
public final class PrefUtils {

    private interface Prefs {
        /**
         * Per the design guidelines, you should show the drawer on launch until the user manually
         * expands it. This shared preference tracks this.
         */
        String USER_LEARNED_DRAWER = "pref_navigation_drawer_learned";

        String STARTING_DRAWER_ITEM = "pref_starting_fragment";
    }

    public static boolean isDrawerLearned(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(Prefs.USER_LEARNED_DRAWER, false);
    }

    public static void markDrawerLearned(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(Prefs.USER_LEARNED_DRAWER, true).apply();
    }

    public static int getStartingDrawerItem(final Context context, int defaultItem) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(Prefs.STARTING_DRAWER_ITEM, defaultItem);
    }

    public static void setStartingDrawerItem(final Context context, int position) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putInt(Prefs.STARTING_DRAWER_ITEM, position).apply();
    }

    public static void registerOnSharedPreferenceChangeListener(final Context context, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.registerOnSharedPreferenceChangeListener(listener);
    }

    public static void unregisterOnSharedPreferenceChangeListener(final Context context, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public static void clear(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().clear().apply();
    }

    private PrefUtils() { throw new AssertionError("No instances."); }
}

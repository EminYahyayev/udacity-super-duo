package com.ewintory.alexandria.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.ewintory.alexandria.R;
import com.ewintory.alexandria.service.BookService;

public final class Utils {

    public static boolean isConnected(Context context) {
        ConnectivityManager conn
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = conn.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @SuppressWarnings("ResourceType")
    public static @BookService.BookServiceStatus int getBookServiceStatus(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(context.getString(R.string.pref_book_service_status_key), BookService.BOOK_SERVICE_STATUS_UNKNOWN);
    }

    public static void resetBookServiceStatus(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor e = prefs.edit();
        e.putInt(context.getString(R.string.pref_book_service_status_key), BookService.BOOK_SERVICE_STATUS_UNKNOWN);
        e.apply();
    }

    public static String fixEanForISBN13(Context context, String ean) {
        String isbn13prefix = context.getResources().getString(R.string.isbn_13_prefix);
        //catch isbn10 numbers
        if (ean.length() == 10 && !ean.startsWith(isbn13prefix)) {
            return isbn13prefix.concat(ean);
        }

        return ean;
    }

    public static long convertEanStringToLong(String eanStr) {
        try {
            return Long.parseLong(eanStr);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private Utils() {
        throw new AssertionError("No instances.");
    }

}

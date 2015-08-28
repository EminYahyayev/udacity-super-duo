package com.ewintory.alexandria.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.ewintory.alexandria.R;
import com.ewintory.alexandria.provider.AlexandriaContract;
import com.ewintory.alexandria.ui.activity.MainActivity;
import com.ewintory.alexandria.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public final class BookService extends IntentService {
    private final String LOG_TAG = BookService.class.getSimpleName();

    public static final String ACTION_FETCH_BOOK = "com.ewintory.alexandria.services.action.ACTION_FETCH_BOOK";
    public static final String ACTION_DELETE_BOOK = "com.ewintory.alexandria.services.action.ACTION_DELETE_BOOK";

    public static final String EXTRA_EAN = "com.ewintory.alexandria.services.extras.EXTRA_EAN";

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({BOOK_SERVICE_STATUS_OK,
            BOOK_SERVICE_STATUS_SERVER_DOWN,
            BOOK_SERVICE_STATUS_SERVER_INVALID,
            BOOK_SERVICE_STATUS_UNKNOWN,
            BOOK_SERVICE_STATUS_INVALID})
    public @interface BookServiceStatus {}
    public static final int BOOK_SERVICE_STATUS_OK = 0;
    public static final int BOOK_SERVICE_STATUS_SERVER_DOWN = 1;
    public static final int BOOK_SERVICE_STATUS_SERVER_INVALID = 2;
    public static final int BOOK_SERVICE_STATUS_UNKNOWN = 3;
    public static final int BOOK_SERVICE_STATUS_INVALID = 4;

    public BookService() {
        super("Alexandria");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FETCH_BOOK.equals(action)) {
                final String ean = intent.getStringExtra(EXTRA_EAN);
                fetchBook(ean);
            } else if (ACTION_DELETE_BOOK.equals(action)) {
                final String ean = intent.getStringExtra(EXTRA_EAN);
                deleteBook(ean);
            }
        }
    }

    /**
     * Handle action deleteBook in the provided background thread with the provided
     * parameters.
     */
    private void deleteBook(String eanStr) {
        long longEan = Utils.convertEanStringToLong(eanStr);
        if (longEan != -1) {
            getContentResolver().delete(AlexandriaContract.BookEntry.buildBookUri(longEan), null, null);
        }
    }

    /**
     * Handle action fetchBook in the provided background thread with the provided
     * parameters.
     */
    private void fetchBook(String ean) {
        if (ean == null || ean.length() != 13) {
            return;
        }

        Cursor bookEntry = getContentResolver().query(
                AlexandriaContract.BookEntry.buildBookUri(Long.parseLong(ean)),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        if (bookEntry.getCount() > 0) {
            bookEntry.close();
            return;
        }

        bookEntry.close();

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String bookJsonString = null;

        try {
            final String BOOKS_BASE_URL = "https://www.googleapis.com/books/v1/volumes?";
            final String QUERY_PARAM = "q";

            final String ISBN_PARAM = "isbn:" + ean;

            Uri builtUri = Uri.parse(BOOKS_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, ISBN_PARAM)
                    .build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                setBookServiceStatus(BOOK_SERVICE_STATUS_SERVER_DOWN);
                return;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
                buffer.append("\n");
            }

            if (buffer.length() == 0) {
                setBookServiceStatus(BOOK_SERVICE_STATUS_SERVER_DOWN);
                return;
            }
            bookJsonString = buffer.toString();
            Log.d(LOG_TAG, "bookJsonString=" + bookJsonString);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error ", e);
            setBookServiceStatus(BOOK_SERVICE_STATUS_SERVER_DOWN);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }

        }

        final String ITEMS = "items";

        final String VOLUME_INFO = "volumeInfo";

        final String TITLE = "title";
        final String SUBTITLE = "subtitle";
        final String AUTHORS = "authors";
        final String DESC = "description";
        final String CATEGORIES = "categories";
        final String IMG_URL_PATH = "imageLinks";
        final String IMG_URL = "thumbnail";

        try {
            JSONObject bookJson = new JSONObject(bookJsonString);
            JSONArray bookArray;
            if (bookJson.has(ITEMS)) {
                bookArray = bookJson.getJSONArray(ITEMS);
                setBookServiceStatus(BOOK_SERVICE_STATUS_OK);
            } else {
                setBookServiceStatus(BOOK_SERVICE_STATUS_INVALID);
                return;
            }

            JSONObject bookInfo = ((JSONObject) bookArray.get(0)).getJSONObject(VOLUME_INFO);

            String title = bookInfo.getString(TITLE);

            String subtitle = "";
            if (bookInfo.has(SUBTITLE)) {
                subtitle = bookInfo.getString(SUBTITLE);
            }

            String desc = "";
            if (bookInfo.has(DESC)) {
                desc = bookInfo.getString(DESC);
            }

            String imgUrl = "";
            if (bookInfo.has(IMG_URL_PATH) && bookInfo.getJSONObject(IMG_URL_PATH).has(IMG_URL)) {
                imgUrl = bookInfo.getJSONObject(IMG_URL_PATH).getString(IMG_URL);
            }

            writeBackBook(ean, title, subtitle, desc, imgUrl);

            if (bookInfo.has(AUTHORS)) {
                writeBackAuthors(ean, bookInfo.getJSONArray(AUTHORS));
            }
            if (bookInfo.has(CATEGORIES)) {
                writeBackCategories(ean, bookInfo.getJSONArray(CATEGORIES));
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error ", e);
            setBookServiceStatus(BOOK_SERVICE_STATUS_SERVER_INVALID);
        }
    }

    private void writeBackBook(String ean, String title, String subtitle, String desc, String imgUrl) {
        ContentValues values = new ContentValues();
        values.put(AlexandriaContract.BookEntry._ID, ean);
        values.put(AlexandriaContract.BookEntry.TITLE, title);
        values.put(AlexandriaContract.BookEntry.IMAGE_URL, imgUrl);
        values.put(AlexandriaContract.BookEntry.SUBTITLE, subtitle);
        values.put(AlexandriaContract.BookEntry.DESC, desc);
        getContentResolver().insert(AlexandriaContract.BookEntry.CONTENT_URI, values);
    }

    private void writeBackAuthors(String ean, JSONArray jsonArray) throws JSONException {
        ContentValues values = new ContentValues();
        for (int i = 0; i < jsonArray.length(); i++) {
            values.put(AlexandriaContract.AuthorEntry._ID, ean);
            values.put(AlexandriaContract.AuthorEntry.AUTHOR, jsonArray.getString(i));
            getContentResolver().insert(AlexandriaContract.AuthorEntry.CONTENT_URI, values);
            values = new ContentValues();
        }
    }

    private void writeBackCategories(String ean, JSONArray jsonArray) throws JSONException {
        ContentValues values = new ContentValues();
        for (int i = 0; i < jsonArray.length(); i++) {
            values.put(AlexandriaContract.CategoryEntry._ID, ean);
            values.put(AlexandriaContract.CategoryEntry.CATEGORY, jsonArray.getString(i));
            getContentResolver().insert(AlexandriaContract.CategoryEntry.CONTENT_URI, values);
            values = new ContentValues();
        }
    }

    private void setBookServiceStatus(@BookServiceStatus int status) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(BookService.this);
        SharedPreferences.Editor e = prefs.edit();
        Log.d(LOG_TAG, "Status = " + status);
        e.putInt(getString(R.string.pref_book_service_status_key), status);
        e.apply();
    }
}
package com.ewintory.footballscores.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

public class ScoresProvider extends ContentProvider {
    private static final String TAG = ScoresProvider.class.getSimpleName();

    private static ScoresDBHelper mOpenHelper;
    private static final int MATCHES = 100;
    private static final int MATCHES_WITH_LEAGUE = 101;
    private static final int MATCHES_WITH_ID = 102;
    private static final int MATCHES_WITH_DATE = 103;

    private UriMatcher mUriMatcher = buildUriMatcher();
    private static final String SCORES_BY_LEAGUE = ScoresContract.ScoreEntry.COLUMN_LEAGUE + " = ?";

    private static final String SCORES_BY_DATE =
            ScoresContract.ScoreEntry.COLUMN_DATE + " LIKE ?";
    private static final String SCORES_BY_ID =
            ScoresContract.ScoreEntry.COLUMN_MATCH_ID + " = ?";


    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ScoresContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, null, MATCHES);
        matcher.addURI(authority, "id", MATCHES_WITH_ID);
        matcher.addURI(authority, "league", MATCHES_WITH_LEAGUE);
        matcher.addURI(authority, "date", MATCHES_WITH_DATE);

        return matcher;
    }

    private int matchUri(Uri uri) {
        String link = uri.toString();

        if (link.contentEquals(ScoresContract.BASE_CONTENT_URI.toString())) {
            return MATCHES;
        } else if (link.contentEquals(ScoresContract.ScoreEntry.buildScoreWithDate().toString())) {
            return MATCHES_WITH_DATE;
        } else if (link.contentEquals(ScoresContract.ScoreEntry.buildScoreWithId().toString())) {
            return MATCHES_WITH_ID;
        } else if (link.contentEquals(ScoresContract.ScoreEntry.buildScoreWithLeague().toString())) {
            return MATCHES_WITH_LEAGUE;
        }
        return -1;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new ScoresDBHelper(getContext());
        return false;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case MATCHES:
                return ScoresContract.ScoreEntry.CONTENT_TYPE;
            case MATCHES_WITH_LEAGUE:
                return ScoresContract.ScoreEntry.CONTENT_TYPE;
            case MATCHES_WITH_ID:
                return ScoresContract.ScoreEntry.CONTENT_ITEM_TYPE;
            case MATCHES_WITH_DATE:
                return ScoresContract.ScoreEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri :" + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        int match = matchUri(uri);
        switch (match) {
            case MATCHES:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ScoresContract.ScoreEntry.TABLE_NAME,
                        projection, null, null, null, null, sortOrder);
                break;
            case MATCHES_WITH_DATE:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ScoresContract.ScoreEntry.TABLE_NAME,
                        projection, SCORES_BY_DATE, selectionArgs, null, null, sortOrder);
                break;
            case MATCHES_WITH_ID:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ScoresContract.ScoreEntry.TABLE_NAME,
                        projection, SCORES_BY_ID, selectionArgs, null, null, sortOrder);
                break;
            case MATCHES_WITH_LEAGUE:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ScoresContract.ScoreEntry.TABLE_NAME,
                        projection, SCORES_BY_LEAGUE, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri" + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int bulkInsert(Uri uri, @NonNull ContentValues[] values) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.delete(ScoresContract.ScoreEntry.TABLE_NAME, null, null);

        switch (matchUri(uri)) {
            case MATCHES:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(
                                ScoresContract.ScoreEntry.TABLE_NAME,
                                null,
                                value,
                                SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                Log.w(TAG, uri.toString());
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }
}

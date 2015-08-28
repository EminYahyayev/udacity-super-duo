package com.ewintory.footballscores.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ewintory.footballscores.provider.ScoresContract.ScoreEntry;


/**
 * Mistake found: onUpgrade was only dropping the db, without creating it again.
 */
public class ScoresDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "scores.db";
    private static final int DATABASE_VERSION = 2;

    public ScoresDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CreateScoresTable = "CREATE TABLE " + ScoreEntry.TABLE_NAME + " ("
                + ScoreEntry._ID + " INTEGER PRIMARY KEY,"
                + ScoreEntry.COLUMN_DATE + " TEXT NOT NULL,"
                + ScoreEntry.COLUMN_TIME + " INTEGER NOT NULL,"
                + ScoreEntry.COLUMN_HOME + " TEXT NOT NULL,"
                + ScoreEntry.COLUMN_AWAY + " TEXT NOT NULL,"
                + ScoreEntry.COLUMN_HOME_CREST + " TEXT,"
                + ScoreEntry.COLUMN_AWAY_CREST + " TEXT,"
                + ScoreEntry.COLUMN_LEAGUE + " INTEGER NOT NULL,"
                + ScoreEntry.COLUMN_LEAGUE_CAPTION + " TEXT,"
                + ScoreEntry.COLUMN_HOME_GOALS + " TEXT NOT NULL,"
                + ScoreEntry.COLUMN_AWAY_GOALS + " TEXT NOT NULL,"
                + ScoreEntry.COLUMN_MATCH_ID + " INTEGER NOT NULL,"
                + ScoreEntry.COLUMN_MATCH_DAY + " INTEGER NOT NULL,"
                + " UNIQUE (" + ScoreEntry.COLUMN_MATCH_ID + ") ON CONFLICT REPLACE"
                + " );";
        db.execSQL(CreateScoresTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ScoreEntry.TABLE_NAME);
        onCreate(db);
    }
}

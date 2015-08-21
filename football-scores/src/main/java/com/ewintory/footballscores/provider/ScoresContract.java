package com.ewintory.footballscores.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the scores database.
 */
public final class ScoresContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.ewintory.footballscores";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    public static final String PATH_SCORES = "scores";

    public static final class ScoreEntry implements BaseColumns {
        //public static Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SCORES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SCORES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SCORES;

        public static final String TABLE_NAME = "scores";

        public static final String COLUMN_LEAGUE = "league";
        public static final String COLUMN_LEAGUE_CAPTION = "league_caption";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_HOME = "home";
        public static final String COLUMN_AWAY = "away";
        public static final String COLUMN_HOME_CREST = "home_crest";
        public static final String COLUMN_AWAY_CREST = "away_crest";
        public static final String COLUMN_HOME_GOALS = "home_goals";
        public static final String COLUMN_AWAY_GOALS = "away_goals";
        public static final String COLUMN_MATCH_ID = "match_id";
        public static final String COLUMN_MATCH_DAY = "match_day";


        public static Uri buildScoreWithLeague() {
            return BASE_CONTENT_URI.buildUpon().appendPath("league").build();
        }

        public static Uri buildScoreWithId() {
            return BASE_CONTENT_URI.buildUpon().appendPath("id").build();
        }

        public static Uri buildScoreWithDate() {
            return BASE_CONTENT_URI.buildUpon().appendPath("date").build();
        }
    }
}

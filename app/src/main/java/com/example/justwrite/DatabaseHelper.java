package com.example.justwrite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    // It's a good idea to always define a log tag like this.
    private static final String TAG = "DATABASE_HELPER";

    // has to be 1 first time or app will crash
    private static final int DATABASE_VERSION = 1;
    public static final String PROJECTS_TABLE = "PROJECTS";
    public static final String SPRINTS_TABLE = "SPRINTS";
    public static final String PROJECTS_STATS_TABLE = "PROJECT_STATS";
    private static final String DATABASE_NAME = "JUSTWRITE.DB";

    // Common Column Names
    public static final String KEY_ID = "id";
    public static final String KEY_CREATED_AT = "created_at";
    public static final String KEY_PROJECT_ID = "project_id";

    // PROJECTS Table Column Names
    public static final String KEY_TITLE = "title";
    public static final String KEY_GENRE = "genre";

    // SPRINTS Table Column Names
    public static final String KEY_SPRINT_TIME = "sprint_time";
    public static final String KEY_UNFOCUSED_TIME = "unfocused_time";
    public static final String KEY_WORD_COUNT = "word_count";

    // PROJECT STATS Column Names
    public static final String KEY_TOTAL_WORDS = "total_words";
    public static final String KEY_TOTAL_TIME = "total_time";
    public static final String KEY_TOTAL_UNFOCUSED_TIME = "total_unfocused_time";

    // PROJECTS Table Create Statement
    private static final String CREATE_TABLE_PROJECTS = "CREATE TABLE " + PROJECTS_TABLE
            + "(" + KEY_PROJECT_ID + " INTEGER PRIMARY KEY," + KEY_TITLE + " TEXT,"
            + KEY_GENRE + " TEXT)";

    // SPRINTS Table Create Statement
    private static final String CREATE_TABLE_SPRINTS = "CREATE TABLE " + SPRINTS_TABLE
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_SPRINT_TIME + " INTEGER,"
            + KEY_UNFOCUSED_TIME + " INTEGER," + KEY_WORD_COUNT + " INTEGER," + KEY_PROJECT_ID + " INTEGER)";

    // PROJECT STATS Table Create Statement
    private static final String CREATE_TABLE_PROJECT_STATS = "CREATE TABLE " + PROJECTS_STATS_TABLE
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TOTAL_WORDS + " INTEGER,"
            + KEY_TOTAL_TIME + " INTEGER," + KEY_TOTAL_UNFOCUSED_TIME + " INTEGER," + KEY_PROJECT_ID + " INTEGER)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PROJECTS);
        db.execSQL(CREATE_TABLE_SPRINTS);
        db.execSQL(CREATE_TABLE_PROJECT_STATS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public int getTotalWordCount(long currentProjectId) {
        Cursor result = getReadableDatabase().query(
                PROJECTS_STATS_TABLE,
                new String[] { KEY_TOTAL_WORDS },
                KEY_PROJECT_ID + "=?",
                new String[] { String.valueOf(currentProjectId) },
                null,
                null,
                null
        );
        if (result.moveToFirst()) {
            int wordCount = result.getInt(result.getColumnIndex(KEY_TOTAL_WORDS));
            Log.d("HELP_WORD_COUNT", String.valueOf(wordCount));
            return wordCount;
        }
        return 0;
    }

    public int getTotalTime(long currentProjectId) {
        Cursor result = getReadableDatabase().query(
                PROJECTS_STATS_TABLE,
                new String[] { KEY_TOTAL_TIME },
                KEY_PROJECT_ID + "=?",
                new String[] { String.valueOf(currentProjectId) },
                null,
                null,
                null
        );
        if (result.moveToFirst()) {
            int totalTime = result.getInt(result.getColumnIndex(KEY_TOTAL_TIME));
            Log.d("HELP_TOTAL_TIME", String.valueOf(totalTime));
            return totalTime;
        }
        return 0;
    }

    public int getTotalUnfocusedTime(long currentProjectId) {
        Cursor result = getReadableDatabase().query(
                PROJECTS_STATS_TABLE,
                new String[] { KEY_TOTAL_UNFOCUSED_TIME },
                KEY_PROJECT_ID + "=?",
                new String[] { String.valueOf(currentProjectId) },
                null,
                null,
                null
        );
        if (result.moveToFirst()) {
            int unfocusedTime = result.getInt(result.getColumnIndex(KEY_TOTAL_UNFOCUSED_TIME));
            Log.d("HELP_UNFOCUSED", String.valueOf(unfocusedTime));
            return unfocusedTime;
        }
        return 0;
    }

//    public Long getProjectIdFrom(String title) {
//        String[] projection = {DatabaseHelper.KEY_PROJECT_ID, DatabaseHelper.KEY_TITLE};
//        String selection = DatabaseHelper.KEY_TITLE + " = ?";
//        String[] selectionArgs = {selected.getTitle()};
//        Cursor cursor = mReadableDatabase.query(DatabaseHelper.PROJECTS_TABLE, projection, selection, selectionArgs, null, null, null);
//        cursor.moveToNext();
//        Long projectId = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.KEY_PROJECT_ID));
//        cursor.close();
//    }
}

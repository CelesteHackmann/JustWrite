package com.example.justwrite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    // has to be 1 first time or app will crash
    private static final int DATABASE_VERSION = 1;
    public static final String PROJECTS_TABLE = "PROJECTS";
    public static final String SPRINTS_TABLE = "SPRINTS";
    public static final String PROJECTS_STATS_TABLE = "PROJECT_STATS";
    private static final String DATABASE_NAME = "JUSTWRITE.DB";

    // Common Column Names
    public static final String KEY_ID = "id";
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

    public long insertProject(String pName, String pGenre) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues projectValues = new ContentValues();
        projectValues.put(KEY_TITLE, pName);
        projectValues.put(KEY_GENRE, pGenre);
        return db.insert(PROJECTS_TABLE, null, projectValues);
    }

    public void insertProjectStats(long currentProjectId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues statsValues = new ContentValues();
        statsValues.put(KEY_PROJECT_ID, currentProjectId);
        statsValues.put(KEY_TOTAL_TIME, 0);
        statsValues.put(KEY_TOTAL_WORDS, 0);
        statsValues.put(KEY_TOTAL_UNFOCUSED_TIME, 0);
        db.insert(PROJECTS_STATS_TABLE, null, statsValues);
    }

    public void addSprint(int sprintTime, int unfocusedTime, int wordCount, long currentProjectId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues sprintValues = new ContentValues();
        sprintValues.put(KEY_SPRINT_TIME, sprintTime);
        sprintValues.put(KEY_WORD_COUNT, wordCount);
        sprintValues.put(KEY_UNFOCUSED_TIME, unfocusedTime);
        sprintValues.put(KEY_PROJECT_ID, currentProjectId);
        db.insert(SPRINTS_TABLE, null, sprintValues);
    }

    public void updateProjectStats(int sprintTime, int unfocusedTime, int wordCount, long currentProjectId) {
        SQLiteDatabase db = getWritableDatabase();
        int newWordCount = wordCount + getTotalWordCount(currentProjectId);
        int newTotalTime = sprintTime + getTotalTime(currentProjectId);
        int newTotalUnfocusedTime = unfocusedTime + getTotalUnfocusedTime(currentProjectId);

        ContentValues statsValues = new ContentValues();
        statsValues.put(KEY_TOTAL_WORDS, newWordCount);
        statsValues.put(KEY_TOTAL_TIME, newTotalTime);
        statsValues.put(KEY_TOTAL_UNFOCUSED_TIME, newTotalUnfocusedTime);

        String selection = KEY_PROJECT_ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(currentProjectId)};
        db.update(PROJECTS_STATS_TABLE, statsValues, selection, selectionArgs);
    }
}

package com.example.justwrite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.LinkedList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper sInstance;
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
    public static final String KEY_NUMBER_OF_SPRINTS = "number_of_sprints";

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
            + KEY_TOTAL_TIME + " INTEGER," + KEY_TOTAL_UNFOCUSED_TIME + " INTEGER,"
            + KEY_NUMBER_OF_SPRINTS + " INTEGER," + KEY_PROJECT_ID + " INTEGER)";

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context);
        }
        return sInstance;
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
        statsValues.put(KEY_NUMBER_OF_SPRINTS, 0);
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
        int newWordCount = wordCount + getTotalWordCount(String.valueOf(currentProjectId));
        int newTotalTime = sprintTime + getTotalTime(String.valueOf(currentProjectId));
        int newTotalUnfocusedTime = unfocusedTime + getTotalUnfocusedTime(String.valueOf(currentProjectId));
        int newNumberSprints = 1 + getCurrentNumberOfSprints(String.valueOf(currentProjectId));

        ContentValues statsValues = new ContentValues();
        statsValues.put(KEY_TOTAL_WORDS, newWordCount);
        statsValues.put(KEY_TOTAL_TIME, newTotalTime);
        statsValues.put(KEY_TOTAL_UNFOCUSED_TIME, newTotalUnfocusedTime);
        statsValues.put(KEY_NUMBER_OF_SPRINTS, newNumberSprints);

        String selection = KEY_PROJECT_ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(currentProjectId)};
        db.update(PROJECTS_STATS_TABLE, statsValues, selection, selectionArgs);
    }

    public ArrayList<Project> getProjectList() {
        ArrayList<Project> projects = new ArrayList<>();
        Cursor cursor = getReadableDatabase().query(
                PROJECTS_TABLE,
                null,
                null,
                null,
                null,
                null,
                null
        );
        while (cursor.moveToNext()) {
            projects.add(new Project(cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                    cursor.getString(cursor.getColumnIndex(KEY_GENRE)),
                    cursor.getLong(cursor.getColumnIndex(KEY_PROJECT_ID))));
        }
        cursor.close();
        return projects;
    }

    public LinkedList<Analytic> getBasicAnalyticsList(String selectedProjectId) {
        LinkedList<Analytic> analyticsList = new LinkedList<>();
        Cursor cursor = getReadableDatabase().query(PROJECTS_STATS_TABLE,
                null,
                KEY_PROJECT_ID + "=?",
                new String[] {selectedProjectId},
                null, null, null);
        while (cursor.moveToNext()){
            analyticsList.addLast(new Analytic("Total Words", cursor.getInt(cursor.getColumnIndex(KEY_TOTAL_WORDS))+ " words"));
            analyticsList.addLast(new Analytic("Total Time", cursor.getInt(cursor.getColumnIndex(KEY_TOTAL_TIME)) + " seconds"));
            analyticsList.addLast(new Analytic("Total Unfocused Time", cursor.getInt(cursor.getColumnIndex(KEY_TOTAL_UNFOCUSED_TIME)) + " seconds"));
        }
        cursor.close();
        return analyticsList;
    }

    public int getTotalWordCount(String currentProjectId) {
        Cursor cursor = getReadableDatabase().query(
                PROJECTS_STATS_TABLE,
                new String[] { KEY_TOTAL_WORDS },
                KEY_PROJECT_ID + "=?",
                new String[] { currentProjectId},
                null,
                null,
                null
        );
        if (cursor.moveToFirst()) {
            return cursor.getInt(cursor.getColumnIndex(KEY_TOTAL_WORDS));
        }
        cursor.close();
        return 0;
    }

    public int getTotalTime(String currentProjectId) {
        Cursor cursor = getReadableDatabase().query(
                PROJECTS_STATS_TABLE,
                new String[] { KEY_TOTAL_TIME },
                KEY_PROJECT_ID + "=?",
                new String[] { String.valueOf(currentProjectId) },
                null,
                null,
                null
        );
        if (cursor.moveToFirst()) {
            return cursor.getInt(cursor.getColumnIndex(KEY_TOTAL_TIME));
        }
        cursor.close();
        return 0;
    }

    public int getTotalUnfocusedTime(String currentProjectId) {
        Cursor cursor = getReadableDatabase().query(
                PROJECTS_STATS_TABLE,
                new String[] { KEY_TOTAL_UNFOCUSED_TIME },
                KEY_PROJECT_ID + "=?",
                new String[] {currentProjectId},
                null,
                null,
                null
        );
        if (cursor.moveToFirst()) {
            return cursor.getInt(cursor.getColumnIndex(KEY_TOTAL_UNFOCUSED_TIME));
        }
        cursor.close();
        return 0;
    }

    private int getCurrentNumberOfSprints(String currentProjectId) {
        Cursor cursor = getReadableDatabase().query(
                PROJECTS_STATS_TABLE,
                new String[] { KEY_NUMBER_OF_SPRINTS },
                KEY_PROJECT_ID + "=?",
                new String[] {currentProjectId},
                null, null, null
        );
        if (cursor.moveToFirst()) {
            return cursor.getInt(cursor.getColumnIndex(KEY_NUMBER_OF_SPRINTS));
        }
        cursor.close();
        return 0;
    }

    public LinkedList<Sprint> getSprintsForProject(String selectedProjectId) {
        LinkedList<Sprint> sprintList = new LinkedList<>();
        Cursor cursor = getReadableDatabase().query(SPRINTS_TABLE,
                null,
                KEY_PROJECT_ID + "=?",
                new String[] {selectedProjectId},
                null, null, null);
        while (cursor.moveToNext()) {
            sprintList.addFirst(new Sprint(cursor.getInt(cursor.getColumnIndex(KEY_SPRINT_TIME)),
                    cursor.getInt(cursor.getColumnIndex(KEY_UNFOCUSED_TIME)),
                    cursor.getInt(cursor.getColumnIndex(KEY_WORD_COUNT))));
        }
        cursor.close();
        return sprintList;
    }

    public Analytic getWordsPerMinuteAnalyticForProject(String selectedProjectId) {
        int wordsPerMinute = getWordsPerMinute(selectedProjectId);
        return new Analytic("Words Per Minute", wordsPerMinute + " words per minute");
    }

    public Analytic getAverageWordsPer30MinAnalyticForProject(String selectedProjectId) {
        int wordsPer30Minutes = getWordsPerMinute(selectedProjectId) * 30;
        return new Analytic("Words Per 30 Minutes", wordsPer30Minutes + " words per 30 minutes");
    }

    public Analytic getAverageWordsPerSprintForProject(String selectedProjectId) {
        int totalWords = getTotalWordCount(selectedProjectId);
        int numSprints = getCurrentNumberOfSprints(selectedProjectId);
        int avgWordsPerSprint = calculateAverage(totalWords, numSprints);
        return new Analytic("Average Words Per Sprint", avgWordsPerSprint +" words");
    }

    public Analytic getAverageSprintTimeForProject(String selectedProjectId) {
        int totalTime = getTotalTime(selectedProjectId);
        int numSprints = getCurrentNumberOfSprints(selectedProjectId);
        int avgSecondsPerSprint = calculateAverage(totalTime, numSprints);
        return new Analytic("Average Time Per Sprint", avgSecondsPerSprint + " seconds");
    }

    private int getWordsPerMinute(String selectedProjectId) {
        float totalWords = getTotalWordCount(selectedProjectId);
        float totalTimeSeconds = getTotalTime(selectedProjectId);
        float totalTimeMinutes = totalTimeSeconds / 60;
        int wordsPerMinute = 0;
        if (totalTimeMinutes != 0) {
            wordsPerMinute = (int) (totalWords / totalTimeMinutes);
        }
        return wordsPerMinute;
    }

    private int calculateAverage(int sum, int numOfOccurrences) {
        int average = 0;
        if (numOfOccurrences != 0) {
            average = sum / numOfOccurrences;
        }
        return average;
    }
}

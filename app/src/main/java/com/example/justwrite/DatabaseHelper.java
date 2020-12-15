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
    public static final String DATABASE_NAME = "JUSTWRITE.DB";

    // Common Column Names
    public static final String KEY_ID = "id";
    public static final String KEY_PROJECT_ID = "project_id";

    // PROJECTS Table Column Names
    public static final String KEY_TITLE = "title";
    public static final String KEY_GENRE = "genre";
    private static final String KEY_PROJECT_ARCHIVED = "archived";

    // SPRINTS Table Column Names
    public static final String KEY_SPRINT_TIME = "sprint_time";
    public static final String KEY_UNFOCUSED_TIME = "unfocused_time";
    public static final String KEY_WORD_COUNT = "word_count";
    public static final String KEY_SPRINT_DATE = "sprint_date";

    // PROJECT STATS Column Names
    public static final String KEY_TOTAL_WORDS = "total_words";
    public static final String KEY_TOTAL_TIME = "total_time";
    public static final String KEY_TOTAL_UNFOCUSED_TIME = "total_unfocused_time";
    public static final String KEY_NUMBER_OF_SPRINTS = "number_of_sprints";

    // PROJECTS Table Create Statement
    public static final String CREATE_TABLE_PROJECTS = "CREATE TABLE " + PROJECTS_TABLE
            + "(" + KEY_PROJECT_ID + " INTEGER PRIMARY KEY," + KEY_TITLE + " TEXT,"
            + KEY_GENRE + " TEXT," + KEY_PROJECT_ARCHIVED + " INTEGER)";

    // SPRINTS Table Create Statement
    public static final String CREATE_TABLE_SPRINTS = "CREATE TABLE " + SPRINTS_TABLE
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_SPRINT_TIME + " INTEGER,"
            + KEY_UNFOCUSED_TIME + " INTEGER," + KEY_WORD_COUNT + " INTEGER," + KEY_SPRINT_DATE + " TEXT,"
            + KEY_PROJECT_ID + " INTEGER)";

    // PROJECT STATS Table Create Statement
    public static final String CREATE_TABLE_PROJECT_STATS = "CREATE TABLE " + PROJECTS_STATS_TABLE
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TOTAL_WORDS + " INTEGER,"
            + KEY_TOTAL_TIME + " INTEGER," + KEY_TOTAL_UNFOCUSED_TIME + " INTEGER,"
            + KEY_NUMBER_OF_SPRINTS + " INTEGER," + KEY_PROJECT_ID + " TEXT)";

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
        projectValues.put(KEY_PROJECT_ARCHIVED, false);
        long projectId = db.insert(PROJECTS_TABLE, null, projectValues);
        db.close();
        return projectId;
    }

    public void insertProjectStats(String currentProjectId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues statsValues = new ContentValues();
        statsValues.put(KEY_PROJECT_ID, currentProjectId);
        statsValues.put(KEY_TOTAL_TIME, 0);
        statsValues.put(KEY_TOTAL_WORDS, 0);
        statsValues.put(KEY_TOTAL_UNFOCUSED_TIME, 0);
        statsValues.put(KEY_NUMBER_OF_SPRINTS, 0);
        db.insert(PROJECTS_STATS_TABLE, null, statsValues);
        db.close();
    }

    public void addSprint(Sprint sprint, String currentProjectId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues sprintValues = new ContentValues();
        sprintValues.put(KEY_SPRINT_TIME, sprint.getSprintTimeSeconds());
        sprintValues.put(KEY_WORD_COUNT, sprint.getWordCount());
        sprintValues.put(KEY_UNFOCUSED_TIME, sprint.getUnfocusedSeconds());
        sprintValues.put(KEY_PROJECT_ID, currentProjectId);
        sprintValues.put(KEY_SPRINT_DATE, sprint.getTimestamp());
        db.insert(SPRINTS_TABLE, null, sprintValues);
        db.close();
    }

    public void updateProjectStats(int sprintTime, int unfocusedTime, int wordCount, String currentProjectId) {
        SQLiteDatabase db = getWritableDatabase();
        int newWordCount = wordCount + getTotalWordCount(currentProjectId);
        int newTotalTime = sprintTime + getTotalTime(currentProjectId);
        int newTotalUnfocusedTime = unfocusedTime + getTotalUnfocusedTime(currentProjectId);
        int newNumberSprints = 1 + getCurrentNumberOfSprints(currentProjectId);

        ContentValues statsValues = new ContentValues();
        statsValues.put(KEY_TOTAL_WORDS, newWordCount);
        statsValues.put(KEY_TOTAL_TIME, newTotalTime);
        statsValues.put(KEY_TOTAL_UNFOCUSED_TIME, newTotalUnfocusedTime);
        statsValues.put(KEY_NUMBER_OF_SPRINTS, newNumberSprints);

        String selection = KEY_PROJECT_ID + " LIKE ?";
        String[] selectionArgs = {currentProjectId};
        db.update(PROJECTS_STATS_TABLE, statsValues, selection, selectionArgs);
        db.close();
    }

    public void updateProjectTitleAndGenre(String currentProjectId, String newTitle, String newGenre) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues projectValues = new ContentValues();
        projectValues.put(KEY_TITLE, newTitle);
        projectValues.put(KEY_GENRE, newGenre);
        String selection = KEY_PROJECT_ID + " LIKE ?";
        String[] selectionArgs = {currentProjectId};
        db.update(PROJECTS_TABLE, projectValues, selection, selectionArgs);
        db.close();
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
                    cursor.getString(cursor.getColumnIndex(KEY_PROJECT_ID))));
        }
        cursor.close();
        return projects;
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
        int result = 0;
        if (cursor.moveToFirst()) {
            result = cursor.getInt(cursor.getColumnIndex(KEY_TOTAL_WORDS));
        }
        cursor.close();
        return result;
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
        int result = 0;
        if (cursor.moveToFirst()) {
            result = cursor.getInt(cursor.getColumnIndex(KEY_TOTAL_TIME));
        }
        cursor.close();
        return result;
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
        int result = 0;
        if (cursor.moveToFirst()) {
            result = cursor.getInt(cursor.getColumnIndex(KEY_TOTAL_UNFOCUSED_TIME));
        }
        cursor.close();
        return result;
    }

    private int getCurrentNumberOfSprints(String currentProjectId) {
        Cursor cursor = getReadableDatabase().query(
                PROJECTS_STATS_TABLE,
                new String[] { KEY_NUMBER_OF_SPRINTS },
                KEY_PROJECT_ID + "=?",
                new String[] {currentProjectId},
                null, null, null
        );
        int result = 0;
        if (cursor.moveToFirst()) {
            result = cursor.getInt(cursor.getColumnIndex(KEY_NUMBER_OF_SPRINTS));
        }
        cursor.close();
        return result;
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
                    cursor.getInt(cursor.getColumnIndex(KEY_WORD_COUNT)),
                    cursor.getString(cursor.getColumnIndex(KEY_SPRINT_DATE))));
        }
        cursor.close();
        return sprintList;
    }

    public int projectIsArchived(String selectedProjectId) {
        Cursor cursor = getReadableDatabase().query(PROJECTS_TABLE,
                new String[]{KEY_PROJECT_ARCHIVED},
                KEY_PROJECT_ID + "=?",
                new String[] {selectedProjectId},
                null, null, null);
        if (cursor.moveToNext()) {
            return cursor.getInt(cursor.getColumnIndex(KEY_PROJECT_ARCHIVED));
        }
        return -1;
    }

    public void setProjectAsArchived(String selectedProjectId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues projectValues = new ContentValues();
        projectValues.put(KEY_PROJECT_ARCHIVED, 1);
        String selection = KEY_PROJECT_ID + " LIKE ?";
        String[] selectionArgs = {selectedProjectId};
        db.update(PROJECTS_TABLE, projectValues, selection, selectionArgs);
        db.close();
    }

    public void setProjectAsUnarchived(String selectedProjectId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues projectValues = new ContentValues();
        projectValues.put(KEY_PROJECT_ARCHIVED, 0);
        String selection = KEY_PROJECT_ID + " LIKE ?";
        String[] selectionArgs = {selectedProjectId};
        db.update(PROJECTS_TABLE, projectValues, selection, selectionArgs);
        db.close();
    }

    public Analytic getWordsPerMinuteAnalyticForProject(String selectedProjectId) {
        int wordsPerMinute = getWordsPerMinute(selectedProjectId);
        return new Analytic("Average Words Per Minute", wordsPerMinute + " words");
    }

    public Analytic getAverageWordsPer30MinAnalyticForProject(String selectedProjectId) {
        int wordsPer30Minutes = getWordsPerMinute(selectedProjectId) * 30;
        return new Analytic("Average Words Per 30 Minutes", wordsPer30Minutes + " words");
    }

    public Analytic getAverageWordsPerSprintForProject(String selectedProjectId) {
        int totalWords = getTotalWordCount(selectedProjectId);
        int numSprints = getCurrentNumberOfSprints(selectedProjectId);
        int avgWordsPerSprint = calculateAverage(totalWords, numSprints);
        return new Analytic("Average Words Per Sprint", avgWordsPerSprint +" words");
    }

    public Analytic getAverageSprintTimeForProject(String selectedProjectId) {
        int totalSeconds = getTotalTime(selectedProjectId);
        int numSprints = getCurrentNumberOfSprints(selectedProjectId);
        int avgSecondsPerSprint = calculateAverage(totalSeconds, numSprints);
        int minutes = avgSecondsPerSprint / 60;
        int seconds = avgSecondsPerSprint % 60;
        String minuteString = String.valueOf(minutes);
        String secondString = String.format("%02d", seconds);
        return new Analytic("Average Sprint Time", minuteString + ":" + secondString);
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

    public Analytic getTotalWordCountAnalyticForProject(String projectId) {
        Cursor cursor = getReadableDatabase().query(PROJECTS_STATS_TABLE,
                new String[]{KEY_TOTAL_WORDS},
                KEY_PROJECT_ID + "=?",
                new String[]{projectId},
                null, null, null);
        Analytic wordCountAnalytic = new Analytic("Total Words", "0 words");
        if (cursor.moveToNext()) {
            wordCountAnalytic = new Analytic("Total Words", cursor.getInt(cursor.getColumnIndex(KEY_TOTAL_WORDS)) + " words");
        }
        cursor.close();
        return wordCountAnalytic;
    }

    public Analytic getTotalTimeAnalyticForProject(String projectId) {
        Cursor cursor = getReadableDatabase().query(PROJECTS_STATS_TABLE,
                new String[]{KEY_TOTAL_TIME},
                KEY_PROJECT_ID + "=?",
                new String[]{projectId},
                null, null, null);
        Analytic wordCountAnalytic = new Analytic("Total Time", "0:00");
        if (cursor.moveToNext()) {
            int totalSeconds = cursor.getInt(cursor.getColumnIndex(KEY_TOTAL_TIME));
            int minutes = totalSeconds / 60;
            int seconds = totalSeconds % 60;
            String minuteString = String.valueOf(minutes);
            String secondString = String.format("%02d", seconds);
            wordCountAnalytic = new Analytic("Total Time", minuteString + ":" + secondString);
        }
        cursor.close();
        return wordCountAnalytic;
    }

    public Analytic getTotalUnfocusedTimeAnalyticForProject(String projectId) {
        Cursor cursor = getReadableDatabase().query(PROJECTS_STATS_TABLE,
                new String[]{KEY_TOTAL_UNFOCUSED_TIME},
                KEY_PROJECT_ID + "=?",
                new String[]{projectId},
                null, null, null);
        Analytic wordCountAnalytic = new Analytic("Total Unfocused Time", "0:00");
        if (cursor.moveToNext()) {
            int totalSeconds = cursor.getInt(cursor.getColumnIndex(KEY_TOTAL_UNFOCUSED_TIME));
            int minutes = totalSeconds / 60;
            int seconds = totalSeconds % 60;
            String minuteString = String.valueOf(minutes);
            String secondString = String.format("%02d", seconds);
            wordCountAnalytic = new Analytic("Total Unfocused Time", minuteString + ":" + secondString);
        }
        cursor.close();
        return wordCountAnalytic;
    }

    private int calculateAverage(int sum, int numOfOccurrences) {
        int average = 0;
        if (numOfOccurrences != 0) {
            average = sum / numOfOccurrences;
        }
        return average;
    }
}

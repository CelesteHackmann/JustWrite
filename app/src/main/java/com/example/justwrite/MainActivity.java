package com.example.justwrite;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    NumberPicker mMinuteText;
    NumberPicker mSecondText;
    Spinner mSpinnerProjects;
    ArrayAdapter<Project> arrayAdapter;
    ArrayList<Project> projects = new ArrayList<>();
    int currentProjectPosition = 0;
    private static final int RESULT_CREATE_PROJECT = 1;
    private static final int RESULT_SPRINT_OVER = 2;
    private Project defaultProject = new Project("Select a Project", "Undefined");

    boolean TESTING_ON = false;
    HashMap<Project, Long> projectToId = new HashMap();
    private DatabaseHelper mDB;
    private SQLiteDatabase mWriteableDatabase;
    private long currentProjectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMinuteText = findViewById(R.id.editMinutes);
        mSecondText = findViewById(R.id.editSeconds);
        mSpinnerProjects = findViewById(R.id.spinnerProjects);
        setupNumberPickers();

        projects.add(defaultProject);
        projectToId.put(defaultProject, null);

        if (TESTING_ON) {
            setUpForTesting();
        }

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, projects);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinnerProjects.setAdapter(arrayAdapter);
        mSpinnerProjects.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentProjectPosition = position;
                Project selected = projects.get(position);
                currentProjectId = selected.getId();
            }
            @Override
            public void onNothingSelected(AdapterView <?> parent) {
            }
        });

        mDB = new DatabaseHelper(this);
        mWriteableDatabase = mDB.getWritableDatabase();
    }

    public void startSprint(View view) {
        hideKeyboard();
        if (mSpinnerProjects.getSelectedItem() == defaultProject) {
            mSpinnerProjects.getSelectedView().setBackgroundResource(R.color.warningColor);
        }
        else {
            int minutes = mMinuteText.getValue();
            int seconds = mSecondText.getValue();
            Intent intent = new Intent(this,Countdown.class);
            intent.putExtra("minutes", minutes);
            intent.putExtra("seconds", seconds);
            startActivityForResult(intent, RESULT_SPRINT_OVER);
        }
    }

    public void createProject(View view) {
        Intent intent = new Intent(getApplicationContext(), CreateProject.class);
        startActivityForResult(intent, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if (requestCode == RESULT_CREATE_PROJECT) {
                String pName = data.getStringExtra("name");
                String pGenre = data.getStringExtra("genre");

                // Add Project to Database
                ContentValues projectValues = new ContentValues();
                projectValues.put(DatabaseHelper.KEY_TITLE, pName);
                projectValues.put(DatabaseHelper.KEY_GENRE, pGenre);
                long newRowId = mWriteableDatabase.insert(DatabaseHelper.PROJECTS_TABLE, null, projectValues);

                currentProjectId = newRowId;

                // Update Listing in Spinner
                Project project = new Project(pName, pGenre, newRowId);
                projects.add(project);
                arrayAdapter.notifyDataSetChanged();
                currentProjectPosition = arrayAdapter.getPosition(project);
                mSpinnerProjects.setSelection(currentProjectPosition);
                
                // Set Up Project Stats
                ContentValues statsValues = new ContentValues();
                statsValues.put(DatabaseHelper.KEY_PROJECT_ID, newRowId);
                statsValues.put(DatabaseHelper.KEY_TOTAL_TIME, 0);
                statsValues.put(DatabaseHelper.KEY_TOTAL_WORDS, 0);
                statsValues.put(DatabaseHelper.KEY_TOTAL_UNFOCUSED_TIME, 0);
                mWriteableDatabase.insert(DatabaseHelper.PROJECTS_STATS_TABLE, null, statsValues);
            }
            if (requestCode == RESULT_SPRINT_OVER) {
                int sprintTime = data.getIntExtra("sprint time", 0);
                int unfocusedTime = data.getIntExtra("unfocused time", 0);
                int wordCount = data.getIntExtra("words written", 0);
                Sprint sprint = new Sprint(sprintTime, unfocusedTime, wordCount);
                projects.get(currentProjectPosition).addSprint(sprint);

                ContentValues sprintValues = new ContentValues();
                sprintValues.put(DatabaseHelper.KEY_SPRINT_TIME, sprintTime);
                sprintValues.put(DatabaseHelper.KEY_WORD_COUNT, wordCount);
                sprintValues.put(DatabaseHelper.KEY_UNFOCUSED_TIME, unfocusedTime);
                sprintValues.put(DatabaseHelper.KEY_PROJECT_ID, currentProjectId);
                mWriteableDatabase.insert(DatabaseHelper.SPRINTS_TABLE, null, sprintValues);
                // UPDATE PROJECT STATS
                int newWordCount = wordCount + mDB.getTotalWordCount(currentProjectId);
                int newTotalTime = sprintTime + mDB.getTotalTime(currentProjectId);
                int newTotalUnfocusedTime = unfocusedTime + mDB.getTotalUnfocusedTime(currentProjectId);

                ContentValues statsValues = new ContentValues();
                statsValues.put(DatabaseHelper.KEY_TOTAL_WORDS, newWordCount);
                statsValues.put(DatabaseHelper.KEY_TOTAL_TIME, newTotalTime);
                statsValues.put(DatabaseHelper.KEY_TOTAL_UNFOCUSED_TIME, newTotalUnfocusedTime);

                String selection = DatabaseHelper.KEY_PROJECT_ID + " LIKE ?";
                String[] selectionArgs = {String.valueOf(currentProjectId)};
                mWriteableDatabase.update(DatabaseHelper.PROJECTS_STATS_TABLE, statsValues, selection, selectionArgs);
            }
        }
    }

    public void seeSprintLog(View view) {
        Intent intent = new Intent(this, SprintHistory.class);
        intent.putExtra("projects", projects);
        startActivity(intent);
    }

    private void setupNumberPickers() {
        mMinuteText.setMinValue(0);
        mSecondText.setMinValue(0);
        mMinuteText.setMaxValue(59);
        mSecondText.setMaxValue(59);
        mMinuteText.setValue(15);
        mSecondText.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%02d", i);
            }
        });
    }

    private void setUpForTesting() {
        Project testP1 = new Project("Breaking Trust", "Young Adult");
        Project testP2 = new Project("Start of Everything New", "Contemporary");
        testP1.addSprint(new Sprint(120,0, 120));
        testP1.addSprint(new Sprint(1800, 54, 564));
        testP2.addSprint(new Sprint(320, 24, 300));
        testP2.addSprint(new Sprint(1200, 67, 153));
        projects.add(testP1);
        projects.add(testP2);
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
package com.example.justwrite;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    NumberPicker mMinuteText;
    NumberPicker mSecondText;
    Spinner mSpinnerProjects;
    ArrayAdapter<Project> arrayAdapter;
    ArrayList<Project> projectAndIds = new ArrayList<>();
    private static final int RESULT_CREATE_PROJECT = 1;
    private static final int RESULT_SPRINT_OVER = 2;
    private Project defaultProjectAndId = new Project("Select a Project","Undefined", -1);

    private DatabaseHelper mDB;
    private long currentProjectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMinuteText = findViewById(R.id.editMinutes);
        mSecondText = findViewById(R.id.editSeconds);
        mSpinnerProjects = findViewById(R.id.spinnerProjects);
        setupNumberPickers();

        mDB = DatabaseHelper.getInstance(this);
        setUpProjectSpinner();

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, projectAndIds);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinnerProjects.setAdapter(arrayAdapter);
        mSpinnerProjects.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Project selected = projectAndIds.get(position);
                currentProjectId = selected.getId();
            }
            @Override
            public void onNothingSelected(AdapterView <?> parent) {
            }
        });

    }

    public void startSprint(View view) {
        hideKeyboard();
        if (mSpinnerProjects.getSelectedItem() == defaultProjectAndId) {
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
                currentProjectId = mDB.insertProject(pName, pGenre);;
                // Set Up Project Stats for new Project
                mDB.insertProjectStats(currentProjectId);

                // Create project for spinner
                Project project = new Project(pName, pGenre, currentProjectId);
                projectAndIds.add(project);
                arrayAdapter.notifyDataSetChanged();
                mSpinnerProjects.setSelection(arrayAdapter.getPosition(project));

            }
            if (requestCode == RESULT_SPRINT_OVER) {
                //Add Sprint and Project Stats Tables
                int sprintTime = data.getIntExtra("sprint time", 0);
                int unfocusedTime = data.getIntExtra("unfocused time", 0);
                int wordCount = data.getIntExtra("words written", 0);
                mDB.addSprint(sprintTime, unfocusedTime, wordCount, currentProjectId);
                mDB.updateProjectStats(sprintTime, unfocusedTime, wordCount, currentProjectId);
            }
        }
    }

    public void seeSprintLog(View view) {
        Intent intent = new Intent(this, SprintHistory.class);
        intent.putExtra("projects", projectAndIds);
        startActivity(intent);
    }

    private void setUpProjectSpinner() {
        projectAndIds.add(defaultProjectAndId);
        Cursor cursor = mDB.getReadableDatabase().query(DatabaseHelper.PROJECTS_TABLE,
                null,null, null, null, null, null);
        while (cursor.moveToNext()) {
            projectAndIds.add(new Project(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_TITLE)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_GENRE)),
                    cursor.getLong(cursor.getColumnIndex(DatabaseHelper.KEY_PROJECT_ID))));
        }
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

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
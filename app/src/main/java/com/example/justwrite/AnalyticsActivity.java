package com.example.justwrite;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedList;

public class AnalyticsActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private AnalyticListAdapter mAdapter;
    private Spinner mSpinnerProjects;
    private DatabaseHelper mDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_and_analytics);
        mDB = DatabaseHelper.getInstance(this);

        Intent intent = getIntent();
        // TODO GET SPRINT LIST FROM THE DATABASE
        ArrayList<Project> projects = intent.getParcelableArrayListExtra("projects");
        if (projects != null) {
            ArrayAdapter<Project> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, projects);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSpinnerProjects = findViewById(R.id.selectProject);
            mSpinnerProjects.setAdapter(arrayAdapter);
            mSpinnerProjects.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // GET BASIC STATS FOR THAT PROJECT
                    Project selectedProject = (Project) mSpinnerProjects.getSelectedItem();
                    LinkedList<Analytic> analyticsList = new LinkedList<>();
                    Cursor cursor = mDB.getReadableDatabase().query(DatabaseHelper.PROJECTS_STATS_TABLE,
                            null,
                            DatabaseHelper.KEY_PROJECT_ID + "=?",
                            new String[] {String.valueOf(selectedProject.getId())},
                            null, null, null);
                    Log.d("HELP_HERE", "LINE 50");
                    while (cursor.moveToNext()){
                        analyticsList.addLast(new Analytic("Total Words: ", cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_TOTAL_WORDS))));
                        analyticsList.addLast(new Analytic("Total Time: ", cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_TOTAL_TIME))));
                        analyticsList.addLast(new Analytic("Total Unfocused Time: ", cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_TOTAL_UNFOCUSED_TIME))));
                    }
                    Log.d("HELP_HERE", "LINE 54");
                    mRecyclerView = findViewById(R.id.recyclerview);
                    mAdapter = new AnalyticListAdapter(getApplicationContext(), analyticsList);
                    mRecyclerView.setAdapter(mAdapter);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                }
                @Override
                public void onNothingSelected(AdapterView <?> parent) {
                }
            });
        }
    }
}
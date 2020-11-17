package com.example.justwrite;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

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
    private TextView mProjectName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_and_analytics);
        mDB = DatabaseHelper.getInstance(this);
        mProjectName = findViewById(R.id.selected_project_name);
        mProjectName.setText("No Project Data To Show");

        ArrayList<Project> projects = mDB.getProjectList();
        if (projects != null) {
            setupProjectSpinner(projects);
        }
    }

    private void setupProjectSpinner(ArrayList<Project> projects) {
        ArrayAdapter<Project> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, projects);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerProjects = findViewById(R.id.selectProject);
        mSpinnerProjects.setAdapter(arrayAdapter);
        mSpinnerProjects.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // GET BASIC STATS FOR THAT PROJECT
                Project selectedProject = (Project) mSpinnerProjects.getSelectedItem();
                String selectedProjectId = String.valueOf(selectedProject.getId());
                LinkedList<Analytic> analyticsList = mDB.getBasicAnalyticsList(selectedProjectId);

                // GET ADVANCED STATS FOR THAT PROJECT
                analyticsList.add(mDB.getWordsPerMinuteAnalyticForProject(selectedProjectId));
                analyticsList.add(mDB.getAverageWordsPer30MinAnalyticForProject(selectedProjectId));
                analyticsList.add(mDB.getAverageWordsPerSprintForProject(selectedProjectId));
                analyticsList.add(mDB.getAverageSprintTimeForProject(selectedProjectId));

                // POPULATE RECYCLER VIEW
                mRecyclerView = findViewById(R.id.recyclerview);
                mAdapter = new AnalyticListAdapter(getApplicationContext(), analyticsList);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                mProjectName.setText(selectedProject.getTitle());
            }
            @Override
            public void onNothingSelected(AdapterView <?> parent) {
            }
        });
    }
}
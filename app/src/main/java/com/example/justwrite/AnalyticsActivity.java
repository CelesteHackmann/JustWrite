package com.example.justwrite;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedList;

public class AnalyticsActivity extends BaseActivity {
    private RecyclerView mRecyclerView;
    private AnalyticListAdapter mAdapter;
    private Spinner mSpinnerProjects;
    private DatabaseHelper mDB;
    private TextView mProjectName;
    private SharedPreferences mSharedPreferences;
    private String sharedPrefFile = "com.example.justwrite";

    private static final String KEY_WORD_COUNT = "Word Count";
    private static final String KEY_TIME = "Time";
    private static final String KEY_UNFOCUSED_TIME = "Unfocused Time";
    private static final String KEY_WORDS_PER_MIN = "Words Per Minute";
    private static final String KEY_WORDS_PER_30_MIN = "Words Per 30 Minutes";
    private static final String KEY_WORDS_PER_SPRINT = "Words Per Sprint";
    private static final String KEY_AVG_SPRINT_TIME = "Time Per Sprint";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_and_analytics);
        mDB = DatabaseHelper.getInstance(this);
        mSharedPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        mProjectName = findViewById(R.id.selected_project_name);
        mProjectName.setText(R.string.no_project_to_show_string);

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
                LinkedList<Analytic> analyticsList = restoreAnalyticPreferences(selectedProjectId);

                // POPULATE RECYCLER VIEW
                mRecyclerView = findViewById(R.id.LogRecyclerView);
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

    private LinkedList<Analytic> restoreAnalyticPreferences(String selectedProjectId) {
        mSharedPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        LinkedList<Analytic> analyticsList = new LinkedList<>();
        if (mSharedPreferences.getBoolean(KEY_WORD_COUNT, true)) {
            analyticsList.add(mDB.getTotalWordCountAnalyticForProject(selectedProjectId));
        }
        if (mSharedPreferences.getBoolean(KEY_TIME, true)) {
            analyticsList.add(mDB.getTotalTimeAnalyticForProject(selectedProjectId));
        }
        if (mSharedPreferences.getBoolean(KEY_UNFOCUSED_TIME, true)) {
            analyticsList.add(mDB.getTotalUnfocusedTimeAnalyticForProject(selectedProjectId));
        }
        if (mSharedPreferences.getBoolean(KEY_WORDS_PER_MIN, true)) {
            analyticsList.add(mDB.getWordsPerMinuteAnalyticForProject(selectedProjectId));
        }
        if (mSharedPreferences.getBoolean(KEY_WORDS_PER_30_MIN, true)) {
            analyticsList.add(mDB.getAverageWordsPer30MinAnalyticForProject(selectedProjectId));
        }
        if (mSharedPreferences.getBoolean(KEY_WORDS_PER_SPRINT, true)) {
            analyticsList.add(mDB.getAverageWordsPerSprintForProject(selectedProjectId));
        }
        if (mSharedPreferences.getBoolean(KEY_AVG_SPRINT_TIME, true)) {
            analyticsList.add(mDB.getAverageSprintTimeForProject(selectedProjectId));
        }
        return analyticsList;
    }
}
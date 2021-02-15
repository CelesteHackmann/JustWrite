package com.example.justwrite.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.justwrite.R;
import com.example.justwrite.adapters.AnalyticListAdapter;
import com.example.justwrite.classes.Analytic;
import com.example.justwrite.classes.DatabaseHelper;
import com.example.justwrite.classes.Project;

import java.util.ArrayList;
import java.util.LinkedList;

import static android.content.Context.MODE_PRIVATE;

public class AnalyticsFragment extends Fragment {
    private RecyclerView mRecyclerView;
    public AnalyticListAdapter mAdapter;
    private Spinner mSpinnerProjects;
    private DatabaseHelper mDB;
    private TextView mProjectName;
    private SharedPreferences mSharedPreferences;
    private View mView;
    ArrayList<Project> projects;
    LinkedList<Analytic> analyticsList;

    private static final String KEY_WORD_COUNT = "Word Count";
    private static final String KEY_TIME = "Time";
    private static final String KEY_UNFOCUSED_TIME = "Unfocused Time";
    private static final String KEY_WORDS_PER_MIN = "Words Per Minute";
    private static final String KEY_WORDS_PER_30_MIN = "Words Per 30 Minutes";
    private static final String KEY_WORDS_PER_SPRINT = "Words Per Sprint";
    private static final String KEY_AVG_SPRINT_TIME = "Time Per Sprint";

    public AnalyticsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_history_and_analytics, container, false);
        mDB = DatabaseHelper.getInstance(getActivity());
        mSharedPreferences = getActivity().getSharedPreferences(getString(R.string.sharedPrefFileName), MODE_PRIVATE);
        mProjectName = mView.findViewById(R.id.selected_project_name);

        projects = mDB.getUnarchivedProjectList();
        if (projects.size() > 0) {
            setupProjectSpinner(projects);
        }
        else {
            mProjectName.setText(R.string.no_project_to_show_string);
        }
        return mView;
    }

    private void setupProjectSpinner(ArrayList<Project> projects) {
        ArrayAdapter<Project> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, projects);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerProjects = mView.findViewById(R.id.selectProject);
        mSpinnerProjects.setAdapter(arrayAdapter);
        mSpinnerProjects.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // GET BASIC STATS FOR THAT PROJECT
                Project selectedProject = (Project) mSpinnerProjects.getSelectedItem();
                String selectedProjectId = String.valueOf(selectedProject.getId());
                analyticsList = restoreAnalyticPreferences(selectedProjectId);

                // POPULATE RECYCLER VIEW
                mRecyclerView = mView.findViewById(R.id.LogRecyclerView);
                mAdapter = new AnalyticListAdapter(getContext(), analyticsList);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                mProjectName.setText(selectedProject.getTitle());
            }
            @Override
            public void onNothingSelected(AdapterView <?> parent) {
            }
        });
    }

    private LinkedList<Analytic> restoreAnalyticPreferences(String selectedProjectId) {
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
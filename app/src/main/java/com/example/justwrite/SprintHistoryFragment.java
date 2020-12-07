package com.example.justwrite;

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

import java.util.ArrayList;
import java.util.LinkedList;

public class SprintHistoryFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private Spinner mSpinnerProjects;
    private TextView mProjectName;
    private DatabaseHelper mDB;
    private View mView;
    SprintListAdapter mAdapter;
    ArrayList<Project> projects;
    LinkedList<Sprint> sprintList;

    public SprintHistoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)  {
        mView = inflater.inflate(R.layout.fragment_history_and_analytics, container, false);
        mDB = DatabaseHelper.getInstance(getContext());
        mProjectName = mView.findViewById(R.id.selected_project_name);

        projects = mDB.getProjectList();
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
                // GET SPRINTS RELATED TO PROJECT
                Project selectedProject = (Project) mSpinnerProjects.getSelectedItem();
                String selectedProjectId = String.valueOf(selectedProject.getId());
                sprintList = mDB.getSprintsForProject(selectedProjectId);

                // POPULATE RECYCLER VIEW
                mRecyclerView = mView.findViewById(R.id.LogRecyclerView);
                mAdapter = new SprintListAdapter(getContext(), sprintList);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                mProjectName.setText(selectedProject.getTitle());
            }
            @Override
            public void onNothingSelected(AdapterView <?> parent) {
            }
        });
    }
}
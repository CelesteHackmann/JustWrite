package com.example.justwrite;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.LinkedList;

public class SprintHistory extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private SprintListAdapter mAdapter;
    private Spinner mSpinnerProjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sprint_history);

        Intent intent = getIntent();
        ArrayList<Project> projects = intent.getParcelableArrayListExtra("projects");
        if (projects != null) {
            ArrayAdapter<Project> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, projects);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSpinnerProjects = findViewById(R.id.selectProject);
            mSpinnerProjects.setAdapter(arrayAdapter);
            mSpinnerProjects.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Project selectedProject = (Project) mSpinnerProjects.getSelectedItem();
                    LinkedList<Sprint> sprintList = new LinkedList<>();
                    for (Sprint sprint:selectedProject.getSprints()) {
                        sprintList.addFirst(sprint);
                    }
                    mRecyclerView = findViewById(R.id.recyclerview);
                    mAdapter = new SprintListAdapter(getApplicationContext(), sprintList);
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
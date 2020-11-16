package com.example.justwrite;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedList;

public class SprintHistory extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private SprintListAdapter mAdapter;
    private Spinner mSpinnerProjects;
    private DatabaseHelper mDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_and_analytics);
        mDB = DatabaseHelper.getInstance(this);

        ArrayList<Project> projects = mDB.getProjectList();
        if (projects != null) {
            ArrayAdapter<Project> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, projects);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSpinnerProjects = findViewById(R.id.selectProject);
            mSpinnerProjects.setAdapter(arrayAdapter);
            mSpinnerProjects.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // GET SPRINTS RELATED TO PROJECT
                    Project selectedProject = (Project) mSpinnerProjects.getSelectedItem();
                    LinkedList<Sprint> sprintList = new LinkedList<>();
                    Cursor cursor = mDB.getReadableDatabase().query(DatabaseHelper.SPRINTS_TABLE,
                            null,
                            DatabaseHelper.KEY_PROJECT_ID + "=?",
                            new String[] {String.valueOf(selectedProject.getId())},
                            null, null, null);
                    while (cursor.moveToNext()) {
                        sprintList.addFirst(new Sprint(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_SPRINT_TIME)),
                                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_UNFOCUSED_TIME)),
                                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_WORD_COUNT))));
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
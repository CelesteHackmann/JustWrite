package com.example.justwrite;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class EditProjectsActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    EditProjectAdapter mAdapter;
    DatabaseHelper mDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_projects);
        mDB = DatabaseHelper.getInstance(this);
        ArrayList<Project> projectList = mDB.getProjectList();

        mRecyclerView = findViewById(R.id.editProject_recyclerView);
        mAdapter = new EditProjectAdapter(getApplicationContext(), projectList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    @Override
    public void supportNavigateUpTo(@NonNull Intent upIntent) {
        setResult(RESULT_OK, upIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
}
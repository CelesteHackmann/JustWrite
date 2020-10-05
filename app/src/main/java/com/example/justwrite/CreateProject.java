package com.example.justwrite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class CreateProject extends AppCompatActivity {
    EditText mProjectName;
    EditText mGenre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);

        mProjectName = findViewById(R.id.editProjectName);
        mGenre = findViewById(R.id.editGenre);
    }


    public void createProject(View view) {
        String pName = String.valueOf(mProjectName.getText());
        String pGenre = String.valueOf(mGenre.getText());
        Intent intent = new Intent();
        intent.putExtra("name", pName);
        intent.putExtra("genre", pGenre);
        setResult(RESULT_OK, intent);
        finish();
    }
}
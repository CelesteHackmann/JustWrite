package com.example.justwrite;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends BaseActivity {
    NumberPicker mMinuteText;
    NumberPicker mSecondText;
    Spinner mSpinnerProjects;
    ArrayAdapter<Project> arrayAdapter;
    ArrayList<Project> projectAndIds = new ArrayList<>();
    private static final int RESULT_SPRINT_OVER = 2;
    private final Project defaultProjectAndId = new Project("Select a Project","Undefined", "-1");

    private DatabaseHelper mDB;
    private String currentProjectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMinuteText = findViewById(R.id.editMinutes);
        mSecondText = findViewById(R.id.editSeconds);
        mSpinnerProjects = findViewById(R.id.spinnerProjects);
        mDB = DatabaseHelper.getInstance(this);
        setupNumberPickers();
        setUpProjectSpinner();
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
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        final View layout = inflater.inflate(R.layout.create_project_dialog, null);
        final EditText projectName = layout.findViewById(R.id.editProjectName);
        final EditText projectGenre = layout.findViewById(R.id.editGenre);

        final AlertDialog alert = getCreateProjectDialog(layout, projectName, projectGenre);
        projectName.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }
                else {
                    alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        alert.show();
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if (requestCode == RESULT_SPRINT_OVER) {
                //Add Sprint and Project Stats Tables
                int sprintTime = data.getIntExtra("sprint time", 0);
                int unfocusedTime = data.getIntExtra("unfocused time", 0);
                int wordCount = data.getIntExtra("words written", 0);
                Date sprintDate = (Date) data.getSerializableExtra("sprint date");
                Sprint sprint = new Sprint(sprintTime, unfocusedTime, wordCount, sprintDate);
                mDB.addSprint(sprint, String.valueOf(currentProjectId));
                mDB.updateProjectStats(sprintTime, unfocusedTime, wordCount, currentProjectId);
            }
        }
    }

    public void seeSprintLog(View view) {
        Intent intent = new Intent(this, SprintHistory.class);
        intent.putExtra("projects", projectAndIds);
        startActivity(intent);
    }

    public void seeProjectAnalytics(View view) {
        Intent intent = new Intent(this, AnalyticsActivity.class);
        intent.putExtra("projects", projectAndIds);
        startActivity(intent);
    }

    private void addProject(String pName, String pGenre) {
        // Add Project to Database
        currentProjectId = String.valueOf(mDB.insertProject(pName, pGenre));

        // Set Up Project Stats for new Project
        mDB.insertProjectStats(currentProjectId);

        Project project = new Project(pName, pGenre, currentProjectId);
        // Create project for spinner
        projectAndIds.add(project);
        arrayAdapter.notifyDataSetChanged();
        mSpinnerProjects.setSelection(arrayAdapter.getPosition(project));
    }

    private void setUpProjectSpinner() {
        projectAndIds = mDB.getProjectList();
        projectAndIds.add(0, defaultProjectAndId);
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

    private void setupNumberPickers() {
        mMinuteText.setMinValue(0);
        mMinuteText.setMaxValue(59);
        mSecondText.setMinValue(0);
        mSecondText.setMaxValue(59);
        mMinuteText.setValue(15);
        mSecondText.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%02d", i);
            }
        });
    }

    private AlertDialog getCreateProjectDialog(View layout, final EditText projectName, final EditText projectGenre) {
        return new AlertDialog.Builder(MainActivity.this)
                .setTitle("Create a New Project")
                .setView(layout)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String titleString = projectName.getText().toString();
                        String genreString = projectGenre.getText().toString();
                        addProject(titleString, genreString);
                    }
                })
                .setCancelable(true)
                .create();
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
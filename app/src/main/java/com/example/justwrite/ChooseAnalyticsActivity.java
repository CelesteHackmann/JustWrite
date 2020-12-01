package com.example.justwrite;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ChooseAnalyticsActivity extends AppCompatActivity {
    private SharedPreferences mSharedPreferences;
    private String sharedPrefFile = "com.example.justwrite";

    CheckBox wordCountCheckbox;
    CheckBox sprintTimeCheckbox;
    CheckBox unfocusedTimeCheckbox;
    CheckBox wordsPerMinCheckbox;
    CheckBox wordsPer30MinCheckbox;
    CheckBox wordsPerSprintCheckbox;
    CheckBox avgSprintTimeCheckbox;
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
        setContentView(R.layout.activity_choose_analytics);

        mSharedPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        assignCheckboxItems();
        setUpCheckboxes();
    }

    private void assignCheckboxItems() {
        wordCountCheckbox = findViewById(R.id.checkBoxWordCount);
        sprintTimeCheckbox = findViewById(R.id.checkBoxTime);
        unfocusedTimeCheckbox = findViewById(R.id.checkBoxUnfocusedTime);
        wordsPerMinCheckbox = findViewById(R.id.checkBoxWordsPerMin);
        wordsPer30MinCheckbox = findViewById(R.id.checkBoxWordsPer30Min);
        wordsPerSprintCheckbox = findViewById(R.id.checkBoxWordsPerSprint);
        avgSprintTimeCheckbox = findViewById(R.id.checkBoxAvgSprintTime);
    }


    private void setUpCheckboxes() {
        wordCountCheckbox.setChecked(mSharedPreferences.getBoolean(KEY_WORD_COUNT, true));
        sprintTimeCheckbox.setChecked(mSharedPreferences.getBoolean(KEY_TIME, true));
        unfocusedTimeCheckbox.setChecked(mSharedPreferences.getBoolean(KEY_UNFOCUSED_TIME, true));
        wordsPerMinCheckbox.setChecked(mSharedPreferences.getBoolean(KEY_WORDS_PER_MIN, true));
        wordsPer30MinCheckbox.setChecked(mSharedPreferences.getBoolean(KEY_WORDS_PER_30_MIN, true));
        wordsPerSprintCheckbox.setChecked(mSharedPreferences.getBoolean(KEY_WORDS_PER_SPRINT, true));
        avgSprintTimeCheckbox.setChecked(mSharedPreferences.getBoolean(KEY_AVG_SPRINT_TIME, true));
    }

    public void savePreferences(View view) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(KEY_WORD_COUNT, wordCountCheckbox.isChecked());
        editor.putBoolean(KEY_TIME, sprintTimeCheckbox.isChecked());
        editor.putBoolean(KEY_UNFOCUSED_TIME, unfocusedTimeCheckbox.isChecked());
        editor.putBoolean(KEY_WORDS_PER_MIN, wordsPerMinCheckbox.isChecked());
        editor.putBoolean(KEY_WORDS_PER_30_MIN, wordsPer30MinCheckbox.isChecked());
        editor.putBoolean(KEY_WORDS_PER_SPRINT, wordsPerSprintCheckbox.isChecked());
        editor.putBoolean(KEY_AVG_SPRINT_TIME, avgSprintTimeCheckbox.isChecked());
        editor.apply();
        Toast.makeText(this, "Analytic Preferences Saved!", Toast.LENGTH_SHORT).show();
    }
}
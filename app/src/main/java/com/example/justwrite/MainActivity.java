package com.example.justwrite;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.NumberPicker;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    NumberPicker mMinuteText;
    NumberPicker mSecondText;
//    private String JUST_WRITE_PACKAGE = "com.example.justwrite";
//    private String[] APP_PACKAGES = {JUST_WRITE_PACKAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMinuteText = findViewById(R.id.editMinutes);
        mSecondText = findViewById(R.id.editSeconds);
        setupNumberPickers();
    }

    public void startSprint(View view) {
        hideKeyboard();
        int minutes = getMinutes();
        int seconds = getSeconds();
        Intent intent = new Intent(this,Countdown.class);
        intent.putExtra("minutes", minutes);
        intent.putExtra("seconds", seconds);
        startActivity(intent);
    }

    private int getMinutes() {
        try {
            return Integer.parseInt(String.valueOf(mMinuteText.getValue()));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private int getSeconds() {
        try {
            return Integer.parseInt(String.valueOf(mSecondText.getValue()));
        } catch (NumberFormatException e) {
            return 0;
        }
    }


    private void setupNumberPickers() {
        mMinuteText.setMinValue(0);
        mSecondText.setMinValue(0);
        mMinuteText.setMaxValue(59);
        mSecondText.setMaxValue(59);
        mMinuteText.setValue(15);
        mSecondText.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%02d", i);
            }
        });
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
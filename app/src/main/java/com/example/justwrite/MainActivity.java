package com.example.justwrite;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    NumberPicker mMinuteText;
    NumberPicker mSecondText;
    TextView mTimerText;
    Button mStartButton;
    Button mEndButton;
    LinearLayout mTimerSetup;
    CountDownTimer mTimer;
    private String FORMAT = "%02d:%02d";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMinuteText = findViewById(R.id.editMinutes);
        mSecondText = findViewById(R.id.editSeconds);
        mStartButton = findViewById(R.id.startButton);
        mEndButton = findViewById(R.id.endButton);
        mTimerText = findViewById(R.id.countdownText);
        mTimerSetup = findViewById(R.id.TimerSetup);
        setupNumberPickers();
    }


    public void startSprint(View view) {
        hideKeyboard();
        int minutes = getMinutes();
        int seconds = getSeconds();
        final int numInSeconds = minutes * 60 + seconds;
        startTimerVisibility();
        mTimer = new CountDownTimer(numInSeconds * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long remainingSeconds = millisUntilFinished / 1000;
                int minutesLeft = (int) (remainingSeconds/60);
                int secondsLeft = ((int) (remainingSeconds%60));
                mTimerText.setText(String.format(FORMAT, minutesLeft, secondsLeft));
            }

            @Override
            public void onFinish() {
                resetVisibility();
                Toast toast = Toast.makeText(getApplicationContext(), "you sprinted for " +
                        numInSeconds/60 + " minutes " + numInSeconds%60 + " seconds", Toast.LENGTH_SHORT);
                toast.show();
            }
        };
        mTimer.start();
    }

    public void endSprint(View view) {
        mTimer.cancel();
        resetVisibility();
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

    private void startTimerVisibility() {
        mTimerSetup.setVisibility(View.INVISIBLE);
        mStartButton.setVisibility(View.INVISIBLE);
        mTimerText.setVisibility(View.VISIBLE);
        mEndButton.setVisibility(View.VISIBLE);
    }

    private void resetVisibility() {
        mTimerSetup.setVisibility(View.VISIBLE);
        mStartButton.setVisibility(View.VISIBLE);
        mTimerText.setVisibility(View.GONE);
        mEndButton.setVisibility(View.GONE);
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
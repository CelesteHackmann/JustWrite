package com.example.justwrite;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Countdown extends AppCompatActivity {
    TextView mTimerText;
    CountDownTimer mTimer;
    private final String FORMAT = "%02d:%02d";
    private boolean mTimerGoing = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown);
        mTimerText = findViewById(R.id.countdownText);

        Intent intent = getIntent();
        int minutes = intent.getIntExtra("minutes",0);
        int seconds = intent.getIntExtra("seconds", 0);
        startTimer(minutes, seconds);
    }

    private void startTimer(int minutes, int seconds) {
        final int numInSeconds = minutes * 60 + seconds;
        mTimer = new CountDownTimer(numInSeconds *1000, 1000) {
            @Override
            public void onTick ( long millisUntilFinished){
                long remainingSeconds = millisUntilFinished / 1000;
                int minutesLeft = (int) (remainingSeconds / 60);
                int secondsLeft = ((int) (remainingSeconds % 60));
                mTimerText.setText(String.format(FORMAT, minutesLeft, secondsLeft));
            }

            @Override
            public void onFinish () {
                Toast toast = Toast.makeText(getApplicationContext(), "you sprinted for " +
                        numInSeconds / 60 + " minutes " + numInSeconds % 60 + " seconds", Toast.LENGTH_SHORT);
                toast.show();
                mTimerGoing = false;
            }
        };
        mTimer.start();
        mTimerGoing = true;
    }

    public void endSprint(View view) {
        if (mTimerGoing) {
            mTimer.cancel();
            mTimerGoing = false;
        }
        finish();
    }
}
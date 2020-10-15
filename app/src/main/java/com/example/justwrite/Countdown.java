package com.example.justwrite;

import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Countdown extends AppCompatActivity {
    TextView mTimerText;
    CountDownTimer mTimer;
    KeyguardManager myKM;
    int mUnfocusedTime = -1;
    private final String FORMAT = "%02d:%02d";
    private boolean mTimerGoing = false;
    private boolean mPopupView = false;
    AlertDialog alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown);
        mTimerText = findViewById(R.id.countdownText);
        myKM = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
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
                if (!hasWindowFocus() && !myKM.isDeviceLocked() && !mPopupView) {
                    mUnfocusedTime++;
                    Log.d("time", String.valueOf(mUnfocusedTime));
                }
                long remainingSeconds = millisUntilFinished / 1000;
                int minutesLeft = (int) (remainingSeconds / 60);
                int secondsLeft = (int) (remainingSeconds % 60);
                mTimerText.setText(String.format(FORMAT, minutesLeft, secondsLeft));
            }

            @Override
            public void onFinish () {
                mTimerGoing = false;
                LayoutInflater inflater = LayoutInflater.from(Countdown.this);
                final View layout = inflater.inflate(R.layout.sprint_finished, null);
                final EditText wordsWrittenText = (EditText) layout.findViewById(R.id.editTextNumber);

                alert = new AlertDialog.Builder(Countdown.this)
                        .setTitle("Congratulations")
                        .setView(layout)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String wordsString = wordsWrittenText.getText().toString();
                                int wordCount = Integer.parseInt(wordsString);
                                Intent intent = new Intent();
                                intent.putExtra("sprint time", numInSeconds);
                                intent.putExtra("unfocused time", mUnfocusedTime);
                                intent.putExtra("words written", wordCount);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        })
                        .setMessage("Sprint Time: " +
                        numInSeconds / 60 + " minutes " + numInSeconds % 60 + " seconds\n" +
                        "Unfocused Time: " + mUnfocusedTime+ " seconds")
                        .setCancelable(false)
                        .create();
                wordsWrittenText.addTextChangedListener(new TextWatcher() {
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
        };
        mTimer.start();
        mTimerGoing = true;
    }

    public void endSprint(View view) {
        if (mTimerGoing) {
            AlertDialog alert = new AlertDialog.Builder(Countdown.this).setTitle("End Sprint")
                    .setMessage("Are you sure you want to end the sprint?\nYour progress will not be counted.")
                    .setPositiveButton("End Sprint", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mTimer.cancel();
                            mTimerGoing = false;
                            finish();
                        }
                    })
                    .setNegativeButton("Continue Sprint", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create();
            alert.show();
            mPopupView = true;
        }
    }

    @Override
    public void onBackPressed() {
    }
}
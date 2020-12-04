package com.example.justwrite;

import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import java.util.Calendar;
import java.util.Date;

public class CountdownActivity extends AppCompatActivity {
    TextView mTimerText;
    CountDownTimer mTimer;
    KeyguardManager myKM;
    AlertDialog alert;
    int mUnfocusedTime = 0;
    private boolean appInFocus = true;
    long remainingSeconds;
    int secondsLeft;
    int minutesLeft;
    Date sprintDate;

    private NotificationManager mNotifyManager;
    private static final String CHANNEL_ID = "notification_channel";
    private static final int RETURN_TO_APP_NOTIFICATION_ID = 0;
    private static final int SPRINT_FINISHED_NOTIFICATION_ID = 1;
    private boolean notificationDisplayed;

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

        createNotificationChannel();
    }

    private void startTimer(int minutes, int seconds) {
        final int numInSeconds = minutes * 60 + seconds;
        mTimer = new CountDownTimer(numInSeconds *1000, 1000) {
            @Override
            public void onTick ( long millisUntilFinished){
                if (!appInFocus && !myKM.isKeyguardLocked()) {
                    mUnfocusedTime++;
                    if (!notificationDisplayed){
                        displayReturnToAppNotification();
                    }
                }
                updateTimerText(millisUntilFinished);
            }

            @Override
            public void onFinish () {
                displayTimerFinishedNotification();
                LayoutInflater inflater = LayoutInflater.from(CountdownActivity.this);
                final View layout = inflater.inflate(R.layout.sprint_finished, null);
                final EditText wordsWrittenText = layout.findViewById(R.id.editTextNumber);
                alert = getFinishedAlert(layout, wordsWrittenText, numInSeconds);

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
        sprintDate = Calendar.getInstance().getTime();
        mTimer.start();
    }

    public void cancelSprint(View view) {
        AlertDialog alert = getCancelAlert();
        alert.show();
    }

    private AlertDialog getFinishedAlert(View layout, final EditText wordsWrittenText, final int numInSeconds) {
        return new AlertDialog.Builder(CountdownActivity.this).setTitle("Congratulations")
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
                        intent.putExtra("sprint date", sprintDate);
                        setResult(RESULT_OK, intent);
                        mNotifyManager.cancelAll();
                        finish();
                    }
                })
                .setMessage("Sprint Time: " + numInSeconds / 60 + " minutes " + numInSeconds % 60 +
                        " seconds\n" + "Unfocused Time: " + mUnfocusedTime+ " seconds")
                .setCancelable(false)
                .create();
    }

    private AlertDialog getCancelAlert() {
        return new AlertDialog.Builder(CountdownActivity.this).setTitle("End Sprint")
                .setMessage(R.string.cancelSprintMessage)
                .setPositiveButton("End Sprint", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mTimer.cancel();
                        mNotifyManager.cancelAll();
                        finish();
                    }
                })
                .setNegativeButton("Continue Sprint", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create();
    }

    private void updateTimerText(long millisUntilFinished) {
        remainingSeconds = millisUntilFinished / 1000;
        minutesLeft = (int) (remainingSeconds / 60);
        secondsLeft = (int) (remainingSeconds % 60);
        String FORMAT = "%02d:%02d";
        mTimerText.setText(String.format(FORMAT, minutesLeft, secondsLeft));
    }

    private void displayTimerFinishedNotification() {
        PendingIntent notificationPendingIntent = getReturnToAppIntent(SPRINT_FINISHED_NOTIFICATION_ID);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("SPRINT FINISHED")
                .setContentText("Your sprint is finished.")
                .setSmallIcon(R.drawable.ic_notification_image)
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true);
        mNotifyManager.notify(SPRINT_FINISHED_NOTIFICATION_ID, builder.build());
    }

    private void displayReturnToAppNotification() {
        notificationDisplayed = true;
        PendingIntent notificationPendingIntent = getReturnToAppIntent(RETURN_TO_APP_NOTIFICATION_ID);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("RETURN TO APP")
                .setContentText("Your time is currently unfocused")
                .setSmallIcon(R.drawable.ic_notification_image)
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true);
        mNotifyManager.notify(RETURN_TO_APP_NOTIFICATION_ID, builder.build());
    }

    public void createNotificationChannel() {
        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                    "Just Write Notification", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(false);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification From Just Write");
            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }

    private PendingIntent getReturnToAppIntent(int sprintFinishedNotificationId) {
        Intent returnToAppIntent = new Intent(this, CountdownActivity.class);
        returnToAppIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(this,
                sprintFinishedNotificationId, returnToAppIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    @Override
    protected void onStop() {
        super.onStop();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!myKM.isKeyguardLocked()){
                    appInFocus = false;
                }
            }
        }, 5000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        appInFocus = true;
        notificationDisplayed = false;
        mNotifyManager.cancelAll();
    }

    @Override
    public void onBackPressed() {
    }
}
package com.example.justwrite;

import java.util.Calendar;

public class Sprint {
    int mSprintTimeSeconds;
    int mUnfocusedSeconds;
    Calendar mSprintDate;

    public Sprint(int numInSeconds, int unfocusedTime, Calendar dateTime) {
        mSprintTimeSeconds = numInSeconds;
        mUnfocusedSeconds = unfocusedTime;
        mSprintDate = dateTime;
    }
}

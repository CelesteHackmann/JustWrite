package com.example.justwrite;

import android.os.Parcel;
import android.os.Parcelable;

public class Sprint implements Parcelable {
    int mSprintTimeSeconds;
    int mUnfocusedSeconds;
    int mWordCount;

    public Sprint(int numInSeconds, int unfocusedTime, int wordCount) {
        mSprintTimeSeconds = numInSeconds;
        mUnfocusedSeconds = unfocusedTime;
        mWordCount = wordCount;
    }

    protected Sprint(Parcel in) {
        mSprintTimeSeconds = in.readInt();
        mUnfocusedSeconds = in.readInt();
        mWordCount = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mSprintTimeSeconds);
        dest.writeInt(mUnfocusedSeconds);
        dest.writeInt(mWordCount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Sprint> CREATOR = new Creator<Sprint>() {
        @Override
        public Sprint createFromParcel(Parcel in) {
            return new Sprint(in);
        }

        @Override
        public Sprint[] newArray(int size) {
            return new Sprint[size];
        }
    };

    @Override
    public String toString() {
        int minutes = mSprintTimeSeconds/60;
        int seconds = mSprintTimeSeconds%60;
        int unfocusedMinutes = mUnfocusedSeconds/60;
        int unfocusedSeconds = mUnfocusedSeconds%60;
        String minuteString = String.format("%02d", minutes);
        String secondString = String.format("%02d", seconds);
        String unfocusedMinuteString = String.format("%02d", unfocusedMinutes);
        String unfocusedSecondString = String.format("%02d", unfocusedSeconds);
        return "Sprint Time - " + minuteString + ":" + secondString +
                "\nUnfocused Time - " + unfocusedMinuteString + ":" + unfocusedSecondString;
    }

    public int getWordCount() {
        return mWordCount;
    }

    public int getUnfocusedSeconds() {
        return mUnfocusedSeconds;
    }

    public int getSprintTimeSeconds() {
        return mSprintTimeSeconds;
    }
}

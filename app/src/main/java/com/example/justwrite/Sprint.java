package com.example.justwrite;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Objects;

public class Sprint implements Parcelable {
    private final int mSprintTimeSeconds;
    private final int mUnfocusedSeconds;
    private final int mWordCount;
    private final String mTimestamp;

    public Sprint(int numInSeconds, int unfocusedTime, int wordCount, String timestamp) {
        mSprintTimeSeconds = numInSeconds;
        mUnfocusedSeconds = unfocusedTime;
        mWordCount = wordCount;
        mTimestamp = timestamp;
    }

    @NonNull
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
                "\nUnfocused Time - " + unfocusedMinuteString + ":" + unfocusedSecondString +
                "\nDate - " + mTimestamp;
    }

    protected Sprint(Parcel in) {
        mSprintTimeSeconds = in.readInt();
        mUnfocusedSeconds = in.readInt();
        mWordCount = in.readInt();
        mTimestamp = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mSprintTimeSeconds);
        dest.writeInt(mUnfocusedSeconds);
        dest.writeInt(mWordCount);
        dest.writeString(mTimestamp);
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

    public int getWordCount() {
        return mWordCount;
    }

    public int getUnfocusedSeconds() {
        return mUnfocusedSeconds;
    }

    public int getSprintTimeSeconds() {
        return mSprintTimeSeconds;
    }

    public String getTimestamp() {
        return mTimestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sprint sprint = (Sprint) o;
        return mSprintTimeSeconds == sprint.mSprintTimeSeconds &&
                mUnfocusedSeconds == sprint.mUnfocusedSeconds &&
                mWordCount == sprint.mWordCount &&
                mTimestamp.equals(sprint.mTimestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mSprintTimeSeconds, mUnfocusedSeconds, mWordCount, mTimestamp);
    }
}

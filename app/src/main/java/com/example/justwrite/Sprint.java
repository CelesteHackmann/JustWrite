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
        return "SprintTimeSeconds=" + mSprintTimeSeconds +
                "\nUnfocusedSeconds=" + mUnfocusedSeconds;
    }

    public int getmWordCount() {
        return mWordCount;
    }

}

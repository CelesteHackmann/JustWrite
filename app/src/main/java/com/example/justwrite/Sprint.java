package com.example.justwrite;

import android.os.Parcel;
import android.os.Parcelable;

public class Sprint implements Parcelable {
    int mSprintTimeSeconds;
    int mUnfocusedSeconds;
    String projectName;

    public Sprint(int numInSeconds, int unfocusedTime) {
        mSprintTimeSeconds = numInSeconds;
        mUnfocusedSeconds = unfocusedTime;
    }

    protected Sprint(Parcel in) {
        mSprintTimeSeconds = in.readInt();
        mUnfocusedSeconds = in.readInt();
        projectName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mSprintTimeSeconds);
        dest.writeInt(mUnfocusedSeconds);
        dest.writeString(projectName);
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

    public String getProjectName() {
        return projectName;
    }
}

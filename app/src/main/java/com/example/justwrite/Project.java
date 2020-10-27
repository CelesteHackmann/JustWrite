package com.example.justwrite;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Project implements Parcelable {
    String mTitle;
    String mGenre;
    long mProjectId;
    ArrayList<Sprint> mSprints = new ArrayList<>();

    public Project(String name, String genre) {
        mTitle = name;
        mGenre = genre;
    }

    public Project(String name, String genre, long id) {
        mTitle = name;
        mGenre = genre;
        mProjectId = id;
    }

    protected Project(Parcel in) {
        mTitle = in.readString();
        mGenre = in.readString();
        mSprints = in.createTypedArrayList(Sprint.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mGenre);
        dest.writeTypedList(mSprints);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Project> CREATOR = new Creator<Project>() {
        @Override
        public Project createFromParcel(Parcel in) {
            return new Project(in);
        }

        @Override
        public Project[] newArray(int size) {
            return new Project[size];
        }
    };

    @Override
    public String toString() {
        return mTitle;
    }

    public ArrayList<Sprint> getSprints() {
        return mSprints;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getGenre() {
        return mGenre;
    }

    public void addSprint(Sprint sprint) {
        mSprints.add(sprint);
    }

    public long getId() {
        return mProjectId;
    }
}

package com.example.justwrite;

import android.os.Parcel;
import android.os.Parcelable;

public class Project implements Parcelable {
    String mTitle;
    String mGenre;
    long mProjectId;

    public Project(String name, String genre, long id) {
        mTitle = name;
        mGenre = genre;
        mProjectId = id;
    }

    protected Project(Parcel in) {
        mTitle = in.readString();
        mGenre = in.readString();
        mProjectId = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mGenre);
        dest.writeLong(mProjectId);
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

    public String getTitle() {
        return mTitle;
    }

    public String getGenre() {
        return mGenre;
    }

    public long getId() {
        return mProjectId;
    }

    public void setTitle(String newTitle) {
        mTitle = newTitle;
    }

    public void setGenre(String newGenre) {
        mGenre = newGenre;
    }
}

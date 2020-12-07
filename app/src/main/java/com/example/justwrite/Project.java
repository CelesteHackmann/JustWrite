package com.example.justwrite;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public class Project implements Parcelable {
    private String mTitle;
    private String mGenre;
    private final String mProjectId;

    public Project(String name, String genre, String id) {
        mTitle = name;
        mGenre = genre;
        mProjectId = String.valueOf(id);
    }

    @NonNull
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

    public String getId() {
        return mProjectId;
    }

    public void setTitle(String newTitle) {
        mTitle = newTitle;
    }

    public void setGenre(String newGenre) {
        mGenre = newGenre;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) { return true; }
        if (obj == null || getClass() != obj.getClass()) { return false; }
        Project project = (Project) obj;
        return (mTitle.equals(project.getTitle()) &&
                mGenre.equals(project.getGenre()) &&
                mProjectId.equals(project.getId()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(mTitle, mGenre, mProjectId);
    }

    protected Project(Parcel in) {
        mTitle = in.readString();
        mGenre = in.readString();
        mProjectId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mGenre);
        dest.writeString(mProjectId);
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
}

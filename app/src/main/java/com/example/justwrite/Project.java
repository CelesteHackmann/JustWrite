package com.example.justwrite;

import java.util.ArrayList;

public class Project {
    String mTitle;
    String mGenre;
    ArrayList<Sprint> mSprints = new ArrayList<>();

    public Project(String name, String genre) {
        mTitle = name;
        mGenre = genre;
    }

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
}

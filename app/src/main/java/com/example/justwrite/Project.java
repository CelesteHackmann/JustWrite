package com.example.justwrite;

public class Project {
    String mTitle;
    String mGenre;


    public Project(String name, String genre) {
        mTitle = name;
        mGenre = genre;
    }

    @Override
    public String toString() {
        return "Title: " + mTitle + '\n' +
                "Genre: " + mGenre;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getGenre() {
        return mGenre;
    }
}

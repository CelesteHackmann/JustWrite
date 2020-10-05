package com.example.justwrite;

public class Project {
    String mName;
    String mGenre;


    public Project(String name, String genre) {
        mName = name;
        mGenre = genre;
    }

    @Override
    public String toString() {
        return "Project: " + mName + '\n' +
                "Genre: " + mGenre;
    }
}

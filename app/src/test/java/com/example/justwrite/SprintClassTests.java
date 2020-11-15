package com.example.justwrite;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SprintClassTests {
    @Test
    public void constructorAndGetters() {
        Sprint sprint = new Sprint(600, 10, 250);
        assertEquals(600, sprint.getSprintTimeSeconds());
        assertEquals(10, sprint.getUnfocusedSeconds());
        assertEquals(250, sprint.getWordCount());
    }

    @Test
    public void sprintToString() {
        Sprint sprint = new Sprint(900, 30, 300);
        String expectedString = "Sprint Time - 15:00\nUnfocused Time - 00:30";
        assertEquals(expectedString, sprint.toString());
    }


}

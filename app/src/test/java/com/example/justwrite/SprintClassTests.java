package com.example.justwrite;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class SprintClassTests {
    Date time1 = new Date(1606500630);
    Date time2 = new Date(1606407315);

    private Sprint sprint = new Sprint(1200, 0, 800, time1);
    private Sprint sprintLong = new Sprint(2450, 15, 1000, time1);
    private Sprint sprintDifferentTime = new Sprint(1200, 0, 800, time2);

    @Test
    public void constructorAndGetters() {
        Date timestamp = Calendar.getInstance().getTime();
        Sprint sprint = new Sprint(600, 10, 250, timestamp);
        assertEquals(600, sprint.getSprintTimeSeconds());
        assertEquals(10, sprint.getUnfocusedSeconds());
        assertEquals(250, sprint.getWordCount());
        assertEquals(timestamp.toString(), sprint.getTimestamp());
    }

    @Test
    public void sprintToString() {
        String expectedString = "Sprint Time - 40:50\nUnfocused Time - 00:15\nDate - Mon Jan 19 09:15:00 EST 1970";
        assertEquals(expectedString, sprintLong.toString());
    }

    @Test
    public void equals_SprintEqualsItself() {
        assertEquals(sprint, sprint);
    }

    @Test
    public void equals_SprintNotEqualNumber() {
        assertNotEquals(10, sprint);
    }

    @Test
    public void equals_SprintsNotEqualDifferentTime() {
        assertNotEquals(sprint, sprintLong);
    }

    @Test
    public void equals_SprintsNotEqualDifferentDate() {
        assertNotEquals(sprint, sprintDifferentTime);
    }

}

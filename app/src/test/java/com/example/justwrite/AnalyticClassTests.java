package com.example.justwrite;

import com.example.justwrite.classes.Analytic;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class AnalyticClassTests {
    @Test
    public void createAnalytic() {
        Analytic analytic = new Analytic("name", "data");
        assertNotEquals(null, analytic);
    }

    @Test
    public void testGetters() {
        Analytic analytic = new Analytic("Total Words", "265");
        assertEquals("265", analytic.getData());
        assertEquals("Total Words", analytic.getName());
    }

    @Test
    public void testEquals_SelfEqualsTrue() {
        Analytic analytic1 = new Analytic("Total Words", "300");
        assertEquals(analytic1, analytic1);
    }

    @Test
    public void testEquals_DifferentNamesAndDataReturnFalse() {
        Analytic analytic1 = new Analytic("Total Words", "300");
        Analytic analytic2 = new Analytic("Total Time", "5 minutes 0 seconds");
        assertNotEquals(analytic1, analytic2);
    }

    @Test
    public void testEquals_SameNamesAndDataReturnFalse() {
        Analytic analytic1 = new Analytic("Total Words", "300");
        Analytic analytic2 = new Analytic("Total Words", "230");
        assertNotEquals(analytic1, analytic2);
    }
}

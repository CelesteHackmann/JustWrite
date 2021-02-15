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
}

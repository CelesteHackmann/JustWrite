package com.example.justwrite;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class ProjectClassTests {
    @Test
    public void projectGetters() {
        Project prj = new Project("Breaking Trust", "YA", 123);
        assertEquals("Breaking Trust", prj.getTitle());
        assertEquals("YA", prj.getGenre());
    }

    @Test
    public void projectToString() {
        Project prj = new Project("Project Restart", "GAGA", 321);
        assertEquals("Project Restart", prj.toString());
    }

}
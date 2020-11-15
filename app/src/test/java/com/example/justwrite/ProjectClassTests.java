package com.example.justwrite;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ProjectClassTests {
    @Test
    public void projectGetters() {
        Project prj = new Project("Breaking Trust", "YA", 123);
        assertEquals("Breaking Trust", prj.getTitle());
        assertEquals("YA", prj.getGenre());
    }

    @Test
    public void projectSettersAndGetters() {
        Project project = new Project("Breaking Trust", "Young Adult", 52);
        project.setTitle("Starting Over");
        project.setGenre("Adult Fiction");
        assertEquals("Starting Over", project.getTitle());
        assertEquals("Adult Fiction", project.getGenre());
    }

    @Test
    public void projectToString() {
        Project prj = new Project("Project Restart", "GAGA", 321);
        assertEquals("Project Restart", prj.toString());
    }

}
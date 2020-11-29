package com.example.justwrite;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ProjectClassTests {
    private final Project project1 = new Project("Project 1", "YA", "12");
    private final Project project1Equal = new Project("Project 1", "YA", "12");
    private final Project project2 = new Project("Project 2", "YA", "12");
    private final Project project3 = new Project("Project 1", "Adult", "12");
    private final Project project4 = new Project("Project 1", "YA", "13");

    @Test
    public void projectGetters() {
        Project prj = new Project("Breaking Trust", "YA", "123");
        assertEquals("Breaking Trust", prj.getTitle());
        assertEquals("YA", prj.getGenre());
    }

    @Test
    public void projectSettersAndGetters() {
        Project project = new Project("Breaking Trust", "Young Adult", "52");
        project.setTitle("Starting Over");
        project.setGenre("Adult Fiction");
        assertEquals("Starting Over", project.getTitle());
        assertEquals("Adult Fiction", project.getGenre());
    }

    @Test
    public void projectToString() {
        Project prj = new Project("Project Restart", "GAGA", "321");
        assertEquals("Project Restart", prj.toString());
    }

    @Test
    public void testEquals_ProjectNotEqualInt() {
        assertNotEquals(12, project1);
    }

    @Test
    public void testEquals_ProjectEqualsItself() {
        assertEquals(project1, project1);
    }

    @Test
    public void testEquals_TwoProjectsEqual() {
        assertEquals(project1, project1Equal);
    }

    @Test
    public void testEquals_ProjectNamesDifferent() {
        assertNotEquals(project1, project2);
    }

    @Test
    public void testEquals_ProjectGenresDifferent() {
        assertNotEquals(project1, project3);
    }

    @Test
    public void testEquals_ProjectIdDifferent() {
        assertNotEquals(project1, project4);
    }
}
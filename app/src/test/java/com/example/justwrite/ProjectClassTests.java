package com.example.justwrite;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class ProjectClassTests {
    @Test
    public void projectGetters() {
        Project prj = new Project("Breaking Trust", "YA");
        assertEquals("Breaking Trust", prj.getTitle());
        assertEquals("YA", prj.getGenre());
    }

    @Test
    public void projectToString() {
        Project prj = new Project("Project Restart", "Fiction");
        assertEquals("Project Restart", prj.toString());
    }

    @Test
    public void addSprintToExistingProject() {
        Project project = new Project("SOEN", "Fiction");
        Sprint sprint = new Sprint(120, 0, 25);
        project.addSprint(sprint);
        ArrayList<Sprint> sprints = new ArrayList<>();
        sprints.add(sprint);
        assertEquals(sprints, project.getSprints());
    }

}
package com.example.justwrite;

import org.junit.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
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
        assertEquals("Title: Project Restart\nGenre: Fiction", prj.toString());
    }

    @Test
    public void addSprintToExistingProject() {
        Project project = new Project("SOEN", "Fiction");
        Sprint sprint = new Sprint(120, 0, Calendar.getInstance());
        project.addSprint(sprint);
        ArrayList<Sprint> sprints = new ArrayList<>();
        sprints.add(sprint);
        assertEquals(sprints, project.getSprints());
    }

}
package com.example.justwrite;

import org.junit.Test;

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
}
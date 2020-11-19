package com.example.justwrite;

import android.content.Context;
import android.os.Build;

import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(maxSdk = Build.VERSION_CODES.P, minSdk = Build.VERSION_CODES.P) // Value of Build.VERSION_CODES.P is 28

public class DatabaseTests {
    private DatabaseHelper db;
    private Context context;
    private Project projectYoungAdult1 = new Project("Project 1", "Young Adult", "1");
    private Project projectAdult2 = new Project("Project 2", "Adult", "2");
    private Project projectTeenFiction3 = new Project("Project 3", "Teen Fiction", "3");
    private Sprint sprint1 = new Sprint(1200, 0, 800);
    private Sprint sprint2 = new Sprint(2134, 321, 324);
    private Sprint sprint3 = new Sprint(1201, 10, 500);

    @Before
    public void setUpDatabase() {
        context = ApplicationProvider.getApplicationContext();
        db = DatabaseHelper.getInstance(context);
        String projectId = String.valueOf(db.insertProject("Project 1", "Young Adult"));
        db.insertProjectStats(projectId);
        projectId = String.valueOf(db.insertProject("Project 2", "Adult"));
        db.insertProjectStats(projectId);
        projectId = String.valueOf(db.insertProject("Project 3", "Teen Fiction"));
        db.insertProjectStats(projectId);
    }

    @After
    public void createAndCloseDatabase() {
        db.close();
        context.deleteDatabase("JUSTWRITE.DB");
    }

    @Test
    public void getProjectsList() {
        ArrayList<Project> result = db.getProjectList();
        ArrayList<Project> expectedList = new ArrayList<>();
        expectedList.add(projectYoungAdult1);
        expectedList.add(projectAdult2);
        expectedList.add(projectTeenFiction3);
        assertEquals(expectedList.size(), result.size());
        for (Project project : expectedList) {
            assertTrue(result.contains(project));
        }
    }

    @Test
    public void insertAndGetSprints_OneBasicSprint() {
        Sprint basicSprint = new Sprint(1200, 0, 800);
        LinkedList<Sprint> expectedList = new LinkedList<>();
        expectedList.add(basicSprint);
        db.addSprint(basicSprint, projectYoungAdult1.getId());
        LinkedList<Sprint> result = db.getSprintsForProject(String.valueOf(projectYoungAdult1.getId()));
        assertEquals(expectedList.size(), result.size());
        assertEquals(expectedList, result);
    }

    @Test
    public void insertAndGetSprints_ThreeSprints() {
        LinkedList<Sprint> expectedList = new LinkedList<>();
        expectedList.add(sprint3);
        expectedList.add(sprint2);
        expectedList.add(sprint1);
        db.addSprint(sprint1, projectAdult2.getId());
        db.addSprint(sprint2, projectAdult2.getId());
        db.addSprint(sprint3, projectAdult2.getId());
        LinkedList result = db.getSprintsForProject(String.valueOf(projectAdult2.getId()));
        assertEquals(expectedList, result);
    }
}
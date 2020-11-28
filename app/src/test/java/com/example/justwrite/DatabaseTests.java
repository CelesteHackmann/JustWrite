package com.example.justwrite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Date;
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
    Date time1 = new Date(1606500630);

    private Sprint sprint1 = new Sprint(1200, 0, 800, time1);
    private Sprint sprint2 = new Sprint(2134, 321, 324, time1);
    private Sprint sprint3 = new Sprint(1201, 10, 500, time1);

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
        System.out.println(db.getProjectList());
    }

    @After
    public void createAndCloseDatabase() {
        SQLiteDatabase writeable = db.getWritableDatabase();
        writeable.execSQL("DROP TABLE IF EXISTS " + DatabaseHelper.PROJECTS_TABLE);
        writeable.execSQL("DROP TABLE IF EXISTS " + DatabaseHelper.SPRINTS_TABLE);
        writeable.execSQL("DROP TABLE IF EXISTS " + DatabaseHelper.PROJECTS_STATS_TABLE);
        writeable.execSQL(DatabaseHelper.CREATE_TABLE_PROJECTS);
        writeable.execSQL(DatabaseHelper.CREATE_TABLE_SPRINTS);
        writeable.execSQL(DatabaseHelper.CREATE_TABLE_PROJECT_STATS);
        writeable.close();
        db.close();
    }

    @Test
    public void getProjectsList_ThreeProjects() {
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
        LinkedList<Sprint> expectedList = new LinkedList<>();
        expectedList.add(sprint1);
        db.addSprint(sprint1, projectYoungAdult1.getId());
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

    @Test
    public void updateProjectInformationInDatabase() {
        String newTitle = "New Title";
        String newGenre = "New Genre";
        String projectId = projectYoungAdult1.getId();
        db.updateProjectTitleAndGenre(projectId, newTitle, newGenre);
        ArrayList<Project> result = db.getProjectList();
        ArrayList<Project> expectedList = new ArrayList<>();
        expectedList.add(new Project("New Title", "New Genre", projectId));
        expectedList.add(projectAdult2);
        expectedList.add(projectTeenFiction3);
        assertEquals(expectedList, result);
    }
}
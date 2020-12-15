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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(maxSdk = Build.VERSION_CODES.P, minSdk = Build.VERSION_CODES.P) // Value of Build.VERSION_CODES.P is 28

public class DatabaseTests {
    private DatabaseHelper db;
    private final Project projectYoungAdult1 = new Project("Project 1", "Young Adult", "1");
    private final Project projectAdult2 = new Project("Project 2", "Adult", "2");
    private final Project projectTeenFiction3 = new Project("Project 3", "Teen Fiction", "3");
    final Date time1 = new Date(1606500630);

    private final Sprint sprint1 = new Sprint(1200, 0, 800, time1.toString());
    private final Sprint sprint2 = new Sprint(2134, 321, 324, time1.toString());
    private final Sprint sprint3 = new Sprint(1201, 10, 500, time1.toString());

    @Before
    public void setUpDatabase() {
        Context context = ApplicationProvider.getApplicationContext();
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
    public void getUnarchivedProjects_TwoOfThreeProjects() {
        db.setProjectAsArchived(projectAdult2.getId());
        ArrayList<Project> result = db.getUnarchivedProjectList();
        ArrayList<Project> expectedList = new ArrayList<>();
        expectedList.add(projectYoungAdult1);
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
        LinkedList<Sprint> result = db.getSprintsForProject(String.valueOf(projectAdult2.getId()));
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

    @Test
    public void updateProjectStats_updateFromEmptyTable() {
        String projectId = projectYoungAdult1.getId();
        db.updateProjectStats(600, 15, 400, projectId);
        assertEquals(600, db.getTotalTime(projectId));
        assertEquals(15, db.getTotalUnfocusedTime(projectId));
        assertEquals(400, db.getTotalWordCount(projectId));
    }

    @Test
    public void updateProjectStats_updateMultipleTimesOneProject() {
        String projectId = projectYoungAdult1.getId();
        updateProjectStatsThreeTimesForProject(projectId);
        assertEquals(910, db.getTotalTime(projectId));
        assertEquals(34, db.getTotalUnfocusedTime(projectId));
        assertEquals(559, db.getTotalWordCount(projectId));
    }

    @Test
    public void analyticGetters_getTotalWordsAnalytic() {
        String projectId = projectYoungAdult1.getId();
        updateProjectStatsThreeTimesForProject(projectId);
        Analytic totalWordsAnalytic = db.getTotalWordCountAnalyticForProject(projectId);
        Analytic expected = new Analytic("Total Words", "559 words");
        assertEquals(expected, totalWordsAnalytic);
    }

    @Test
    public void analyticGetters_getTotalTimeAnalytic() {
        String projectId = projectYoungAdult1.getId();
        updateProjectStatsThreeTimesForProject(projectId);
        Analytic totalTimeAnalytic = db.getTotalTimeAnalyticForProject(projectId);
        Analytic expected = new Analytic("Total Time", "15:10");
        assertEquals(expected, totalTimeAnalytic);
    }

    @Test
    public void analyticGetters_getTotalUnfocusedTimeAnalytic() {
        String projectId = projectAdult2.getId();
        updateProjectStatsThreeTimesForProject(projectId);
        Analytic totalUnfocusedTimeAnalytic = db.getTotalUnfocusedTimeAnalyticForProject(projectId);
        Analytic expected = new Analytic("Total Unfocused Time", "0:34");
        assertEquals(expected, totalUnfocusedTimeAnalytic);
    }

    @Test
    public void analyticGetters_getWordsPerMinuteAnalytic() {
        String projectId = projectTeenFiction3.getId();
        updateProjectStatsThreeTimesForProject(projectId);
        Analytic wordsPerMinAnalytic = db.getWordsPerMinuteAnalyticForProject(projectId);
        Analytic expected = new Analytic("Average Words Per Minute", "36 words");
        assertEquals(expected, wordsPerMinAnalytic);
    }

    @Test
    public void analyticGetters_getWordsPer30MinutesAnalytic() {
        String projectId = projectTeenFiction3.getId();
        updateProjectStatsThreeTimesForProject(projectId);
        Analytic wordsPer30MinAnalytic = db.getAverageWordsPer30MinAnalyticForProject(projectId);
        Analytic expected = new Analytic("Average Words Per 30 Minutes", "1080 words");
        assertEquals(expected, wordsPer30MinAnalytic);
    }

    @Test
    public void analyticGetters_getAverageWordsPerSprintAnalytic() {
        String projectId = projectTeenFiction3.getId();
        updateProjectStatsThreeTimesForProject(projectId);
        Analytic avgWordsPerSprint = db.getAverageWordsPerSprintForProject(projectId);
        Analytic expected = new Analytic("Average Words Per Sprint", "186 words");
        assertEquals(expected, avgWordsPerSprint);
    }

    @Test
    public void analyticGetters_getAverageSprintTimeAnalytic() {
        String projectId = projectTeenFiction3.getId();
        updateProjectStatsThreeTimesForProject(projectId);
        Analytic avgSprintTime = db.getAverageSprintTimeForProject(projectId);
        Analytic expected = new Analytic("Average Sprint Time", "5:03");
        assertEquals(expected, avgSprintTime);
    }

    @Test
    public void projectArchivedInitiallyFalse() {
        boolean projectArchived = db.projectIsArchived(projectYoungAdult1.getId());
        assertFalse(projectArchived);
    }

    @Test
    public void setProjectToBeArchived() {
        db.setProjectAsArchived(projectYoungAdult1.getId());
        boolean projectArchived = db.projectIsArchived(projectYoungAdult1.getId());
        assertTrue(projectArchived);
    }

    @Test
    public void setProjectToBeUnarchived() {
        db.setProjectAsArchived(projectTeenFiction3.getId());
        db.setProjectAsUnarchived(projectTeenFiction3.getId());
        boolean projectArchived = db.projectIsArchived(projectTeenFiction3.getId());
        assertFalse(projectArchived);
    }

    @Test
    public void switchProjectArchived_MultipleTimes() {
        String projectId = projectTeenFiction3.getId();
        db.switchProjectedArchived(projectId);
        assertTrue(db.projectIsArchived(projectId));
        db.switchProjectedArchived(projectId);
        assertFalse(db.projectIsArchived(projectId));
    }

    private void updateProjectStatsThreeTimesForProject(String projectId) {
        db.updateProjectStats(500, 24, 123, projectId);
        db.updateProjectStats(150, 0, 200, projectId);
        db.updateProjectStats(260, 10, 236, projectId);
    }
}
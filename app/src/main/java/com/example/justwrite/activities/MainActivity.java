package com.example.justwrite.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.justwrite.R;
import com.example.justwrite.fragments.AnalyticsFragment;
import com.example.justwrite.fragments.SprintHistoryFragment;
import com.example.justwrite.fragments.TimerSetupFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private static final int RESULT_PROJECTS_EDITED = 1;
    private static final int RESULT_NEW_ANALYTICS_CHOSEN = 2;
    BottomNavigationView bottomNavigationView;

    final TimerSetupFragment fragmentTimerSetup = new TimerSetupFragment();
    final SprintHistoryFragment fragmentSprintHistory = new SprintHistoryFragment();
    final AnalyticsFragment fragmentAnalytics = new AnalyticsFragment();
    final FragmentManager fragmentManager = getSupportFragmentManager();
    private Fragment activeFragment;
    private Fragment fragmentToHide;

    String helpLinkUrl = "https://docs.google.com/document/d/1XWBDkBCKa9hrgbLrisv5F_lHKgxEPXEUyMcg6iS2WPU/edit?usp=sharing";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.nav_view);

        setupFragmentManager();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_timer_setup:
                        fragmentToHide = activeFragment;
                        activeFragment = fragmentTimerSetup;
                        break;
                    case R.id.navigation_sprint_history:
                        fragmentToHide = activeFragment;
                        activeFragment = fragmentSprintHistory;
                        break;
                    case R.id.navigation_analytics:
                        fragmentToHide = activeFragment;
                        activeFragment = fragmentAnalytics;
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + item.getItemId());
                }
                fragmentManager.beginTransaction().hide(fragmentToHide).show(activeFragment).commit();
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_edit_projects:
                intent = new Intent(this, EditProjectsActivity.class);
                startActivityForResult(intent, RESULT_PROJECTS_EDITED);
                return true;
            case R.id.action_choose_analytics:
                intent = new Intent(this, ChooseAnalyticsActivity.class);
                startActivityForResult(intent, RESULT_NEW_ANALYTICS_CHOSEN);
                return true;
            case R.id.action_help:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(helpLinkUrl));
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        boolean changesMade = data.getBooleanExtra("Changes Made", false);
        if(resultCode == RESULT_OK && changesMade) {
            if (requestCode == RESULT_PROJECTS_EDITED ) {
                refreshAllFragments();
            }
            else if (requestCode == RESULT_NEW_ANALYTICS_CHOSEN) {
                fragmentManager.beginTransaction().detach(fragmentAnalytics).attach(fragmentAnalytics)
                        .hide(fragmentAnalytics).show(activeFragment).commit();
            }
        }
    }

    public void refreshHistoryAndAnalyticsFragments() {
        fragmentManager.beginTransaction().detach(fragmentAnalytics).attach(fragmentAnalytics).commit();
        fragmentManager.beginTransaction().detach(fragmentSprintHistory).attach(fragmentSprintHistory).commit();
        fragmentManager.beginTransaction().hide(fragmentSprintHistory).hide(fragmentAnalytics).commit();
    }

    private void setupFragmentManager() {
        activeFragment = fragmentTimerSetup;
        fragmentManager.beginTransaction().add(R.id.host_fragment, activeFragment).show(activeFragment).commit();
        fragmentManager.beginTransaction().add(R.id.host_fragment, fragmentSprintHistory).hide(fragmentSprintHistory).commit();
        fragmentManager.beginTransaction().add(R.id.host_fragment, fragmentAnalytics).hide(fragmentAnalytics).commit();
    }

    private void refreshAllFragments() {
        fragmentManager.beginTransaction().detach(fragmentTimerSetup).attach(fragmentTimerSetup).commit();
        fragmentManager.beginTransaction().detach(fragmentAnalytics).attach(fragmentAnalytics).commit();
        fragmentManager.beginTransaction().detach(fragmentSprintHistory).attach(fragmentSprintHistory).commit();
        fragmentManager.beginTransaction().hide(fragmentSprintHistory).hide(fragmentAnalytics).hide(fragmentTimerSetup).commit();
        fragmentManager.beginTransaction().show(activeFragment).commit();
    }
}


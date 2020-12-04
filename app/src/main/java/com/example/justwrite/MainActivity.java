package com.example.justwrite;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private static final int RESULT_PROJECTS_EDITED = 1;
    private static final int RESULT_NEW_ANALYTICS_CHOSEN = 2;
    BottomNavigationView bottomNavigationView;

    final TimerSetupFragment fragmentTimerSetup = new TimerSetupFragment();
    final SprintHistoryFragment fragmentSprintHistory = new SprintHistoryFragment();
    final AnalyticsFragment fragmentAnalytics = new AnalyticsFragment();
    final FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment activeFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.nav_view);

        activeFragment = fragmentTimerSetup;
        fragmentManager.beginTransaction().replace(R.id.host_fragment, activeFragment).commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_timer_setup:
                        activeFragment = fragmentTimerSetup;
                        break;
                    case R.id.navigation_sprint_history:
                        activeFragment = fragmentSprintHistory;
                        break;
                    case R.id.navigation_analytics:
                        activeFragment = fragmentAnalytics;
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + item.getItemId());
                }
                fragmentManager.beginTransaction().replace(R.id.host_fragment, activeFragment).commit();
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if (requestCode == RESULT_PROJECTS_EDITED) {
                getSupportFragmentManager().beginTransaction().detach(activeFragment).attach(activeFragment).commit();
            }
            if (requestCode == RESULT_NEW_ANALYTICS_CHOSEN) {
                if (activeFragment == fragmentAnalytics) {
                    getSupportFragmentManager().beginTransaction().detach(activeFragment).attach(activeFragment).commit();
                }
            }
        }
    }
}


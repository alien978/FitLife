package com.example.fitlife.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.fitlife.R;
import com.example.fitlife.MainActivity;
import com.example.fitlife.data.db.AppDatabase;
import com.example.fitlife.data.model.WeeklyPlan;
import com.example.fitlife.data.repository.WorkoutRoutineRepository;
import com.example.fitlife.data.repository.GymLocationRepository;
import com.example.fitlife.utils.SessionManager;
import com.example.fitlife.ui.map.MapActivity;
import com.example.fitlife.ui.routine.RoutineListActivity;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private WorkoutRoutineRepository routineRepository;
    private GymLocationRepository locationRepository;
    private SessionManager sessionManager;
    private AppDatabase db;
    private TextView tvRoutineCount, tvLocationCount, tvPlannedCount, tvUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        db = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);
        routineRepository = new WorkoutRoutineRepository(getApplication());
        locationRepository = new GymLocationRepository(getApplication());

        tvRoutineCount = findViewById(R.id.tvRoutineCount);
        tvLocationCount = findViewById(R.id.tvLocationCount);
        tvPlannedCount = findViewById(R.id.tvPlannedCount);
        tvUserName = findViewById(R.id.tvUserName);

        String userName = getIntent().getStringExtra("USER_NAME");
        if (userName != null && !userName.isEmpty()) {
            tvUserName.setText(userName);
        } else {
            tvUserName.setText("Athlete");
        }

        findViewById(R.id.btnMyRoutines).setOnClickListener(v ->
                startActivity(new Intent(this, RoutineListActivity.class)));

        findViewById(R.id.btnPlanner).setOnClickListener(v ->
                startActivity(new Intent(this, WeeklyPlannerActivity.class)));

        findViewById(R.id.btnMap).setOnClickListener(v ->
                startActivity(new Intent(this, MapActivity.class)));

        // UPDATED: Logout with Confirmation Dialog
        findViewById(R.id.btnLogout).setOnClickListener(v -> showLogoutConfirmation());

        updateDashboardStats();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                moveTaskToBack(true);
            }
        });
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Logout", (dialog, which) -> performLogout())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performLogout() {
        sessionManager.logoutUser();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDashboardStats();
    }

    private void updateDashboardStats() {
        int userId = sessionManager.getUserId();
        int routines = routineRepository.getAllRoutines(userId).size();
        int locations = locationRepository.getAllLocations(userId).size();
        
        List<WeeklyPlan> plans = db.weeklyPlanDao().getFullPlan(userId);
        int plannedDays = 0;
        for (WeeklyPlan p : plans) {
            if (p.routineId != -1) plannedDays++;
        }

        tvRoutineCount.setText(String.valueOf(routines));
        tvLocationCount.setText(String.valueOf(locations));
        tvPlannedCount.setText(String.valueOf(plannedDays));
    }
}

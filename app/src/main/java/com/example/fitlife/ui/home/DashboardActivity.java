package com.example.fitlife.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import com.example.fitlife.R;
import com.example.fitlife.MainActivity;
import com.example.fitlife.data.db.AppDatabase;
import com.example.fitlife.data.model.WeeklyPlan;
import com.example.fitlife.data.repository.WorkoutRoutineRepository;
import com.example.fitlife.data.repository.GymLocationRepository;
import com.example.fitlife.ui.map.MapActivity;
import com.example.fitlife.ui.routine.RoutineListActivity;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private WorkoutRoutineRepository routineRepository;
    private GymLocationRepository locationRepository;
    private AppDatabase db;
    private TextView tvRoutineCount, tvLocationCount, tvPlannedCount, tvUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        db = AppDatabase.getDatabase(this);
        routineRepository = new WorkoutRoutineRepository(getApplication());
        locationRepository = new GymLocationRepository(getApplication());

        tvRoutineCount = findViewById(R.id.tvRoutineCount);
        tvLocationCount = findViewById(R.id.tvLocationCount);
        tvPlannedCount = findViewById(R.id.tvPlannedCount);
        tvUserName = findViewById(R.id.tvUserName);

        // GET USER NAME FROM LOGIN
        String userName = getIntent().getStringExtra("USER_NAME");
        if (userName != null && !userName.isEmpty()) {
            tvUserName.setText(userName);
        } else {
            tvUserName.setText("Athlete"); // Default fallback
        }

        findViewById(R.id.btnMyRoutines).setOnClickListener(v ->
                startActivity(new Intent(this, RoutineListActivity.class)));

        findViewById(R.id.btnPlanner).setOnClickListener(v ->
                startActivity(new Intent(this, WeeklyPlannerActivity.class)));

        findViewById(R.id.btnMap).setOnClickListener(v ->
                startActivity(new Intent(this, MapActivity.class)));

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        updateDashboardStats();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                moveTaskToBack(true);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDashboardStats();
    }

    private void updateDashboardStats() {
        int routines = routineRepository.getAllRoutines().size();
        int locations = locationRepository.getAllLocations().size();
        
        List<WeeklyPlan> plans = db.weeklyPlanDao().getFullPlan();
        int plannedDays = 0;
        for (WeeklyPlan p : plans) {
            if (p.routineId != -1) plannedDays++;
        }

        tvRoutineCount.setText(String.valueOf(routines));
        tvLocationCount.setText(String.valueOf(locations));
        tvPlannedCount.setText(String.valueOf(plannedDays));
    }
}

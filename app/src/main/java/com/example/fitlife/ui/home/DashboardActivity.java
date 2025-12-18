package com.example.fitlife.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import com.example.fitlife.R;
import com.example.fitlife.MainActivity;
import com.example.fitlife.ui.map.MapActivity;
import com.example.fitlife.ui.routine.RoutineListActivity;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // MY ROUTINES
        findViewById(R.id.btnMyRoutines).setOnClickListener(v ->
                startActivity(new Intent(this, RoutineListActivity.class)));

        // WEEKLY PLANNER
        findViewById(R.id.btnPlanner).setOnClickListener(v ->
                startActivity(new Intent(this, WeeklyPlannerActivity.class)));

        // MAP BUTTON - NOW CONNECTED
        findViewById(R.id.btnMap).setOnClickListener(v ->
                startActivity(new Intent(this, MapActivity.class)));

        // LOGOUT
        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                moveTaskToBack(true);
            }
        });
    }
}

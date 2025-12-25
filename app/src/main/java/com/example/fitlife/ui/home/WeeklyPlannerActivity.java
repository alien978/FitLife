package com.example.fitlife.ui.home;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.fitlife.R;
import com.example.fitlife.data.db.AppDatabase;
import com.example.fitlife.data.model.WeeklyPlan;
import com.example.fitlife.data.model.WorkoutRoutine;
import com.example.fitlife.utils.SessionManager;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WeeklyPlannerActivity extends AppCompatActivity {

    private LinearLayout containerDays, layoutWeekStrip;
    private TextView tvTrainingSummary;
    private AppDatabase db;
    private SessionManager sessionManager;
    private List<WorkoutRoutine> allRoutines;
    private final String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    private final Map<String, View> dayViews = new HashMap<>();
    private final Map<String, WeeklyPlan> currentPlanMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_planner);

        db = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);
        containerDays = findViewById(R.id.containerDays);
        layoutWeekStrip = findViewById(R.id.layoutWeekStrip);
        tvTrainingSummary = findViewById(R.id.tvTrainingSummary);
        findViewById(R.id.btnBackToDashboard).setOnClickListener(v -> finish());

        allRoutines = db.workoutRoutineDao().getAllRoutines(sessionManager.getUserId());
        
        setupWeekStrip();
        setupDayRows();
        loadSavedPlan();
    }

    private void setupWeekStrip() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        String todayName = new SimpleDateFormat("EEE", Locale.getDefault()).format(Calendar.getInstance().getTime());

        for (String day : daysOfWeek) {
            String shortName = day.substring(0, 3);
            TextView tv = new TextView(this);
            tv.setText(shortName);
            tv.setGravity(Gravity.CENTER);
            tv.setPadding(20, 20, 20, 20);
            tv.setTextSize(12);
            
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            tv.setLayoutParams(lp);

            if (shortName.equalsIgnoreCase(todayName)) {
                tv.setTextColor(Color.WHITE);
                GradientDrawable shape = new GradientDrawable();
                shape.setShape(GradientDrawable.OVAL);
                shape.setColor(ContextCompat.getColor(this, R.color.teal_main));
                tv.setBackground(shape);
            } else {
                tv.setTextColor(ContextCompat.getColor(this, R.color.teal_dark));
            }
            layoutWeekStrip.addView(tv);
        }
    }

    private void setupDayRows() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        SimpleDateFormat dateFmt = new SimpleDateFormat("d MMM", Locale.getDefault());
        String today = new SimpleDateFormat("EEEE", Locale.getDefault()).format(Calendar.getInstance().getTime());

        for (String day : daysOfWeek) {
            View row = LayoutInflater.from(this).inflate(R.layout.item_planner_day, containerDays, false);
            
            TextView tvDayName = row.findViewById(R.id.tvDayName);
            TextView tvDate = row.findViewById(R.id.tvDate);
            TextView tvTodayBadge = row.findViewById(R.id.tvTodayBadge);
            ImageView ivStatus = row.findViewById(R.id.ivStatus);
            
            tvDayName.setText(day);
            tvDate.setText(dateFmt.format(cal.getTime()));
            cal.add(Calendar.DAY_OF_YEAR, 1);
            
            if (day.equalsIgnoreCase(today)) {
                tvTodayBadge.setVisibility(View.VISIBLE);
            }

            // Tapping the card opens the routine assignment dialog
            row.setOnClickListener(v -> showDayActionDialog(day));

            // NEW: Tapping the checkbox toggles completion status directly
            ivStatus.setOnClickListener(v -> {
                WeeklyPlan plan = currentPlanMap.get(day);
                if (plan != null && plan.routineId != -1) {
                    plan.isCompleted = !plan.isCompleted;
                    new Thread(() -> {
                        db.weeklyPlanDao().updateDayPlan(plan);
                        runOnUiThread(() -> updateDayUI(plan));
                    }).start();
                }
            });

            dayViews.put(day, row);
            containerDays.addView(row);
        }
    }

    private void loadSavedPlan() {
        new Thread(() -> {
            List<WeeklyPlan> savedPlans = db.weeklyPlanDao().getFullPlan(sessionManager.getUserId());
            runOnUiThread(() -> {
                int trainingCount = 0;
                for (WeeklyPlan plan : savedPlans) {
                    currentPlanMap.put(plan.dayName, plan);
                    updateDayUI(plan);
                    if (plan.routineId != -1) trainingCount++;
                }
                tvTrainingSummary.setText(trainingCount + " training days this week");
            });
        }).start();
    }

    private void updateDayUI(WeeklyPlan plan) {
        View row = dayViews.get(plan.dayName);
        if (row == null) return;

        TextView tvRoutine = row.findViewById(R.id.tvRoutineName);
        TextView tvExercises = row.findViewById(R.id.tvExercisePreview);
        ImageView ivStatus = row.findViewById(R.id.ivStatus);

        tvRoutine.setText(plan.routineName);
        if (plan.routineId == -1) {
            tvRoutine.setTextColor(Color.GRAY);
            tvExercises.setText("Tap to assign a routine");
            ivStatus.setVisibility(View.GONE);
        } else {
            tvRoutine.setTextColor(ContextCompat.getColor(this, R.color.teal_dark));
            tvExercises.setText(plan.exercisePreview != null ? plan.exercisePreview : "No exercises listed");
            ivStatus.setVisibility(View.VISIBLE);
            ivStatus.setImageResource(plan.isCompleted ? android.R.drawable.checkbox_on_background : android.R.drawable.checkbox_off_background);
            ivStatus.setColorFilter(plan.isCompleted ? ContextCompat.getColor(this, R.color.teal_main) : Color.GRAY);
        }
    }

    private void showDayActionDialog(String day) {
        int userId = sessionManager.getUserId();
        
        String[] options = new String[allRoutines.size() + 1];
        options[0] = "Rest day";
        for (int i = 0; i < allRoutines.size(); i++) options[i+1] = allRoutines.get(i).name;

        new AlertDialog.Builder(this)
                .setTitle(day + " Plan")
                .setItems(options, (dialog, which) -> {
                    String rName = options[which];
                    int rId = (which == 0) ? -1 : allRoutines.get(which-1).id;
                    String preview = (which == 0) ? "" : getExercisePreview(allRoutines.get(which-1).exercises);
                    
                    WeeklyPlan newPlan = new WeeklyPlan(day, userId, rId, rName, preview, false);
                    new Thread(() -> {
                        db.weeklyPlanDao().updateDayPlan(newPlan);
                        runOnUiThread(() -> {
                            currentPlanMap.put(day, newPlan);
                            updateDayUI(newPlan);
                            refreshSummary();
                        });
                    }).start();
                })
                .show();
    }

    private String getExercisePreview(String exercises) {
        if (exercises == null || exercises.isEmpty()) return "No exercises";
        String[] lines = exercises.split("\n");
        if (lines.length == 1) return lines[0];
        return lines[0] + ", " + lines[1];
    }

    private void refreshSummary() {
        int count = 0;
        for (WeeklyPlan p : currentPlanMap.values()) if (p.routineId != -1) count++;
        tvTrainingSummary.setText(count + " training days this week");
    }
}

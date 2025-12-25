package com.example.fitlife.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(tableName = "weekly_plans", primaryKeys = {"dayName", "userId"})
public class WeeklyPlan {
    @NonNull
    public String dayName; // "Monday", "Tuesday", etc.
    
    public int userId; // Link to the user who owns this plan
    
    public int routineId; // -1 for Rest Day
    public String routineName;
    public String exercisePreview; // Preview of first 2 exercises
    public boolean isCompleted; // Completion status for the day

    public WeeklyPlan(@NonNull String dayName, int userId, int routineId, String routineName, String exercisePreview, boolean isCompleted) {
        this.dayName = dayName;
        this.userId = userId;
        this.routineId = routineId;
        this.routineName = routineName;
        this.exercisePreview = exercisePreview;
        this.isCompleted = isCompleted;
    }
}

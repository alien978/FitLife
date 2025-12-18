package com.example.fitlife.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "workout_routines")
public class WorkoutRoutine {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String exercises;
    public String equipment;
    public String imageUri;
    
    // NEW: Geotagging fields
    public int locationId = -1; // -1 means no location assigned
    public String locationName;

    @ColumnInfo(defaultValue = "0")
    public boolean isCompleted;

    public WorkoutRoutine(String name, String exercises, String equipment, String imageUri, int locationId, String locationName) {
        this.name = name;
        this.exercises = exercises;
        this.equipment = equipment;
        this.imageUri = imageUri;
        this.locationId = locationId;
        this.locationName = locationName;
    }
}

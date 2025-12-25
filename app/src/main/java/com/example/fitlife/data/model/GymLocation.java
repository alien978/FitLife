package com.example.fitlife.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "gym_locations")
public class GymLocation {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int userId; // Owner of this location
    public String name;
    public double latitude;
    public double longitude;

    public GymLocation(int userId, String name, double latitude, double longitude) {
        this.userId = userId;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}

package com.example.fitlife.data.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.fitlife.data.model.GymLocation;
import java.util.List;

@Dao
public interface GymLocationDao {
    @Query("SELECT * FROM gym_locations")
    List<GymLocation> getAllLocations();

    @Query("SELECT * FROM gym_locations WHERE id = :id")
    GymLocation getLocationById(int id);

    @Insert
    void insert(GymLocation location);

    @Update
    void update(GymLocation location);

    @Delete
    void delete(GymLocation location);
}

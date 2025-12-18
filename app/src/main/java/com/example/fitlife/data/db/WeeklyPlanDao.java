package com.example.fitlife.data.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.fitlife.data.model.WeeklyPlan;
import java.util.List;

@Dao
public interface WeeklyPlanDao {
    @Query("SELECT * FROM weekly_plans")
    List<WeeklyPlan> getFullPlan();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void updateDayPlan(WeeklyPlan plan);
}

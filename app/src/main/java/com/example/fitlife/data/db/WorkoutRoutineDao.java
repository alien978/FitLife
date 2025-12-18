package com.example.fitlife.data.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.fitlife.data.model.WorkoutRoutine;
import java.util.List;

@Dao
public interface WorkoutRoutineDao {

    @Query("SELECT * FROM workout_routines ORDER BY id DESC")
    List<WorkoutRoutine> getAllRoutines();

    @Query("SELECT * FROM workout_routines WHERE id = :routineId")
    WorkoutRoutine getRoutineById(int routineId);

    @Insert
    void insert(WorkoutRoutine routine);

    @Update
    void update(WorkoutRoutine routine);

    @Delete
    void delete(WorkoutRoutine routine);
}

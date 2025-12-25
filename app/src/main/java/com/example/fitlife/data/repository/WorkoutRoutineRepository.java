package com.example.fitlife.data.repository;

import android.app.Application;
import com.example.fitlife.data.db.AppDatabase;
import com.example.fitlife.data.db.WorkoutRoutineDao;
import com.example.fitlife.data.model.WorkoutRoutine;
import java.util.List;

public class WorkoutRoutineRepository {

    private final WorkoutRoutineDao dao;

    public WorkoutRoutineRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.workoutRoutineDao();
    }

    public List<WorkoutRoutine> getAllRoutines(int userId) {
        return dao.getAllRoutines(userId);
    }

    public WorkoutRoutine getRoutineById(int routineId) {
        return dao.getRoutineById(routineId);
    }

    public void insert(WorkoutRoutine routine) {
        dao.insert(routine);
    }

    public void update(WorkoutRoutine routine) {
        dao.update(routine);
    }

    public void delete(WorkoutRoutine routine) {
        dao.delete(routine);
    }
}

package com.example.fitlife.data.db;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.fitlife.data.model.User;
import com.example.fitlife.data.model.WorkoutRoutine;
import com.example.fitlife.data.model.WeeklyPlan;
import com.example.fitlife.data.model.GymLocation;

@Database(entities = {User.class, WorkoutRoutine.class, WeeklyPlan.class, GymLocation.class}, version = 11, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract WorkoutRoutineDao workoutRoutineDao();
    public abstract WeeklyPlanDao weeklyPlanDao();
    public abstract GymLocationDao gymLocationDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "fitlife_database")
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

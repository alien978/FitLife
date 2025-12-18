package com.example.fitlife.data.repository;

import android.app.Application;
import com.example.fitlife.data.db.AppDatabase;
import com.example.fitlife.data.db.GymLocationDao;
import com.example.fitlife.data.model.GymLocation;
import java.util.List;

public class GymLocationRepository {
    private final GymLocationDao dao;

    public GymLocationRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.gymLocationDao();
    }

    public List<GymLocation> getAllLocations() {
        return dao.getAllLocations();
    }

    public GymLocation getLocationById(int id) {
        return dao.getLocationById(id);
    }

    public void insert(GymLocation location) {
        dao.insert(location);
    }

    public void delete(GymLocation location) {
        dao.delete(location);
    }
}

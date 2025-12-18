package com.example.fitlife.ui.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.example.fitlife.R;
import com.example.fitlife.data.model.GymLocation;
import com.example.fitlife.data.repository.GymLocationRepository;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private GymLocationRepository repository;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        repository = new GymLocationRepository(getApplication());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        
        enableUserLocation();
        loadSavedMarkers();

        mMap.setOnMapLongClickListener(latLng -> showSaveLocationDialog(latLng));
    }

    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        mMap.setMyLocationEnabled(true);
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current, 14));
            }
        });
    }

    private void loadSavedMarkers() {
        List<GymLocation> locations = repository.getAllLocations();
        
        // AUTO-SEED: Add demo locations if list is empty
        if (locations.isEmpty()) {
            addDemoLocation("University Fitness Center", -37.8368, 144.9280); // Example coordinates
            addDemoLocation("Riverside Running Park", -37.8200, 144.9500);
            addDemoLocation("Home Workout Space", -37.8136, 144.9631);
            locations = repository.getAllLocations(); // refresh list
        }

        for (GymLocation loc : locations) {
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(loc.latitude, loc.longitude))
                    .title(loc.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        }
    }

    private void addDemoLocation(String name, double lat, double lng) {
        repository.insert(new GymLocation(name, lat, lng));
    }

    private void showSaveLocationDialog(LatLng latLng) {
        final EditText input = new EditText(this);
        input.setHint("Name this place...");

        new AlertDialog.Builder(this)
                .setTitle("New Location")
                .setMessage("Save this spot to your gym map?")
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (!name.isEmpty()) {
                        saveLocationToDb(name, latLng);
                    } else {
                        Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveLocationToDb(String name, LatLng latLng) {
        GymLocation newLoc = new GymLocation(name, latLng.latitude, latLng.longitude);
        repository.insert(newLoc);
        
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(name)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        
        Toast.makeText(this, "Location pinned!", Toast.LENGTH_SHORT).show();
    }
}

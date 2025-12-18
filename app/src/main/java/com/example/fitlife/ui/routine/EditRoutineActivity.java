package com.example.fitlife.ui.routine;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.example.fitlife.R;
import com.example.fitlife.data.model.GymLocation;
import com.example.fitlife.data.model.WorkoutRoutine;
import com.example.fitlife.data.repository.WorkoutRoutineRepository;
import com.example.fitlife.data.repository.GymLocationRepository;
import com.google.android.material.textfield.TextInputEditText;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditRoutineActivity extends AppCompatActivity {

    private WorkoutRoutineRepository repository;
    private GymLocationRepository locationRepository;
    private TextInputEditText etRoutineName, etEquipment;
    private LinearLayout containerExercises;
    private WorkoutRoutine currentRoutine = null;
    private Button btnSendSms, btnAddImage, btnQuickAdd;
    private ImageView ivRoutineImage;
    private FrameLayout layoutImagePreview;
    private Spinner spinnerLocation;
    
    private Uri currentImageUri = null;
    private String savedImageUriString = null;
    private List<GymLocation> allLocations;

    private final String[] equipmentOptions = {
            "Dumbbells", "Yoga mat", "Resistance bands", "Bench", 
            "Jump rope", "Barbell", "Kettlebell", "Pull-up bar"
    };
    private final boolean[] checkedItems = new boolean[equipmentOptions.length];

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    showImagePreview(result.getData().getData());
                }
            });

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    showImagePreview(currentImageUri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_routine);

        repository = new WorkoutRoutineRepository(getApplication());
        locationRepository = new GymLocationRepository(getApplication());

        etRoutineName = findViewById(R.id.etRoutineName);
        containerExercises = findViewById(R.id.containerExercises);
        etEquipment = findViewById(R.id.etEquipment);
        btnSendSms = findViewById(R.id.btnSendSms);
        btnAddImage = findViewById(R.id.btnAddImage);
        btnQuickAdd = findViewById(R.id.btnQuickAddEquipment);
        ivRoutineImage = findViewById(R.id.ivRoutineImage);
        layoutImagePreview = findViewById(R.id.layoutImagePreview);
        spinnerLocation = findViewById(R.id.spinnerLocation);
        ImageButton btnRemoveImage = findViewById(R.id.btnRemoveImage);
        
        Button btnAddExercise = findViewById(R.id.btnAddExercise);
        Button btnSaveRoutine = findViewById(R.id.btnSaveRoutine);

        setupLocationSpinner();

        int routineId = getIntent().getIntExtra("ROUTINE_ID", -1);
        if (routineId != -1) {
            currentRoutine = repository.getRoutineById(routineId);
            if (currentRoutine != null) {
                etRoutineName.setText(currentRoutine.name);
                etEquipment.setText(currentRoutine.equipment);
                recreateExerciseRows(currentRoutine.exercises);
                if (currentRoutine.imageUri != null) showImagePreview(Uri.parse(currentRoutine.imageUri));
                
                for (int i = 0; i < allLocations.size(); i++) {
                    if (allLocations.get(i).id == currentRoutine.locationId) {
                        spinnerLocation.setSelection(i + 1);
                        break;
                    }
                }
                btnSendSms.setVisibility(View.VISIBLE);
            }
        } else {
            btnSendSms.setVisibility(View.GONE);
            addExerciseRow("", "", "");
        }

        btnAddImage.setOnClickListener(v -> showImageSourceDialog());
        btnRemoveImage.setOnClickListener(v -> removeImage());
        btnQuickAdd.setOnClickListener(v -> showQuickAddDialog());
        btnAddExercise.setOnClickListener(v -> addExerciseRow("", "", ""));
        btnSaveRoutine.setOnClickListener(v -> saveRoutine());
        btnSendSms.setOnClickListener(v -> sendSmsChecklist());
    }

    private void setupLocationSpinner() {
        allLocations = locationRepository.getAllLocations();
        List<String> names = new ArrayList<>();
        names.add("No location assigned");
        for (GymLocation loc : allLocations) names.add(loc.name);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, names) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTextColor(position == 0 ? Color.GRAY : Color.BLACK);
                return v;
            }

            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setTextColor(Color.BLACK);
                return v;
            }
        };
        spinnerLocation.setAdapter(adapter);
    }

    private void saveRoutine() {
        String name = etRoutineName.getText().toString().trim();
        if (name.isEmpty()) { Toast.makeText(this, "Enter name", Toast.LENGTH_SHORT).show(); return; }
        
        String exercises = getCombinedExercises();
        String equipment = etEquipment.getText().toString().trim();
        
        int locPos = spinnerLocation.getSelectedItemPosition();
        int locId = (locPos == 0) ? -1 : allLocations.get(locPos - 1).id;
        String locName = (locPos == 0) ? null : allLocations.get(locPos - 1).name;

        if (currentRoutine == null) {
            repository.insert(new WorkoutRoutine(name, exercises, equipment, savedImageUriString, locId, locName));
        } else {
            currentRoutine.name = name;
            currentRoutine.exercises = exercises;
            currentRoutine.equipment = equipment;
            currentRoutine.imageUri = savedImageUriString;
            currentRoutine.locationId = locId;
            currentRoutine.locationName = locName;
            repository.update(currentRoutine);
        }
        finish();
    }

    private void showQuickAddDialog() {
        Arrays.fill(checkedItems, false);
        new AlertDialog.Builder(this)
                .setTitle("Select Equipment")
                .setMultiChoiceItems(equipmentOptions, checkedItems, (dialog, which, isChecked) -> checkedItems[which] = isChecked)
                .setPositiveButton("Add Selected", (dialog, which) -> {
                    StringBuilder sb = new StringBuilder(etEquipment.getText().toString());
                    for (int i = 0; i < equipmentOptions.length; i++) {
                        if (checkedItems[i]) {
                            if (sb.length() > 0 && sb.charAt(sb.length() - 1) != '\n') sb.append("\n");
                            sb.append(equipmentOptions[i]).append("\n");
                        }
                    }
                    etEquipment.setText(sb.toString().trim());
                })
                .setNegativeButton("Cancel", null).show();
    }

    private void showImageSourceDialog() {
        String[] options = {"Choose from Gallery", "Take Photo"};
        new AlertDialog.Builder(this).setTitle("Add Image").setItems(options, (dialog, which) -> {
            if (which == 0) launchGallery(); else checkCameraPermission();
        }).show();
    }

    private void launchGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
        } else {
            launchCamera();
        }
    }

    private void launchCamera() {
        try {
            File photoFile = File.createTempFile("JPEG_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + "_", ".jpg", getExternalFilesDir(null));
            currentImageUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, currentImageUri);
            cameraLauncher.launch(intent);
        } catch (IOException e) { Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show(); }
    }

    private void showImagePreview(Uri uri) {
        if (uri == null) return;
        try { getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION); } catch (Exception ignored) {}
        savedImageUriString = uri.toString();
        ivRoutineImage.setImageURI(uri);
        layoutImagePreview.setVisibility(View.VISIBLE);
        btnAddImage.setVisibility(View.GONE);
    }

    private void removeImage() {
        savedImageUriString = null;
        layoutImagePreview.setVisibility(View.GONE);
        btnAddImage.setVisibility(View.VISIBLE);
    }

    private void addExerciseRow(String name, String sets, String notes) {
        View row = LayoutInflater.from(this).inflate(R.layout.item_dynamic_exercise, containerExercises, false);
        ((TextInputEditText)row.findViewById(R.id.etDynamicExerciseName)).setText(name);
        ((TextInputEditText)row.findViewById(R.id.etDynamicSetsReps)).setText(sets);
        ((TextInputEditText)row.findViewById(R.id.etDynamicNotes)).setText(notes);
        row.findViewById(R.id.btnRemoveExercise).setOnClickListener(v -> containerExercises.removeView(row));
        containerExercises.addView(row);
    }

    private void recreateExerciseRows(String combined) {
        if (combined == null || combined.isEmpty()) return;
        for (String line : combined.split("\n")) {
            String[] p = line.split(" - ");
            addExerciseRow(p.length > 0 ? p[0] : "", p.length > 1 ? p[1] : "", p.length > 2 ? p[2] : "");
        }
    }

    private String getCombinedExercises() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < containerExercises.getChildCount(); i++) {
            View r = containerExercises.getChildAt(i);
            TextInputEditText etN = r.findViewById(R.id.etDynamicExerciseName);
            TextInputEditText etS = r.findViewById(R.id.etDynamicSetsReps);
            TextInputEditText etNt = r.findViewById(R.id.etDynamicNotes);
            String n = etN.getText().toString().trim();
            String s = etS.getText().toString().trim();
            String nt = etNt.getText().toString().trim();
            if (!n.isEmpty()) {
                if (sb.length() > 0) sb.append("\n");
                sb.append(n).append(" - ").append(s).append(" - ").append(nt);
            }
        }
        return sb.toString();
    }

    private void sendSmsChecklist() {
        if (currentRoutine == null) return;
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"));
        intent.putExtra("sms_body", "Routine: " + currentRoutine.name + "\nLocation: " + (currentRoutine.locationName != null ? currentRoutine.locationName : "None") + "\n\nEquipment:\n" + currentRoutine.equipment);
        startActivity(intent);
    }
}

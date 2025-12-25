package com.example.fitlife.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.lifecycle.ViewModelProvider;
import com.example.fitlife.R;
import com.example.fitlife.data.db.AppDatabase;
import com.example.fitlife.data.model.User;
import com.example.fitlife.data.model.WeeklyPlan;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private UserViewModel userViewModel;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        db = AppDatabase.getDatabase(this);

        TextInputEditText etName = findViewById(R.id.etName);
        TextInputEditText etEmail = findViewById(R.id.etEmail);
        TextInputEditText etPassword = findViewById(R.id.etPassword);
        TextInputEditText etConfirm = findViewById(R.id.etConfirmPassword);

        findViewById(R.id.tvGoToLogin).setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        findViewById(R.id.btnRegister).setOnClickListener(v -> {
            String name = Objects.requireNonNull(etName.getText()).toString().trim();
            String email = Objects.requireNonNull(etEmail.getText()).toString().trim();
            String pass = Objects.requireNonNull(etPassword.getText()).toString();
            String confirm = Objects.requireNonNull(etConfirm.getText()).toString();

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!pass.equals(confirm)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }
            if (userViewModel.isEmailTaken(email)) {
                Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show();
                return;
            }

            // 1. Register User
            User newUser = new User(name, email, pass);
            userViewModel.registerUser(newUser);
            
            // Get the newly created user to initialize their plan
            User registeredUser = userViewModel.getUserByEmail(email);
            if (registeredUser != null) {
                // 2. Initialize User's Weekly Plan in background
                initializeUserWeeklyPlan(registeredUser.id);

                Toast.makeText(this, "Account created! Please login.", Toast.LENGTH_LONG).show();
                
                // 3. Redirect to Login Activity
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initializeUserWeeklyPlan(int userId) {
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        new Thread(() -> {
            for (String day : days) {
                db.weeklyPlanDao().updateDayPlan(new WeeklyPlan(day, userId, -1, "Rest Day", "No routine assigned", false));
            }
        }).start();
    }
}
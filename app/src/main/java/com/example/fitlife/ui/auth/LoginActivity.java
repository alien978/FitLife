package com.example.fitlife.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.fitlife.R;
import com.example.fitlife.data.model.User;
import com.example.fitlife.ui.home.DashboardActivity;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        TextInputEditText etEmail = findViewById(R.id.etEmail);
        TextInputEditText etPassword = findViewById(R.id.etPassword);

        findViewById(R.id.btnLoginReal).setOnClickListener(v -> {
            String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
            String password = etPassword.getText() != null ? etPassword.getText().toString() : "";

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (userViewModel.checkLogin(email, password)) {
                User user = userViewModel.getUserByEmail(email);
                String name = (user != null && user.fullName != null) ? user.fullName : "Athlete";
                
                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                intent.putExtra("USER_NAME", name); // Pass the name to the dashboard
                startActivity(intent);
                
                Toast.makeText(this, "Logged in as " + name, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Wrong email or password", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.tvGoToRegister).setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }
}
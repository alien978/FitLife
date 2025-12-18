package com.example.fitlife.ui.auth;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.fitlife.data.model.User;
import com.example.fitlife.data.repository.UserRepository;

public class UserViewModel extends AndroidViewModel {

    private final UserRepository repository;

    public UserViewModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository(application);
    }

    // Register new user (hashes password inside repository)
    public void registerUser(User user) {
        repository.registerUser(user);
    }

    // Get user by email (for showing name after login)
    public User getUserByEmail(String email) {
        return repository.getUserByEmail(email);
    }

    // Check if email already exists
    public boolean isEmailTaken(String email) {
        return repository.isEmailTaken(email);
    }

    // SECURE LOGIN: hashes password and compares
    public boolean checkLogin(String email, String plainPassword) {
        return repository.checkLogin(email, plainPassword);
    }
}
package com.example.fitlife.data.repository;

import android.app.Application;
import com.example.fitlife.data.db.AppDatabase;
import com.example.fitlife.data.db.UserDao;
import com.example.fitlife.data.model.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class UserRepository {

    private final UserDao userDao;

    public UserRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        userDao = db.userDao();
    }

    // Helper: hash password using SHA-256
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password; // fallback (never happens on Android)
        }
    }

    // Register: hash password before saving
    public void registerUser(User user) {
        user.password = hashPassword(user.password);
        userDao.insertUser(user);
    }

    // Login: hash input and compare
    public User getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }

    public boolean checkLogin(String email, String plainPassword) {
        User user = getUserByEmail(email);
        if (user == null) return false;
        return user.password.equals(hashPassword(plainPassword));
    }

    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    public boolean isEmailTaken(String email) {
        return userDao.countUsersByEmail(email) > 0;
    }
}
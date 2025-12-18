package com.example.fitlife.data.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.fitlife.data.model.User;

import java.util.List;

@Dao
public interface UserDao {

    // Save a new user (used when someone registers)
    @Insert
    void insertUser(User user);

    // Get user by email (used when someone logs in)
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User getUserByEmail(String email);

    // Get all users (just in case we can see it worked – optional)
    @Query("SELECT * FROM users")
    List<User> getAllUsers();

    // Check if email already exists (to prevent duplicate accounts)
    @Query("SELECT COUNT(*) FROM users WHERE email = :email")
    int countUsersByEmail(String email);
}
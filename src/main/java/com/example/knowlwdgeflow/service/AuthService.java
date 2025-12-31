package com.example.knowlwdgeflow.service;

import com.example.knowlwdgeflow.dao.UserDao;
import com.example.knowlwdgeflow.model.User;

public class AuthService {
    private final UserDao userDao;
    private final SessionService sessionService = new SessionService();

    public AuthService() {
        this.userDao = new UserDao();
    }

    public User signup(String name, String email, String password) throws Exception {
        if (name == null || name.isBlank() || email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("All fields are required");
        }
        if (userDao.emailExists(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        User created = userDao.insert(name.trim(), email.trim(), password);
        sessionService.saveUserId(created.getId());
        return created;
    }

    public User login(String email, String password) throws Exception {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Email and password required");
        }
        User user = userDao.validateLogin(email.trim(), password);
        if (user == null) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        sessionService.saveUserId(user.getId());
        return user;
    }

    public void updateProfileImage(int userId, byte[] imageBytes) throws Exception {
        userDao.updateProfileImage(userId, imageBytes);
    }

    public void clearSession() {
        sessionService.clear();
    }
}

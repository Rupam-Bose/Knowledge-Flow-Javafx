package com.example.knowlwdgeflow.dao;

import com.example.knowlwdgeflow.Database;
import com.example.knowlwdgeflow.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {
    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT id, name, email, profile_image FROM users WHERE email = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
                return null;
            }
        }
    }

    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public User insert(String name, String email, String passwordPlain) throws SQLException {
        String sql = "INSERT INTO users(name, email, password, profile_image) VALUES(?,?,?,?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, email.toLowerCase());
            ps.setString(3, passwordPlain);
            ps.setBytes(4, null);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    return new User(id, name, email.toLowerCase(), null);
                }
            }
            throw new SQLException("Failed to retrieve generated key");
        }
    }

    public User validateLogin(String email, String passwordPlain) throws SQLException {
        String sql = "SELECT id, name, email, profile_image FROM users WHERE email = ? AND password = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.toLowerCase());
            ps.setString(2, passwordPlain);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
                return null;
            }
        }
    }

    public void updateProfileImage(int userId, byte[] imageBytes) throws SQLException {
        String sql = "UPDATE users SET profile_image = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBytes(1, imageBytes);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    public User findById(int id) throws SQLException {
        String sql = "SELECT id, name, email, profile_image FROM users WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
                return null;
            }
        }
    }

    public User findByIdWithFollowerCount(int id, FollowDao followDao) throws SQLException {
        User base = findById(id);
        if (base == null) return null;
        try {
            int count = followDao.countFollowers(id);
            return new User(base.getId(), base.getName(), base.getEmail(), base.getProfileImage(), count);
        } catch (Exception e) {
            return base;
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        byte[] image = rs.getBytes("profile_image");
        return new User(id, name, email, image);
    }
}

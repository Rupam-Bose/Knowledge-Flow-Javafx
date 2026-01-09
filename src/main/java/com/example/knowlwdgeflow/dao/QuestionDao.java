package com.example.knowlwdgeflow.dao;

import com.example.knowlwdgeflow.Database;
import com.example.knowlwdgeflow.model.Question;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class QuestionDao {

    public Question insert(int userId, String text) throws SQLException {
        String sql = "INSERT INTO questions(user_id, text) VALUES(?,?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setString(2, text);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    return findById(id);
                }
            }
            throw new SQLException("Failed to retrieve question id");
        }
    }

    public Question findById(int id) throws SQLException {
        String sql = "SELECT q.id, q.user_id, q.text, q.created_at, u.name, u.email, u.profile_image " +
                "FROM questions q JOIN users u ON u.id = q.user_id WHERE q.id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapQuestion(rs);
                }
            }
            return null;
        }
    }

    public List<Question> findRecent(int limit) throws SQLException {
        String sql = "SELECT q.id, q.user_id, q.text, q.created_at, u.name, u.email, u.profile_image " +
                "FROM questions q JOIN users u ON u.id = q.user_id " +
                "ORDER BY q.created_at DESC LIMIT ?";
        List<Question> questions = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    questions.add(mapQuestion(rs));
                }
            }
        }
        return questions;
    }

    private Question mapQuestion(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int userId = rs.getInt("user_id");
        String text = rs.getString("text");
        Timestamp ts = rs.getTimestamp("created_at");
        LocalDateTime createdAt = ts != null ? ts.toLocalDateTime() : null;
        String authorName = rs.getString("name");
        String authorEmail = rs.getString("email");
        byte[] authorImage = rs.getBytes("profile_image");
        return new Question(id, userId, text, createdAt, authorName, authorEmail, authorImage);
    }
}


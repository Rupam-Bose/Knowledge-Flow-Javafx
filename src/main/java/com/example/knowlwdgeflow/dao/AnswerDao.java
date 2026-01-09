package com.example.knowlwdgeflow.dao;

import com.example.knowlwdgeflow.Database;
import com.example.knowlwdgeflow.model.Answer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AnswerDao {

    public Answer insert(int questionId, int userId, String content) throws SQLException {
        String sql = "INSERT INTO answers(question_id, user_id, content) VALUES(?,?,?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, questionId);
            ps.setInt(2, userId);
            ps.setString(3, content);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return findById(keys.getInt(1));
                }
            }
        }
        throw new SQLException("Failed to insert answer");
    }

    public Answer findById(int id) throws SQLException {
        String sql = baseSelect() + " WHERE a.id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
            return null;
        }
    }

    public List<Answer> findByQuestion(int questionId) throws SQLException {
        String sql = baseSelect() + " WHERE a.question_id = ? ORDER BY a.created_at DESC";
        List<Answer> answers = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) answers.add(map(rs));
            }
        }
        return answers;
    }

    private String baseSelect() {
        return "SELECT a.id, a.question_id, a.user_id, a.content, a.created_at, u.name, u.email, u.profile_image " +
                "FROM answers a JOIN users u ON u.id = a.user_id";
    }

    private Answer map(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int qid = rs.getInt("question_id");
        int uid = rs.getInt("user_id");
        String content = rs.getString("content");
        Timestamp ts = rs.getTimestamp("created_at");
        LocalDateTime createdAt = ts != null ? ts.toLocalDateTime() : null;
        String name = rs.getString("name");
        String email = rs.getString("email");
        byte[] img = rs.getBytes("profile_image");
        return new Answer(id, qid, uid, content, createdAt, name, email, img);
    }
}


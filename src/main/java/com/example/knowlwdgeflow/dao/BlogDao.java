package com.example.knowlwdgeflow.dao;

import com.example.knowlwdgeflow.Database;
import com.example.knowlwdgeflow.model.Blog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BlogDao {

    public Blog insert(int userId, String title, String content) throws SQLException {
        String sql = "INSERT INTO blogs(user_id, title, content) VALUES(?,?,?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setString(2, title);
            ps.setString(3, content);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    return findById(id);
                }
            }
            throw new SQLException("Failed to retrieve blog id");
        }
    }

    public Blog findById(int id) throws SQLException {
        String sql = "SELECT b.id, b.user_id, b.title, b.content, b.created_at, u.name, u.email, u.profile_image " +
                "FROM blogs b JOIN users u ON u.id = b.user_id WHERE b.id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapBlog(rs);
                }
            }
            return null;
        }
    }

    public List<Blog> findRecent(int limit) throws SQLException {
        String sql = "SELECT b.id, b.user_id, b.title, b.content, b.created_at, u.name, u.email, u.profile_image " +
                "FROM blogs b JOIN users u ON u.id = b.user_id " +
                "ORDER BY b.created_at DESC LIMIT ?";
        List<Blog> blogs = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    blogs.add(mapBlog(rs));
                }
            }
        }
        return blogs;
    }

    public List<Blog> findByUser(int userId) throws SQLException {
        String sql = "SELECT b.id, b.user_id, b.title, b.content, b.created_at, u.name, u.email, u.profile_image " +
                "FROM blogs b JOIN users u ON u.id = b.user_id WHERE b.user_id = ? ORDER BY b.created_at DESC";
        List<Blog> blogs = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    blogs.add(mapBlog(rs));
                }
            }
        }
        return blogs;
    }

    private Blog mapBlog(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int userId = rs.getInt("user_id");
        String title = rs.getString("title");
        String content = rs.getString("content");
        Timestamp ts = rs.getTimestamp("created_at");
        LocalDateTime createdAt = ts != null ? ts.toLocalDateTime() : null;
        String authorName = rs.getString("name");
        String authorEmail = rs.getString("email");
        byte[] authorImage = rs.getBytes("profile_image");
        return new Blog(id, userId, title, content, createdAt, authorName, authorEmail, authorImage);
    }
}

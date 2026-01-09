package com.example.knowlwdgeflow.dao;

import com.example.knowlwdgeflow.Database;
import com.example.knowlwdgeflow.model.Comment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommentDao {
    public void addComment(int blogId, int userId, String content) throws SQLException {
        String sql = "INSERT INTO comments(blog_id, user_id, content) VALUES(?,?,?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, blogId);
            ps.setInt(2, userId);
            ps.setString(3, content);
            ps.executeUpdate();
        }
    }

    public List<Comment> findByBlog(int blogId) throws SQLException {
        String sql = "SELECT c.id, c.blog_id, c.user_id, c.content, c.created_at, u.name, u.profile_image " +
                "FROM comments c JOIN users u ON u.id = c.user_id WHERE c.blog_id = ? ORDER BY c.created_at DESC";
        List<Comment> list = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, blogId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp ts = rs.getTimestamp("created_at");
                    LocalDateTime created = ts != null ? ts.toLocalDateTime() : null;
                    list.add(new Comment(
                            rs.getInt("id"),
                            rs.getInt("blog_id"),
                            rs.getInt("user_id"),
                            rs.getString("name"),
                            rs.getBytes("profile_image"),
                            rs.getString("content"),
                            created
                    ));
                }
            }
        }
        return list;
    }

    public int countByBlog(int blogId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM comments WHERE blog_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, blogId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
}

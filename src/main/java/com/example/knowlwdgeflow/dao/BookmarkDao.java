package com.example.knowlwdgeflow.dao;

import com.example.knowlwdgeflow.Database;
import com.example.knowlwdgeflow.model.Bookmark;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookmarkDao {
    public void addBookmark(int userId, int blogId) throws SQLException {
        String sql = "INSERT OR IGNORE INTO bookmarks(user_id, blog_id) VALUES(?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, blogId);
            ps.executeUpdate();
        }
    }

    public void removeBookmark(int userId, int blogId) throws SQLException {
        String sql = "DELETE FROM bookmarks WHERE user_id = ? AND blog_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, blogId);
            ps.executeUpdate();
        }
    }

    public boolean isBookmarked(int userId, int blogId) throws SQLException {
        String sql = "SELECT 1 FROM bookmarks WHERE user_id = ? AND blog_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, blogId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<Bookmark> findByUser(int userId) throws SQLException {
        String sql = "SELECT b.id as blog_id, b.title, b.content, b.created_at, u.name as author_name, u.profile_image as author_image " +
                "FROM bookmarks bk JOIN blogs b ON bk.blog_id = b.id JOIN users u ON b.user_id = u.id WHERE bk.user_id = ? ORDER BY b.created_at DESC";
        List<Bookmark> list = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Bookmark(
                            rs.getInt("blog_id"),
                            rs.getString("title"),
                            rs.getString("author_name"),
                            rs.getBytes("author_image"),
                            rs.getTimestamp("created_at").toLocalDateTime(),
                            rs.getString("content")
                    ));
                }
            }
        }
        return list;
    }
}

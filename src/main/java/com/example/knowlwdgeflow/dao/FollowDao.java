package com.example.knowlwdgeflow.dao;

import com.example.knowlwdgeflow.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FollowDao {
    public boolean isFollowing(int followerId, int followeeId) throws SQLException {
        String sql = "SELECT 1 FROM followers WHERE follower_id = ? AND followee_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, followerId);
            ps.setInt(2, followeeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void follow(int followerId, int followeeId) throws SQLException {
        String sql = "INSERT OR IGNORE INTO followers(follower_id, followee_id) VALUES(?,?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, followerId);
            ps.setInt(2, followeeId);
            ps.executeUpdate();
        }
    }

    public void unfollow(int followerId, int followeeId) throws SQLException {
        String sql = "DELETE FROM followers WHERE follower_id = ? AND followee_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, followerId);
            ps.setInt(2, followeeId);
            ps.executeUpdate();
        }
    }

    public int countFollowers(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM followers WHERE followee_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
}


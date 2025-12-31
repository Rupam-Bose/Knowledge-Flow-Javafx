package com.example.knowlwdgeflow;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class Database {
    private static final String DB_DIR = "data";
    private static final String DB_FILE = "knowlwdgeflow.db";
    private static final String JDBC_URL = "jdbc:sqlite:" + DB_DIR + "/" + DB_FILE;

    private Database() {}

    public static Connection getConnection() throws SQLException {
        loadDriver();
        ensureDb();
        return DriverManager.getConnection(JDBC_URL);
    }

    private static void loadDriver() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite driver not found", e);
        }
    }

    private static void ensureDb() {
        try {
            Path dir = Path.of(DB_DIR);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
            try (Connection conn = DriverManager.getConnection(JDBC_URL)) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "name TEXT NOT NULL," +
                            "email TEXT NOT NULL UNIQUE," +
                            "password TEXT NOT NULL," +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                            ")");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database: " + e.getMessage(), e);
        }
    }
}

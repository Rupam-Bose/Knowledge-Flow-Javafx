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
                            "profile_image BLOB," +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                            ")");
                    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS blogs (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "user_id INTEGER NOT NULL," +
                            "title TEXT NOT NULL," +
                            "content TEXT NOT NULL," +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                            "FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE" +
                            ")");
                    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS questions (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "user_id INTEGER NOT NULL," +
                            "text TEXT NOT NULL," +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                            "FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE" +
                            ")");
                    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS answers (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "question_id INTEGER NOT NULL," +
                            "user_id INTEGER NOT NULL," +
                            "content TEXT NOT NULL," +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                            "FOREIGN KEY(question_id) REFERENCES questions(id) ON DELETE CASCADE," +
                            "FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE" +
                            ")");
                    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS comments (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "blog_id INTEGER NOT NULL," +
                            "user_id INTEGER NOT NULL," +
                            "content TEXT NOT NULL," +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                            "FOREIGN KEY(blog_id) REFERENCES blogs(id) ON DELETE CASCADE," +
                            "FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE" +
                            ")");
                    stmt.executeUpdate("CREATE INDEX IF NOT EXISTS idx_blogs_created_at ON blogs(created_at DESC)");
                    stmt.executeUpdate("CREATE INDEX IF NOT EXISTS idx_questions_created_at ON questions(created_at DESC)");
                    stmt.executeUpdate("CREATE INDEX IF NOT EXISTS idx_answers_question_id ON answers(question_id)");
                    stmt.executeUpdate("CREATE INDEX IF NOT EXISTS idx_comments_blog_id ON comments(blog_id)");
                }
                ensureProfileImageColumn(conn);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database: " + e.getMessage(), e);
        }
    }

    private static void ensureProfileImageColumn(Connection conn) {
        try (var stmt = conn.createStatement();
             var rs = stmt.executeQuery("PRAGMA table_info(users)")) {
            boolean hasColumn = false;
            while (rs.next()) {
                if ("profile_image".equalsIgnoreCase(rs.getString("name"))) {
                    hasColumn = true;
                    break;
                }
            }
            if (!hasColumn) {
                try (var alter = conn.createStatement()) {
                    alter.executeUpdate("ALTER TABLE users ADD COLUMN profile_image BLOB");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to migrate profile image column", e);
        }
    }
}

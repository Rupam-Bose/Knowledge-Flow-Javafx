package com.example.knowlwdgeflow.model;

import java.time.LocalDateTime;

public class Question {
    private final int id;
    private final int userId;
    private final String text;
    private final LocalDateTime createdAt;
    private final String authorName;
    private final String authorEmail;
    private final byte[] authorImage;

    public Question(int id, int userId, String text, LocalDateTime createdAt,
                    String authorName, String authorEmail, byte[] authorImage) {
        this.id = id;
        this.userId = userId;
        this.text = text;
        this.createdAt = createdAt;
        this.authorName = authorName;
        this.authorEmail = authorEmail;
        this.authorImage = authorImage;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public byte[] getAuthorImage() {
        return authorImage;
    }
}


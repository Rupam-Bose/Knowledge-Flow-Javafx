package com.example.knowlwdgeflow.model;

import java.time.LocalDateTime;

public class Blog {
    private final int id;
    private final int userId;
    private final String title;
    private final String content;
    private final LocalDateTime createdAt;
    private final String authorName;
    private final String authorEmail;
    private final byte[] authorImage;

    public Blog(int id, int userId, String title, String content, LocalDateTime createdAt,
                String authorName, String authorEmail, byte[] authorImage) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.content = content;
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

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
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


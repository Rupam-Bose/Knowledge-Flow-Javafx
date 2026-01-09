package com.example.knowlwdgeflow.model;

import java.time.LocalDateTime;

public class Comment {
    private final int id;
    private final int blogId;
    private final int userId;
    private final String userName;
    private final byte[] userImage;
    private final String content;
    private final LocalDateTime createdAt;

    public Comment(int id, int blogId, int userId, String userName, byte[] userImage, String content, LocalDateTime createdAt) {
        this.id = id;
        this.blogId = blogId;
        this.userId = userId;
        this.userName = userName;
        this.userImage = userImage;
        this.content = content;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public int getBlogId() { return blogId; }
    public int getUserId() { return userId; }
    public String getUserName() { return userName; }
    public byte[] getUserImage() { return userImage; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}


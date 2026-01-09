package com.example.knowlwdgeflow.model;

import java.time.LocalDateTime;

public class Bookmark {
    private final int blogId;
    private final String title;
    private final String authorName;
    private final byte[] authorImage;
    private final LocalDateTime createdAt;
    private final String content;

    public Bookmark(int blogId, String title, String authorName, byte[] authorImage, LocalDateTime createdAt, String content) {
        this.blogId = blogId;
        this.title = title;
        this.authorName = authorName;
        this.authorImage = authorImage;
        this.createdAt = createdAt;
        this.content = content;
    }

    public int getBlogId() { return blogId; }
    public String getTitle() { return title; }
    public String getAuthorName() { return authorName; }
    public byte[] getAuthorImage() { return authorImage; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getContent() { return content; }
}

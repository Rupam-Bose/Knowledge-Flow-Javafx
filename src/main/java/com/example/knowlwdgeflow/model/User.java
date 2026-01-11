package com.example.knowlwdgeflow.model;

public class User {
    private final int id;
    private final String name;
    private final String email;
    private final byte[] profileImage;
    private final Integer followerCount;

    public User(int id, String name, String email, byte[] profileImage) {
        this(id, name, email, profileImage, null);
    }

    public User(int id, String name, String email, byte[] profileImage, Integer followerCount) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.profileImage = profileImage;
        this.followerCount = followerCount;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public byte[] getProfileImage() {
        return profileImage;
    }

    public Integer getFollowerCount() {
        return followerCount;
    }
}

package com.example.knowlwdgeflow.model;

public class User {
    private final int id;
    private final String name;
    private final String email;
    private final byte[] profileImage;

    public User(int id, String name, String email, byte[] profileImage) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.profileImage = profileImage;
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
}

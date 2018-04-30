package com.example.android.likeeatapplication.Model;

/**
 * Created by Leonardo on 4/6/2018.
 */

public class User {
    String userId;
    String username;
    String email;
    String nama;

    public User() {
    }

    public User(String userId, String username, String email, String nama) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.nama = nama;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getNama() {
        return nama;
    }
}

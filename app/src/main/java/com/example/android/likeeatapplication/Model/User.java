package com.example.android.likeeatapplication.Model;

import java.io.Serializable;

/**
 * Created by Leonardo on 4/6/2018.
 */

public class User implements Serializable {
    private String userId;
    private String name;
    private String email;
    private String phone;
    private String profile_pic;
    private String ttl;

    public User() {
    }

    public User(String userId, String name, String email, String phone, String profile_pic, String ttl) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.profile_pic = profile_pic;
        this.ttl = ttl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getTtl() {
        return ttl;
    }

    public void setTtl(String ttl) {
        this.ttl = ttl;
    }
}

package com.example.android.likeeatapplication.Model;

/**
 * Created by Leonardo on 4/21/2018.
 */

public class Like {
    //model untuk class comment
    //attribut2 apa aja yang ada di class comment
    String id;
    String username;

    //construcor kosong
    public Like() {
    }

    public Like(String id, String username) {
        this.id = id;
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
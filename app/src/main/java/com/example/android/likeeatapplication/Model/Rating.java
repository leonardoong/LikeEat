package com.example.android.likeeatapplication.Model;

/**
 * Created by Leonardo on 4/21/2018.
 */

public class Rating {
    //model untuk class comment
    //attribut2 apa aja yang ada di class comment
    String id;
    String username;
    Float rating;

    //construcor kosong
    public Rating() {
    }

    public Rating(String id, String username, Float rating) {
        this.id = id;
        this.username = username;
        this.rating = rating;
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

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }
}
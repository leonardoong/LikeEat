package com.example.android.likeeatapplication.Model;

/**
 * Created by Leonardo on 10/16/2018.
 */

public class Restaurant {
    private String name;
    private String address;
    private String rating;
    private String userID;
    private String username;
    private String imageUrl;
    private double longitude;
    private double latitiude;

    public Restaurant() {
    }

    public Restaurant(String name, String address, String rating, String userID, String username, String imageUrl, double lng, double lat) {
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.userID = userID;
        this.username = username;
        this.imageUrl = imageUrl;
        this.longitude = lng;
        this.latitiude = lat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitiude() {
        return latitiude;
    }

    public void setLatitiude(double latitiude) {
        this.latitiude = latitiude;
    }
}

package com.example.android.likeeatapplication.Model;

import java.security.PublicKey;

/**
 * Created by Leonardo on 9/18/2018.
 */

public class Coord {
    private double lon;
    private double lat;

    public Coord() {
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }


    @Override
    public String toString() {
        return new StringBuilder("[").append(this.lat).append(",").append(this.lon).append("]").toString();
    }
}

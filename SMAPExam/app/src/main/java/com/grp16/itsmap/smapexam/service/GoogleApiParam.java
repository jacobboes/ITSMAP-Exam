package com.grp16.itsmap.smapexam.service;


public class GoogleApiParam {
    private double latitude;
    private double longitude;
    private double radius;
    private String type;

    public GoogleApiParam (double latitude, double longitude, double radius, String type) {

        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.type = type;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getRadius() {
        return radius;
    }

    public String getType() {
        return type;
    }
}
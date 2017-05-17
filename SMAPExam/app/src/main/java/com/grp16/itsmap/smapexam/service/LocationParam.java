package com.grp16.itsmap.smapexam.service;


public class LocationParam {
    private double latitude;
    private double longitude;
    private int radius;
    private String type;

    public LocationParam(double latitude, double longitude, int radius, String type) {

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

    public int getRadius() {
        return radius;
    }

    public String getType() {
        return type;
    }
}
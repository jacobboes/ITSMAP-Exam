package com.grp16.itsmap.smapexam.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class POI {
    public double latitude;
    public double longitude;
    public double altitude;
    public String name;
    public String description;

    public POI() {

    }

    public POI(double latitude, double longitude, double altitude, String name, String description) {

        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.name = name;
        this.description = description;
    }
}

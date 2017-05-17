package com.grp16.itsmap.smapexam.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class POI {
    public String uid;
    public double latitude;
    public double longitude;
    public double altitude;
    public String name;
    public String description;
    public String type;

    public POI() {

    }

    public POI(String uid, double latitude, double longitude, double altitude, String name, String description, String type) {
        this.uid = uid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.name = name;
        this.description = description;
        this.type = type;
    }
}

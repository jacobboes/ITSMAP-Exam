package com.grp16.itsmap.smapexam.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

@IgnoreExtraProperties
public class POI {
    public String uid;
    public double latitude;
    public double longitude;
    public double altitude;
    public String name;
    public String vicinity;
    public List<String> type;

    public POI() {

    }

    public POI(String uid, double latitude, double longitude, String name, String vicinity, List<String> type) {
        this.uid = uid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.vicinity = vicinity;
        this.type = type;
    }
}

package com.grp16.itsmap.smapexam.util;

import android.graphics.Color;

import java.util.HashMap;

public class AppUtil {
    public static final int MY_RADIUS = 500;

    public static final String BROADCAST_LOCATION_CHANGED = "1001";
    public static final String BROADCAST_TYPE_CHANGED = "1002";
    public static final String GOOGLE_PLACES_KEY = "AIzaSyBdI_z2yg9_2x--UDr_IqGiAJ7A2iUt_hQ";
    public static final String GOOGLE_PLACES_API = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";

    public static final String GOOGLE_MAPS_KEY = "AIzaSyCUEm_dhSGGrtpcBh44tCJpbAAQCzYyszI";
    public static final String GOOGLE_MAPS_API = "https://maps.googleapis.com/maps/api/elevation/json/";

    public static HashMap<String, Integer> getPoiColorMapping(){
        HashMap<String, Integer> poiColorMapping= new HashMap<>();
        poiColorMapping.put("cafe", Color.BLACK);
        poiColorMapping.put("restaurant", Color.GREEN);
        poiColorMapping.put("gym", Color.BLUE);
        poiColorMapping.put("library", Color.YELLOW);
        poiColorMapping.put("gas_station", Color.CYAN);
        poiColorMapping.put("store", Color.RED);
        return poiColorMapping;
    }
}
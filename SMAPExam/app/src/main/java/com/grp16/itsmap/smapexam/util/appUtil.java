package com.grp16.itsmap.smapexam.util;

public class appUtil {
    public static String MY_LOCATION = "";
    public static String MY_RADIUS = "";
    public static String MY_TYPE = "";
    public static final String BROADCAST_LOCATION_CHANGED = "1001";
    public static final String GOOGLE_PLACES_KEY = "AIzaSyBdI_z2yg9_2x--UDr_IqGiAJ7A2iUt_hQ";
    public static final String GOOGLE_PLACES_API = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + MY_LOCATION + "&radius=" + MY_RADIUS + "&types=" + MY_TYPE + "&key=" + GOOGLE_PLACES_KEY;
}

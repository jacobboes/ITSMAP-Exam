package com.grp16.itsmap.smapexam.util;

import android.graphics.Color;

import com.grp16.itsmap.smapexam.R;

public class AppUtil {
    public static final int MY_RADIUS = 500;

    public static final String BROADCAST_LOCATION_CHANGED = "1001";
    public static final String BROADCAST_TYPE_CHANGED = "1002";
    public static final String GOOGLE_PLACES_KEY = "AIzaSyBdI_z2yg9_2x--UDr_IqGiAJ7A2iUt_hQ";
    public static final String GOOGLE_PLACES_API = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";

    public static final String GOOGLE_MAPS_KEY = "AIzaSyCUEm_dhSGGrtpcBh44tCJpbAAQCzYyszI";
    public static final String GOOGLE_MAPS_API = "https://maps.googleapis.com/maps/api/elevation/json/";

    public enum poiTypeMapping
    {
        cafe(R.string.cafe, Color.BLACK, "cafe"),
        restaurant(R.string.restaurant, Color.GREEN, "restaurant"),
        gym(R.string.gym, Color.BLUE, "gym"),
        library(R.string.library, Color.YELLOW, "library"),
        gas_station(R.string.gas_station, Color.CYAN, "gas_station"),
        store(R.string.store, Color.RED, "store");

        private int res;
        private int color;
        private String val;

        poiTypeMapping(int res, int color, String val){

            this.res = res;
            this.color = color;
            this.val = val;
        }

        public int getRes() {
            return res;
        }

        public int getColor() {
            return color;
        }

        public String getVal() {
            return val;
        }
    }
}
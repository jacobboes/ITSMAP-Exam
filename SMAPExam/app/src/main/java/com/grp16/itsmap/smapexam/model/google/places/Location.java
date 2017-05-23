
package com.grp16.itsmap.smapexam.model.google.places;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Location implements Comparable {

    @SerializedName("lat")
    @Expose
    public Double lat;
    @SerializedName("lng")
    @Expose
    public Double lng;

    @Override
    public int compareTo(@NonNull Object o) {
        return 0;
    }
}

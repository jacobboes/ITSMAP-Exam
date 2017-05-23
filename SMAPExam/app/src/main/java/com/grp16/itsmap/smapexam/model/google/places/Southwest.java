
package com.grp16.itsmap.smapexam.model.google.places;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Southwest {

    @SerializedName("lat")
    @Expose
    public Double lat;
    @SerializedName("lng")
    @Expose
    public Double lng;

}

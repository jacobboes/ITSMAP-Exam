
package com.grp16.itsmap.smapexam.model.google.elevation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.grp16.itsmap.smapexam.model.google.places.Location;

public class AltResult {

    @SerializedName("elevation")
    @Expose
    public Integer elevation;
    @SerializedName("location")
    @Expose
    public Location location;
    @SerializedName("resolution")
    @Expose
    public Double resolution;

}

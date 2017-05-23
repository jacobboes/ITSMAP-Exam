
package com.grp16.itsmap.smapexam.model.google.places;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Geometry {

    @SerializedName("location")
    @Expose
    public Location location;
    @SerializedName("viewport")
    @Expose
    public Viewport viewport;

}

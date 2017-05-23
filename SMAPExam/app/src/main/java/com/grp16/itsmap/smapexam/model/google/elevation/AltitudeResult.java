
package com.grp16.itsmap.smapexam.model.google.elevation;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AltitudeResult {

    @SerializedName("results")
    @Expose
    public List<AltResult> results = null;
    @SerializedName("status")
    @Expose
    public String status;

}

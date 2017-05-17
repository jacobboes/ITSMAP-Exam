
package com.grp16.itsmap.smapexam.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OpeningHours {

    @SerializedName("open_now")
    @Expose
    public Boolean openNow;
    @SerializedName("weekday_text")
    @Expose
    public List<Object> weekdayText = null;

}

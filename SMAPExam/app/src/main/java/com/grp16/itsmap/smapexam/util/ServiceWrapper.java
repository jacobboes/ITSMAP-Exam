package com.grp16.itsmap.smapexam.util;

import com.grp16.itsmap.smapexam.model.POI;
import android.location.Location;

import java.util.List;

public interface ServiceWrapper {
    List<POI> getPoiList();
    Location getLocation();
}

package com.grp16.itsmap.smapexam.util;

import android.location.Location;

import com.grp16.itsmap.smapexam.model.POI;
import java.util.List;

public interface PoiListener {
    void dataReady(List<POI> data, Location location);
}

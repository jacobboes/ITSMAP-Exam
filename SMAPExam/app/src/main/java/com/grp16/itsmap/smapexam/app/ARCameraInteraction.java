package com.grp16.itsmap.smapexam.app;

import com.grp16.itsmap.smapexam.util.PoiListener;
import com.grp16.itsmap.smapexam.util.ServiceWrapper;

public interface ARCameraInteraction extends ServiceWrapper {
    void addListener(PoiListener listener);

    void removeListener(PoiListener listener);

    int getOrientation();
}

package com.grp16.itsmap.smapexam.model;


import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class UserCustomInfo {
    public String uid;
    public List<String> poiType;
    public List<POI> myPoi;

    public UserCustomInfo(){
        poiType = new ArrayList<>();
        myPoi = new ArrayList<>();
    }

    public UserCustomInfo(String uid, List<String> poiType, List<POI> myPoi){
        this.uid = uid;
        this.poiType = poiType;
        this.myPoi = myPoi;
    }
}

package com.grp16.itsmap.smapexam.model;


import java.util.List;

public class UserCustomInfo {
    public String uid;
    public List<String> poiType;
    public List<POI> myPoi;

    public UserCustomInfo(){

    }

    public UserCustomInfo(String uid, List<String> poiType, List<POI> myPoi){
        this.uid = uid;
        this.poiType = poiType;
        this.myPoi = myPoi;
    }
}

package com.grp16.itsmap.smapexam.model;


import java.util.List;

public class UserCustomInfo {
    public String uid;
    public List<String> poiType;

    public UserCustomInfo(){

    }

    public UserCustomInfo(String uid, List<String> poiType){
        this.uid = uid;
        this.poiType = poiType;
    }
}

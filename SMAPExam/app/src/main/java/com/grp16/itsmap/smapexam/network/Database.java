package com.grp16.itsmap.smapexam.network;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grp16.itsmap.smapexam.model.POI;

import java.util.ArrayList;
import java.util.List;

public class Database {

    private DatabaseReference poiDatabase;
    private final String POI_COLLECTION_NAME = "POI";
    private DataSnapshot POI;

    Database(){
        poiDatabase = FirebaseDatabase.getInstance().getReference(POI_COLLECTION_NAME);
        listener();
    }

    public void insertPoi(POI data){
        poiDatabase.push();
        //poiDatabase.child(UUID.randomUUID().toString()).setValue(data);
    }

    public void deletePoi(POI data){
        for (DataSnapshot singleSnapshot : POI.getChildren()){
            if (singleSnapshot.getKey().equals(data.uid)){
                singleSnapshot.getRef().removeValue();
                return;
            }
        }
    }

    public List<POI> getAllPOI(){
        List<POI> returnVal = new ArrayList();
        for (DataSnapshot singleSnapshot : POI.getChildren()){
            returnVal.add(singleSnapshot.getValue(POI.class));
        }
        return returnVal;
    }

    public List<POI> getPOI(double lat, double lon, double radius, List<String> type){
        
    }

    private void listener(){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                POI = dataSnapshot;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Database", "loadPost:onCancelled", databaseError.toException());
            }
        };
        poiDatabase.addValueEventListener(postListener);
    }
}

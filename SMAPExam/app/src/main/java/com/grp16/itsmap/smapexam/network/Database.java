package com.grp16.itsmap.smapexam.network;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grp16.itsmap.smapexam.model.POI;
import com.grp16.itsmap.smapexam.model.UserCustomInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Database {

    private DatabaseReference poiDatabase;
    private DatabaseReference userDatabase;
    private FirebaseAuth auth;
    private final String POI_COLLECTION_NAME = "POI";
    private final String USER_COLLECTION_NAME = "User";
    private DataSnapshot POI;
    private DataSnapshot User;

    Database() {
        poiDatabase = FirebaseDatabase.getInstance().getReference(POI_COLLECTION_NAME);
        auth = FirebaseAuth.getInstance();
        userDatabase = FirebaseDatabase.getInstance().getReference(USER_COLLECTION_NAME);
        listener();
    }

    public void insertUpdate(POI data) {
        if (data.uid == null)
            poiDatabase.child(UUID.randomUUID().toString()).setValue(data);
        else
            poiDatabase.child(data.uid).setValue(data);
    }

    public void insertUpdate(UserCustomInfo data){
        userDatabase.child(auth.getCurrentUser().getUid().toString()).setValue(data);
    }

    public void delete(POI data) {
        for (DataSnapshot singleSnapshot : POI.getChildren()) {
            if (singleSnapshot.getKey().equals(data.uid)) {
                singleSnapshot.getRef().removeValue();
                return;
            }
        }
    }

    public void delete(UserCustomInfo data) {
        for (DataSnapshot singleSnapshot : User.getChildren()) {
            if (singleSnapshot.getKey().equals(data.uid)) {
                singleSnapshot.getRef().removeValue();
                return;
            }
        }
    }

    public List<POI> getPOI() {
        List<POI> returnVal = new ArrayList();
        for (DataSnapshot singleSnapshot : POI.getChildren()) {
            returnVal.add(singleSnapshot.getValue(POI.class));
        }
        return returnVal;
    }

    public List<POI> getPOI(double lat, double lng, int radius, List<String> type) {
        List<POI> returnVal = new ArrayList();
        for (DataSnapshot singleSnapshot : POI.getChildren()) {
            POI tmp = singleSnapshot.getValue(POI.class);
            if (type.contains(tmp.type) && withinRadius(lat, lng, radius, tmp)){
                returnVal.add(tmp);
            }
        }
        return returnVal;
    }

    public List<String> getTypes(){
        for (DataSnapshot singleSnapshot : POI.getChildren()) {
            return singleSnapshot.getValue(UserCustomInfo.class).poiType;
        }
        return null;
    }

    private void listener() {
        ValueEventListener poiPostListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                POI = dataSnapshot;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Database", "loadPost:onCancelled", databaseError.toException());
            }
        };
        poiDatabase.addValueEventListener(poiPostListener);

        ValueEventListener userPostListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User = dataSnapshot;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Database", "loadPost:onCancelled", databaseError.toException());
            }
        };
        userDatabase.addValueEventListener(userPostListener);
    }

    //https://www.mullie.eu/geographic-searches/
    private boolean withinRadius(double lat, double lng, int radius, POI poi) {
        double phoneLat = Math.toRadians(lat);
        double phoneLng = Math.toRadians(lng);
        double poiLat = Math.toRadians(poi.latitude);
        double poiLng = Math.toRadians(poi.longitude);

        // earth's radius in km = ~6371
        double distance = Math.acos(Math.sin(phoneLat) * Math.sin(poiLat) + Math.cos(phoneLat) * Math.cos(poiLat) * Math.cos(phoneLng - poiLng)) * 6371;

        return distance <= radius / 1000;
    }
}

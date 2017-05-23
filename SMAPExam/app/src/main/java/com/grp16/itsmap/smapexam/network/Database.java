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
import com.grp16.itsmap.smapexam.service.LocationParam;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Database {

    private DatabaseReference poiDatabase;
    private DatabaseReference userDatabase;
    private FirebaseAuth auth;
    private final String POI_COLLECTION_NAME = "POI";
    private final String USER_COLLECTION_NAME = "User";
    private List<POI> poiList = new ArrayList();
    private UserCustomInfo user = new UserCustomInfo();

    private static final Database ourInstance = new Database();

    public static Database getInstance() {
        return ourInstance;
    }

    private Database() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);
        poiDatabase = database.getReference(POI_COLLECTION_NAME);
        auth = FirebaseAuth.getInstance();
        userDatabase = database.getReference(USER_COLLECTION_NAME);
        listener();
    }

    public void insertUpdate(POI data) {
        if (data.uid == null) {
            poiDatabase.child(UUID.randomUUID().toString()).setValue(data);
        } else {
            poiDatabase.child(data.uid).setValue(data);
        }
    }

    public void insertUpdate(UserCustomInfo data) {
        userDatabase.child(auth.getCurrentUser().getUid().toString()).setValue(data);
    }

    public void delete(POI data) {
        poiDatabase.child(data.uid).removeValue();
    }

    public void delete(UserCustomInfo data) {
        userDatabase.child(data.uid).removeValue();
    }

    public List<POI> getPOI() {
        return poiList;
    }

    public List<POI> getPOI(LocationParam data) {
        List<POI> returnVal = new ArrayList();
        for (POI singlePoi : poiList) {
            if (singlePoi.type.contains(data.getType()) && withinRadius(data, singlePoi)) {
                returnVal.add(singlePoi);
            }
        }
        return returnVal;
    }

    public List<String> getUserSelectedTypes() {
        return user.poiType;
    }

    private void listener() {
        ValueEventListener poiPostListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<POI> returnval = new ArrayList();
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    returnval.add(messageSnapshot.getValue(POI.class));
                }
                poiList = returnval;
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
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    UserCustomInfo tmp = messageSnapshot.getValue(UserCustomInfo.class);
                    if (tmp.uid == auth.getCurrentUser().getUid().toString()) {
                        user = tmp;
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Database", "loadPost:onCancelled", databaseError.toException());
            }
        };
        userDatabase.addValueEventListener(userPostListener);
    }

    //https://www.mullie.eu/geographic-searches/
    private boolean withinRadius(LocationParam location, POI poi) {
        double phoneLat = Math.toRadians(location.getLatitude());
        double phoneLng = Math.toRadians(location.getLongitude());
        double poiLat = Math.toRadians(poi.latitude);
        double poiLng = Math.toRadians(poi.longitude);

        // earth's radius in km = ~6371
        double distance = Math.acos(Math.sin(phoneLat) * Math.sin(poiLat) + Math.cos(phoneLat) * Math.cos(poiLat) * Math.cos(phoneLng - poiLng)) * 6371 * 1000;

        return distance <= location.getRadius();
    }
}

package com.grp16.itsmap.smapexam.network;

import android.content.Context;
import android.content.Intent;
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
import com.grp16.itsmap.smapexam.util.AppUtil;

import java.util.ArrayList;
import java.util.List;

public class Database {

    private DatabaseReference userDatabase;
    private FirebaseAuth auth;
    private Context context;
    private final String USER_COLLECTION_NAME = "User";
    private UserCustomInfo user = new UserCustomInfo();

    private static final Database ourInstance = new Database();

    public static Database getInstance() {
        return ourInstance;
    }

    private Database() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);
        auth = FirebaseAuth.getInstance();
        userDatabase = database.getReference(USER_COLLECTION_NAME);
        listener();
    }

    public void setContext(Context context){
        this.context = context;
    }

    public void insertUpdate(POI data) {
        if (user.myPoi.contains(data)) {
            user.myPoi.remove(data);
        }
        user.myPoi.add(data);
        userDatabase.child(auth.getCurrentUser().getUid().toString()).setValue(user);
    }

    public void insertUpdate(List<String> types) {
        user.poiType = types;
        userDatabase.child(auth.getCurrentUser().getUid().toString()).setValue(user);
    }

    public void delete(POI data) {
        user.myPoi.remove(data);
        userDatabase.child(auth.getCurrentUser().getUid().toString()).setValue(user);
    }

    public List<POI> getPOI() {
        return user.myPoi;
    }

    public List<POI> getPOI(LocationParam data) {
        List<POI> returnVal = new ArrayList();
        for (POI singlePoi : user.myPoi) {
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
        ValueEventListener userPostListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    UserCustomInfo tmp = messageSnapshot.getValue(UserCustomInfo.class);
                    if (tmp.uid.equals(auth.getCurrentUser().getUid().toString())) {
                        user = tmp;
                        if (context != null){
                            Intent broadcastPOI = new Intent();
                            broadcastPOI.setAction(AppUtil.BROADCAST_TYPE_CHANGED);
                            context.sendBroadcast(broadcastPOI);
                        }
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

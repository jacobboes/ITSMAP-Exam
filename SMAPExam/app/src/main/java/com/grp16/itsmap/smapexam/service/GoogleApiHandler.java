package com.grp16.itsmap.smapexam.service;


import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.grp16.itsmap.smapexam.model.POI;
import com.grp16.itsmap.smapexam.model.google.GooglePoi;
import com.grp16.itsmap.smapexam.model.google.Result;
import com.grp16.itsmap.smapexam.util.AppUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class GoogleApiHandler extends AsyncTask<LocationParam, Void, List<POI>> {

    @Override
    protected List<POI> doInBackground(LocationParam... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        List<POI> PointsOfInterest;

        // Raw JSON response as String
        String JsonStr;
        Log.i("Service", "Background call to Places API");
        try {
            // Construct the URL for Places query and open connection

            LocationParam location = params[0];
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .authority("maps.googleapis.com")
                    .appendPath("maps")
                    .appendPath("api")
                    .appendPath("place")
                    .appendPath("nearbysearch")
                    .appendPath("json")
                    .appendQueryParameter("location", location.getLatitude() + "," + location.getLongitude())
                    .appendQueryParameter("radius", String.valueOf(location.getRadius()))
                    .appendQueryParameter("type", location.getType())
                    .appendQueryParameter("key", AppUtil.GOOGLE_PLACES_KEY);

            URL url = new URL(builder.build().toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            // Read input into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return Collections.emptyList();
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return Collections.emptyList();
            }

            JsonStr = buffer.toString();
            PointsOfInterest = JsonToGooglePoi(JsonStr);
            return PointsOfInterest;
        } catch (IOException e) {
            Log.e("PlaceholderFragment", "Error ", e);
            return Collections.emptyList();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error while closing stream", e);
                }
            }
        }
    }

    private List<POI> JsonToGooglePoi(String s) {
        Gson gson = new GsonBuilder().create();
        GooglePoi googlePoi = gson.fromJson(s, GooglePoi.class);
        List<POI> returnList = new ArrayList<>();
        for (Result result : googlePoi.results) {
            returnList.add(new POI(UUID.randomUUID().toString(), result.geometry.location.lat,
                    result.geometry.location.lat, result.name, result.vicinity, result.types));
        }
        return returnList;
    }
}

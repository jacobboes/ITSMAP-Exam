package com.grp16.itsmap.smapexam.service;


import android.os.AsyncTask;
import android.util.Log;

import com.grp16.itsmap.smapexam.model.POI;
import com.grp16.itsmap.smapexam.util.appUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class GoogleApiHandler extends AsyncTask<GoogleApiParam, Void, List<POI>>{

    @Override
    protected List<POI> doInBackground(GoogleApiParam... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        List<POI> PointsOfInterest;

        // Raw JSON response as String
        String JsonStr;
        Log.i("Service", "Background call to Places API");
        try {
            // Construct the URL for Places query and open connection
            URL url = new URL(appUtil.GOOGLE_PLACES_API);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read input into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            if (buffer.length() == 0) {
                return null;
            }
            JsonStr = buffer.toString();
            // Convert to GSON

            // Convert to POI afterwards

            return PointsOfInterest; // return POI objects

        } catch (IOException e) {
            Log.e("PlaceholderFragment", "Error ", e);
            return null;
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
}

package com.grp16.itsmap.smapexam.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.grp16.itsmap.smapexam.R;
import com.grp16.itsmap.smapexam.model.POI;

public class DetailsFragment extends Fragment {
    private static POI poi;

    public static DetailsFragment newInstance(POI poi) {
        DetailsFragment.poi = poi;
        return new DetailsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_details, container, false);

        TextView displayName = (TextView) view.findViewById(R.id.display_name);
        TextView displayVicinity = (TextView) view.findViewById(R.id.display_vicinity);
        TextView displayTypes = (TextView) view.findViewById(R.id.display_types);

        displayName.setText(poi.name);
        displayVicinity.setText(poi.vicinity);
        for (String s : poi.type) {
            displayTypes.append(s);
            displayTypes.append(", ");
        }

        Button btnGoogleMaps = (Button) view.findViewById(R.id.btn_gmm);
        btnGoogleMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchGoogleMaps();
            }
        });
        return view;
    }

    private void launchGoogleMaps() {
        //Uri uri = Uri.parse("geo:" + poi.latitude + "," + poi.longitude + "?q=" + poi.vicinity);
        Uri uri = Uri.parse("geo:" + poi.latitude + "," + poi.longitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }
}

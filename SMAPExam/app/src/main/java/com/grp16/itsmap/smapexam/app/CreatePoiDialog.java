package com.grp16.itsmap.smapexam.app;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.grp16.itsmap.smapexam.R;
import com.grp16.itsmap.smapexam.model.POI;
import com.grp16.itsmap.smapexam.network.Database;
import com.grp16.itsmap.smapexam.util.AppUtil;
import com.grp16.itsmap.smapexam.util.ServiceWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreatePoiDialog {
    private Spinner spinner;
    private EditText vicinity;
    private EditText name;
    private Context context;
    private ServiceWrapper service;
    private Dialog alertDialog;

    public CreatePoiDialog(Context context, ServiceWrapper service) {
        this.context = context;
        this.service = service;

        LayoutInflater li = LayoutInflater.from(context);
        View dialogView = li.inflate(R.layout.create_poi_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        name = (EditText) dialogView.findViewById(R.id.create_name);
        vicinity = (EditText) dialogView.findViewById(R.id.create_vicinity);
        spinner = (Spinner) dialogView.findViewById(R.id.create_typespinner);

        AppUtil.PoiTypeMapping[] values = AppUtil.PoiTypeMapping.values();
        List<TypeWrapper> types = new ArrayList<>();
        for (AppUtil.PoiTypeMapping value : values) {
            types.add(new TypeWrapper(value));
        }
        ArrayAdapter dataAdapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, types);
        spinner.setAdapter(dataAdapter);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onOkClicked();
            }
        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        alertDialog = builder.create();
    }

    public void show() {
        alertDialog.show();
    }

    private void onOkClicked() {
        TypeWrapper selectedItem = (TypeWrapper) spinner.getSelectedItem();
        if (name.getText() != null && !name.getText().toString().equals("")) {
            POI newPoi = new POI();
            newPoi.name = name.getText().toString();
            newPoi.vicinity = vicinity.getText().toString();
            Location location = service.getLocation();
            newPoi.altitude = location.getAltitude();
            newPoi.latitude = location.getLatitude();
            newPoi.longitude = location.getLongitude();
            newPoi.type = Collections.singletonList(selectedItem.getType().getValue());
            Database.getInstance().insertUpdate(newPoi);
        } else {
            errorCreatingPoi();
        }
    }

    private void errorCreatingPoi() {
        Toast.makeText(context, "Error: name empty.", Toast.LENGTH_SHORT).show();
    }

    private class TypeWrapper {
        private AppUtil.PoiTypeMapping type;

        public TypeWrapper(AppUtil.PoiTypeMapping type) {
            this.type = type;
        }

        public AppUtil.PoiTypeMapping getType() {
            return type;
        }

        @Override
        public String toString() {
            return context.getString(type.getRes());
        }
    }
}

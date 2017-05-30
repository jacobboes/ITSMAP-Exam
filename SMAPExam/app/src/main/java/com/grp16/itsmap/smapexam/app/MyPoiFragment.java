package com.grp16.itsmap.smapexam.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.grp16.itsmap.smapexam.R;
import com.grp16.itsmap.smapexam.model.POI;
import com.grp16.itsmap.smapexam.network.Database;

import java.util.List;


public class MyPoiFragment extends Fragment {
    private static Database database;

    public static MyPoiFragment newInstance(Database database) {
        MyPoiFragment.database = database;
        return new MyPoiFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_poi, container, false);
        final List<POI> myPoi = database.getPOI();

        ListView poiListView = (ListView) view.findViewById(R.id.my_poi_list_view);
        final ArrayAdapter adapter = new ArrayAdapter<POI>(view.getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, myPoi) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);

                text1.setText(myPoi.get(position).name);

                return view;
            }
        };

        poiListView.setAdapter(adapter);
        poiListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final POI poi = myPoi.get(position);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext())
                .setMessage(R.string.ConfirmDelete)
                .setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        database.delete(poi);
                        myPoi.remove(poi);
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton(getString(R.string.No),new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });


        return view;
    }
}

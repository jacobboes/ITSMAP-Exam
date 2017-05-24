package com.grp16.itsmap.smapexam.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.grp16.itsmap.smapexam.R;
import com.grp16.itsmap.smapexam.model.POI;
import com.grp16.itsmap.smapexam.util.ServiceWrapper;

import java.util.ArrayList;
import java.util.List;

public class TestServiceFragment extends Fragment {
    private ListView testList;
    private ArrayAdapter<String> adapter;
    private List<String> places = new ArrayList<>();

    private ServiceWrapper activity;

    public TestServiceFragment() {
        // Required empty public constructor
    }

    public static TestServiceFragment newInstance() {
        TestServiceFragment fragment = new TestServiceFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_test, null);

        testList = (ListView) view.findViewById(R.id.testList);
        adapter = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, places);
        testList.setAdapter(adapter);

        Button btn = (Button) view.findViewById(R.id.testInsert);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTestClicked();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ServiceWrapper) {
            activity = (ServiceWrapper) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement TestServiceInteraction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    private void onTestClicked() {
        if (activity != null) {
            List<POI> pois = activity.getPoiList();
            places.clear();
            for (POI poi : pois) {
                places.add(poi.name);
            }
            adapter.notifyDataSetChanged();
        }
    }
}

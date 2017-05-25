package com.grp16.itsmap.smapexam.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;

import com.grp16.itsmap.smapexam.R;

import java.util.ArrayList;
import java.util.List;

public class SelectTypesFragment extends Fragment {
    private AutoCompleteTextView type;
    private Button addType;
    private Button removeType;
    private ListView selectedTypesView;
    private ArrayAdapter<String> adapter;
    private List<String> selectedTypes;

    private SelectTypesInteraction activity;

    public SelectTypesFragment() {
        // Required empty public constructor
        selectedTypes = new ArrayList<>();
    }

    public static SelectTypesFragment newInstance() {
        return new SelectTypesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_types, container, false);

        type = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteType);
        selectedTypesView = (ListView) view.findViewById(R.id.selectedTypes);
        addType = (Button) view.findViewById(R.id.addType);
        removeType = (Button) view.findViewById(R.id.removeType);

        String[] types = getResources().getStringArray(R.array.poi_types);
        ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, types);
        type.setAdapter(autoCompleteAdapter);

        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, selectedTypes);
        selectedTypesView.setAdapter(adapter);

        addType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddClicked();
            }
        });
        return view;
    }

    private void onAddClicked() {
        selectedTypes.add(type.getText().toString());
        adapter.notifyDataSetChanged();
        type.setText("");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SelectTypesInteraction) {
            activity = (SelectTypesInteraction) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement SelectTypesInteraction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }
}

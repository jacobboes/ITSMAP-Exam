package com.grp16.itsmap.smapexam.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.grp16.itsmap.smapexam.R;
import com.grp16.itsmap.smapexam.network.Database;

import java.util.ArrayList;
import java.util.List;

public class SelectTypesFragment extends Fragment {

    private static Database database;
    private List<Type> types;

    public SelectTypesFragment() {
        types = new ArrayList<>();
    }

    public static SelectTypesFragment newInstance(Database database) {
        SelectTypesFragment.database = database;
        return new SelectTypesFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        List<String> userSelectedTypes = database.getUserSelectedTypes();
        for (Type type : types) {
            String value = type.getValue();
            type.setChecked(userSelectedTypes.contains(value));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        List<String> selectedType = new ArrayList<>();
        for (Type type : types) {
            if (type.isChecked()) {
                selectedType.add(type.getValue());
            }
        }
        database.insertUpdate(selectedType);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_types, container, false);
        types.add(new Type((CheckBox) view.findViewById(R.id.cafe), "cafe"));
        types.add(new Type((CheckBox) view.findViewById(R.id.restaurant), "restaurant"));
        types.add(new Type((CheckBox) view.findViewById(R.id.gym), "gym"));
        types.add(new Type((CheckBox) view.findViewById(R.id.library), "library"));
        types.add(new Type((CheckBox) view.findViewById(R.id.gasStation), "gas_station"));
        types.add(new Type((CheckBox) view.findViewById(R.id.store), "store"));

        return view;
    }
}

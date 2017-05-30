package com.grp16.itsmap.smapexam.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.grp16.itsmap.smapexam.R;
import com.grp16.itsmap.smapexam.network.Database;
import com.grp16.itsmap.smapexam.util.AppUtil;

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
        LinearLayout typeList = (LinearLayout) view.findViewById(R.id.linearLayout);

        for (AppUtil.PoiTypeMapping typeMapping : AppUtil.PoiTypeMapping.values()) {
            CheckBox checkBox = new CheckBox(inflater.getContext());
            checkBox.setText(typeMapping.getRes());

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 10, 10, 0);
            checkBox.setLayoutParams(params);

            typeList.addView(checkBox);
            types.add(new Type(checkBox, typeMapping.getValue()));
        }
        return view;
    }
}

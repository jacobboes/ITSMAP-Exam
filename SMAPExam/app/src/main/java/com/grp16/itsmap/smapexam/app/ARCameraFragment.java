package com.grp16.itsmap.smapexam.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.grp16.itsmap.smapexam.R;

public class ARCameraFragment extends Fragment {
    private ARCameraInteraction activity;

    public ARCameraFragment() {
        // Required empty public constructor
    }

    public static ARCameraFragment newInstance() {
        return new ARCameraFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_arcamera, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ARCameraInteraction) {
            activity = (ARCameraInteraction) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement ARCameraInteraction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }
}

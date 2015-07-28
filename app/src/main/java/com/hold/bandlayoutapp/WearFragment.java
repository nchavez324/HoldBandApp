package com.hold.bandlayoutapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A placeholder fragment containing a simple view.
 */
public class WearFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static WearFragment newInstance() {
        WearFragment fragment = new WearFragment();
        return fragment;
    }

    public WearFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_wear, container, false);

        Button button = (Button)rootView.findViewById(R.id.wear_start_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWear(v);
            }
        });

        return rootView;
    }

    public void startWear(View v)
    {
        Intent bandStreamIntent = new Intent(getActivity(), BandStreamingAppActivity.class);
        startActivity(bandStreamIntent);
    }
}
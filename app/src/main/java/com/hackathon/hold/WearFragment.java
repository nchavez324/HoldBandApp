package com.hackathon.hold;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hold.bandlayoutapp.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class WearFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private MainActivity mMainActivity;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static WearFragment newInstance(MainActivity mainActivity) {
        WearFragment fragment = new WearFragment();
        fragment.setMainActivity(mainActivity);
        return fragment;
    }

    public WearFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_wear, container, false);


        return rootView;
    }

    public void setMainActivity(MainActivity mainActivity)
    {
        mMainActivity = mainActivity;
    }

    public void startWear(View v)
    {
        Intent bandTileIntent = new Intent(getActivity(), BandTileEventAppActivity.class);
        startActivity(bandTileIntent);

    }
}
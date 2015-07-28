package com.hackathon.hold;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hold.bandlayoutapp.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class SettingsFragment extends Fragment {
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
    public static SettingsFragment newInstance(MainActivity mainActivity) {
        SettingsFragment fragment = new SettingsFragment();
        fragment.setMainActivity(mainActivity);
        return fragment;
    }

    public SettingsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);


        return rootView;
    }

    public void setMainActivity(MainActivity mainActivity)
    {
        mMainActivity = mainActivity;
    }
}
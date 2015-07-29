package com.hackathon.hold;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hold.bandlayoutapp.R;
import com.parse.ParseUser;

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

        Button mLogoutButton = (Button) rootView.findViewById(R.id.settings_logout);
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                Intent intent = new Intent(getActivity(),
                        LoginActivity.class);
                startActivity(intent);
            }
        });
        return rootView;
    }

    public void setMainActivity(MainActivity mainActivity)
    {
        mMainActivity = mainActivity;
    }
}
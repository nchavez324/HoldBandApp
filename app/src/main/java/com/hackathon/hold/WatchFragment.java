package com.hackathon.hold;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hold.bandlayoutapp.R;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseUser;

import java.util.HashMap;

/**
 * A placeholder fragment containing a simple view.
 */
public class WatchFragment extends Fragment {
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
    public static WatchFragment newInstance(MainActivity mainActivity) {
        WatchFragment fragment = new WatchFragment();
        fragment.setMainActivity(mainActivity);
        return fragment;
    }

    public WatchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_watch, container, false);


        return rootView;
    }

    public void setMainActivity(MainActivity mainActivity)
    {
        mMainActivity = mainActivity;
    }

    public void onGetUserId(String userId)
    {
        //do something to show map blah blah for current user

        // tell parse this current user is watching the user given by user id

        sendWatchSignal(userId);
    }

    private void sendWatchSignal(String userId)
    {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("pulse_user_id", userId);
        ParseCloud.callFunctionInBackground("onWatch", params, new FunctionCallback<Boolean>() {
            public void done(Boolean success, com.parse.ParseException e) {
                if (e == null) {
                    // ratings is 4.5
                }
            }
        });
    }
}
package com.hackathon.hold;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.hackathon.hold.MainActivity;
import com.hackathon.hold.SettingsFragment;
import com.hackathon.hold.WatchFragment;
import com.hackathon.hold.WearFragment;
import com.hold.bandlayoutapp.R;

import java.util.Locale;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private MainActivity mMainActivity;

    public SectionsPagerAdapter(FragmentManager fm, MainActivity mainActivity) {
        super(fm);
        mMainActivity = mainActivity;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        switch(position){

            case 0:
                return WatchFragment.newInstance(mMainActivity);
            case 1:
                return WearFragment.newInstance(mMainActivity);
            case 2:
                return SettingsFragment.newInstance(mMainActivity);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return mMainActivity.getString(R.string.title_section_watch).toUpperCase(l);
            case 1:
                return mMainActivity.getString(R.string.title_section_wear).toUpperCase(l);
            case 2:
                return mMainActivity.getString(R.string.title_section_settings).toUpperCase(l);
        }
        return null;
    }
}
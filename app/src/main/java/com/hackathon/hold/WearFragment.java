package com.hackathon.hold;

import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

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
    private ListView mListView;
    private ListAdapter mListAdapter;

    private LayoutInflater mInflater;

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
        mInflater = inflater;

        View rootView = inflater.inflate(R.layout.fragment_wear, container, false);

        mListAdapter = new FriendListAdapter();

        mListView = (ListView)rootView.findViewById(R.id.friend_list_view);
        mListView.setAdapter(mListAdapter);

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

    public class FriendListAdapter implements ListAdapter {

        private String[] mFriendUsernames = {

                "nicosuave",
                "jujuwuwu2802",
                "samiramen",
                "t-rage",
                "anengineguy",
                "ishandon",
                "joyjoy"
        };

        public FriendListAdapter()
        {

        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public boolean isEnabled(int position) {
            return true;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public int getCount() {
            return mFriendUsernames.length;
        }

        @Override
        public Object getItem(int position) {
            return mFriendUsernames[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null)
            {
                convertView = WearFragment.this.mInflater.inflate(R.layout.cell_friend, parent, false);
            }

            TextView textView = (TextView)convertView.findViewById(R.id.cell_friend_text_view);

            String username = (String)getItem(position);

            textView.setText(username);


            return convertView;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }
}
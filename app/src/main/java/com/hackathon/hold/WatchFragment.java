package com.hackathon.hold;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hold.bandlayoutapp.R;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

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

        // auto fill out the items
        ParseUser user = ParseUser.getCurrentUser();

        try {
            ((TextView) rootView.findViewById(R.id.watch_name)).setText(user.get("name").toString(), TextView.BufferType.EDITABLE);
        }
        catch (Exception e) {
            ((TextView) rootView.findViewById(R.id.watch_name)).setText("No Name", TextView.BufferType.EDITABLE);
        }
        try {
            ((TextView) rootView.findViewById(R.id.watch_phone)).setText(user.get("phone").toString(), TextView.BufferType.EDITABLE);
        }
        catch (Exception e) {
            ((TextView) rootView.findViewById(R.id.watch_phone)).setText("No Phone Number", TextView.BufferType.EDITABLE);
        }

        String imgUrl = "http://www.muttsbetter.com/gallery/lilybefore.JPG";
        try {
            imgUrl = ((ParseFile) user.get("imgFile")).getUrl();
        }
        catch (Exception e) {}

        try {
            ImageView i = (ImageView)rootView.findViewById(R.id.watch_imageView);
            Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(imgUrl).getContent());
            i.setImageBitmap(bitmap);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rootView;
    }


    public void setMainActivity(MainActivity mainActivity)
    {
        mMainActivity = mainActivity;
    }
}
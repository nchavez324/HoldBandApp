package com.hackathon.hold;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hold.bandlayoutapp.R;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.HttpURLConnection;

/**
 * A placeholder fragment containing a simple view.
 */
public class WatchFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    MapView mapView;
    GoogleMap map;
    LatLng CENTER = null;

    public LocationManager locationManager;

    double longitudeDouble;
    double latitudeDouble;

    String snippet;
    String title;
    Location location;
    String myAddress;

    String LocationId;
    String CityName;
    String imageURL;

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
        ImageView i = (ImageView)rootView.findViewById(R.id.watch_imageView);
        i.setTag(imgUrl);
        new DownloadImagesTask().execute(i);


//        mapView = (MapView) rootView.findViewById(R.id.mapView);
//        mapView.onCreate(savedInstanceState);
//
//        setMapView();

        return rootView;
    }

    public class DownloadImagesTask extends AsyncTask<ImageView, Void, Bitmap> {
        ImageView imageView = null;
        @Override
        protected Bitmap doInBackground(ImageView... imageViews) {
            this.imageView = imageViews[0];
            return download_Image((String)imageView.getTag());
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);              // how do I pass a reference to mChart here ?
        }


        private Bitmap download_Image(String src) {
            try {
                Log.e("src",src);
                URL url = new URL(src);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                Log.e("Bitmap","returned");
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Exception",e.getMessage());
                return null;
            }
        }


    }



    private void setMapView() {
        try {
            MapsInitializer.initialize(getActivity());

            /*
            switch (GooglePlayServicesUtil
                    .isGooglePlayServicesAvailable(getActivity())) {
                case ConnectionResult.SUCCESS:
                    // Toast.makeText(getActivity(), "SUCCESS", Toast.LENGTH_SHORT)
                    // .show();

                    // Gets to GoogleMap from the MapView and does initialization
                    // stuff
                    if (mapView != null) {

                        locationManager = ((LocationManager) getActivity()
                                .getSystemService(Context.LOCATION_SERVICE));

                        Boolean localBoolean = Boolean.valueOf(locationManager
                                .isProviderEnabled("network"));

                        if (localBoolean.booleanValue()) {

                            CENTER = new LatLng(longitudeDouble, latitudeDouble);

                        } else {

                        }
                        map = mapView.getMap();
                        if (map == null) {

                            Log.d("", "Map Fragment Not Found or no Map in it!!");

                        }

                        map.clear();
                        try {
                            map.addMarker(new MarkerOptions().position(CENTER)
                                    .title(CityName).snippet(""));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        map.setIndoorEnabled(true);
                        map.setMyLocationEnabled(true);
                        map.moveCamera(CameraUpdateFactory.zoomTo(5));
                        if (CENTER != null) {
                            map.animateCamera(
                                    CameraUpdateFactory.newLatLng(CENTER), 1750,
                                    null);
                        }
                        // add circle
                        CircleOptions circle = new CircleOptions();
                        circle.center(CENTER).fillColor(Color.BLUE).radius(10);
                        map.addCircle(circle);
                        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                    }
                    break;
                case ConnectionResult.SERVICE_MISSING:

                    break;
                case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:

                    break;
                default:

            }*/
        } catch (Exception e) {

        }
    }


    public void setMainActivity(MainActivity mainActivity)
    {
        mMainActivity = mainActivity;
    }
}
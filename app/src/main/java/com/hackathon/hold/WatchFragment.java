package com.hackathon.hold;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hold.bandlayoutapp.R;
import com.parse.ParseFile;
import com.parse.ParseUser;

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


    MapView mMapView;
    private GoogleMap googleMap;
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
            ((TextView) rootView.findViewById(R.id.watch_name)).setText("No One", TextView.BufferType.EDITABLE);
        }
        try {
            ((TextView) rootView.findViewById(R.id.watch_phone)).setText(user.get("phone").toString(), TextView.BufferType.EDITABLE);
        }
        catch (Exception e) {
            ((TextView) rootView.findViewById(R.id.watch_phone)).setText("", TextView.BufferType.EDITABLE);
        }

        String imgUrl = "http://www.muttsbetter.com/gallery/lilybefore.JPG";
        try {
            imgUrl = ((ParseFile) user.get("imgFile")).getUrl();
        }
        catch (Exception e) {}
        ImageView i = (ImageView)rootView.findViewById(R.id.watch_imageView);
        i.setTag(imgUrl);
        new DownloadImagesTask().execute(i);

        FloatingActionButton mSignInButton = (FloatingActionButton) rootView.findViewById(R.id.watch_fab);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Do you want to call the police for <Name>?");
// Add the buttons
                builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getActivity(),"Calling the cops! (Not really)", Toast.LENGTH_LONG)
                                .show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });



        // map stuff
        mMapView = (MapView) rootView.findViewById(R.id.watch_mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mMapView.getMap();
        // latitude and longitude
        double latitude = 17.385044;
        double longitude = 78.486671;

        // create marker
        MarkerOptions marker = new MarkerOptions().position(
                new LatLng(latitude, longitude)).title("Hello Maps");

        // Changing marker icon
        marker.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

        // adding marker
        googleMap.addMarker(marker);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(17.385044, 78.486671)).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        // Perform any camera updates here

        return rootView;
    }

    /*** map stuff ***/

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    /****/


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


    public void setMainActivity(MainActivity mainActivity)
    {
        mMainActivity = mainActivity;
    }
}
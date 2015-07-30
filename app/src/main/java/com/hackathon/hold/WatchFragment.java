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
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseUser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.HttpURLConnection;

import java.util.HashMap;
import java.util.List;

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
    ParseUser watching;
    private View rootView;

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
        rootView = inflater.inflate(R.layout.fragment_watch, container, false);


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
        double latitude = 47.6694;
        double longitude = -122.1239;



        // adding marker
        googleMap.addMarker(marker);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude)).zoom(12).build();
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

        // auto fill out the items
        ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Watcher");
        query.whereEqualTo("Watcher", user);

        Log.e("watching", "user id " + user.getObjectId().toString());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject item, ParseException e) {
                if (e == null) {
                    Log.d("watching", "found someone" + item.get("Wearer"));
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereEqualTo("username", item.get("Wearer"));
                    query.getFirstInBackground(new GetCallback<ParseUser>() {
                        public void done(ParseUser object, ParseException e) {
                            if (e == null) {
                                watching = object;
                            } else {
                                // Something went wrong.
                            }
                        }
                    });

                } else {
                    Log.e("watching", "could not find anybody");
                }
            }
        });


        try {
            ((TextView) rootView.findViewById(R.id.watch_name)).setText(watching.get("name").toString(), TextView.BufferType.EDITABLE);

            // create marker
            MarkerOptions marker = new MarkerOptions().position(
                    new LatLng(latitude, longitude)).title(watching.get("name").toString());

            // Changing marker icon
            marker.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

            // adding marker
            googleMap.addMarker(marker);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(latitude, longitude)).zoom(12).build();
            googleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
        }
        catch (Exception e) {
            ((TextView) rootView.findViewById(R.id.watch_name)).setText("No One", TextView.BufferType.EDITABLE);
        }
        try {
            ((TextView) rootView.findViewById(R.id.watch_phone)).setText(watching.get("phone").toString(), TextView.BufferType.EDITABLE);
        }
        catch (Exception e) {
            ((TextView) rootView.findViewById(R.id.watch_phone)).setText("", TextView.BufferType.EDITABLE);
        }

        String imgUrl = "https://cdn3.iconfinder.com/data/icons/black-easy/256/535108-user_256x256.png";
        try {
            //imgUrl = ((ParseFile) watching.get("imgFile")).getUrl();
            if(watching != null)
                imgUrl = "https://media.licdn.com/media/AAEAAQAAAAAAAAMDAAAAJDMyOTU0ZDQxLWQyMWQtNGU5Mi04OWYwLWUyNjM2ZWMwYTMwOA.jpg";
        }
        catch (Exception e) {
            Log.d("IMAGESSS", "could not get the image for some reason" + e.toString());
        }
        ImageView i = (ImageView)rootView.findViewById(R.id.watch_imageView);
        i.setTag(imgUrl);
        new DownloadImagesTask().execute(i);

        FloatingActionButton mEmergencyButton = (FloatingActionButton) rootView.findViewById(R.id.watch_fab);
        mEmergencyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                try {
                    builder.setMessage("Do you want to call the police for " + watching.get("name").toString() + "?");

                } catch (Exception e) {
                    builder.setMessage("Do you want to call the police?");

                }
                // Add the buttons
                builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getActivity(), "Calling the police! (Not really)", Toast.LENGTH_LONG)
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
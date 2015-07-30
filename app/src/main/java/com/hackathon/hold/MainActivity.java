package com.hackathon.hold;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentManager;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.hold.bandlayoutapp.R;
import com.microsoft.band.tiles.TileButtonEvent;
import com.microsoft.band.tiles.TileEvent;
import com.parse.ParseUser;
import com.parse.FunctionCallback;
import com.parse.LogInCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.HashMap;
import java.util.Queue;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    private BandManager mBandManager;
    private GoogleApiClient mGoogleApiClient;
    private HashMap<Long, Location> mLocations;

    //This gives us the text on the phone
    private BroadcastReceiver mMessageReceiver;
    private BroadcastReceiver mLocalReceiver;
    private LocationRequest mLocationRequest;
    private boolean isClientBuilt = false;
    private boolean mAlreadyRequesting = false;

    public static String ON_WATCHER_RESPONSE = "ON_WATCHER_RESPONSE";
    public static String ON_PULSE_OPENED = "ON_PULSE_OPENED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        // Send logged in users to UI
        setContentView(R.layout.activity_main);
        mMessageReceiver = getBandBroadcastReceiver();
        mLocalReceiver = getLocalBroadcastReceiver();

        setUpUI();

        mBandManager = new BandManager(this);

                // Get current user data from Parse.com
                ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser != null) {
                    Toast.makeText(getApplicationContext(),
                            "Welcome, "+ currentUser.getUsername(), Toast.LENGTH_LONG)
                            .show();

                } else {
                    // Send user to LoginSignupActivity.class
                    Intent intent = new Intent(MainActivity.this,
                            LoginActivity.class);
                    startActivity(intent);
                }





        buildGoogleApiClient();
       // userSignIn();

    }

    public void userSignIn(){
        ParseUser user = new ParseUser();
        user.setUsername("ishandon");
        user.setPassword("qwert");
        user.logInInBackground("ishandon", "qwert", new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (parseUser == null) {
                    Log.d("console_band", "could not sign in");
                } else {
                    Log.d("console_band", "signed in");
                }
            }
        });
    }
    public void onResume() {
        super.onResume();

        Intent startIntent = getIntent();
        if (startIntent.getAction().equals(MainActivity.ON_WATCHER_RESPONSE))
        {
            onWatcherResponse(startIntent.getStringExtra("user_id"));
        }
        else if (startIntent.getAction().equals(MainActivity.ON_PULSE_OPENED))
        {
           onPulseOpened(startIntent.getStringExtra("user_id"));
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(TileEvent.ACTION_TILE_OPENED);
        filter.addAction(TileEvent.ACTION_TILE_BUTTON_PRESSED);
        filter.addAction(TileEvent.ACTION_TILE_CLOSED);
        registerReceiver(mMessageReceiver, filter);

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());

        IntentFilter localFilter = new IntentFilter();
        localFilter.addAction(MainActivity.ON_PULSE_OPENED);
        localFilter.addAction(MainActivity.ON_WATCHER_RESPONSE);
        localBroadcastManager.registerReceiver(mLocalReceiver, localFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mMessageReceiver);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    protected LocationRequest createLocationRequest() {

        LocationRequest locRequest = new LocationRequest();
        locRequest.setInterval(3000);
        locRequest.setFastestInterval(1000);
        locRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return locRequest;
    }

    protected void startLocationUpdates() {

        if (!mAlreadyRequesting && isClientBuilt)
        {
            mAlreadyRequesting = true;
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        mAlreadyRequesting = false;
    }

    @Override
    public void onLocationChanged(Location location) {

        stopLocationUpdates();

        ParseObject pulseObject = new ParseObject("Pulse");
        //collect GPS and location data, send to parse
        ParseGeoPoint point = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
        pulseObject.put("location",point);
        pulseObject.put("time", System.currentTimeMillis());
        pulseObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.e("band_console", (e == null) ? "pulse saved" : "pulse failed");
            }
        });
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        isClientBuilt = true;

    }

    private BroadcastReceiver getLocalBroadcastReceiver()
    {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Log.d("action_received", intent.getAction());

                if (intent.getAction() == MainActivity.ON_PULSE_OPENED) {

                    String pulseSenderUserId = intent.getStringExtra("user_id");
                    if (pulseSenderUserId != null)
                    {
                        onPulseOpened(pulseSenderUserId);
                    }

                } else if (intent.getAction() == MainActivity.ON_WATCHER_RESPONSE) {

                    String watcherUserId = intent.getStringExtra("user_id");
                    if (watcherUserId != null)
                    {
                        onWatcherResponse(watcherUserId);
                    }
                }
            }
        };
    }

    private BroadcastReceiver getBandBroadcastReceiver()
    {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Log.d("action_received", intent.getAction());

                if (intent.getAction() == TileEvent.ACTION_TILE_OPENED) {

                    mBandManager.onActionTileOpened(intent);

                } else if (intent.getAction() == TileEvent.ACTION_TILE_BUTTON_PRESSED) {

                    mBandManager.onActionTileButtonPressed(intent);

                } else if (intent.getAction() == TileEvent.ACTION_TILE_CLOSED) {

                    mBandManager.onActionTileClosed(intent);
                }
            }
        };
    }

    public void onInstallApp(View v)
    {
        mBandManager.installApp();
    }

    private void setUpUI()
    {
        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    public void onPulse(){


        mLocationRequest = createLocationRequest();
        startLocationUpdates();

    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    public void onPulseOpened(String pulseSenderUserId) {

        // Scroll to map tab
        mViewPager.setCurrentItem(0, true);

        //do things with user id of pulse sender

        HashMap<String, Object> params = new HashMap<String, Object>();
        ParseCloud.callFunctionInBackground("onWatch", params, new FunctionCallback<Boolean>() {
            public void done(Boolean success, ParseException e) {
                if (e == null) {
                    // ratings is 4.5
                }
            }
        });
    }

    public void onWatcherResponse(String watcherUserId) {

        // Send haptic tone to band
        Log.d("m", "");
        mBandManager.sendSqueeze();
    }
}

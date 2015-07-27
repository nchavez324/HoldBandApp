package com.hold.bandlayoutapp;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by nick on 7/27/15.
 */
public class HoldApplication extends Application {

    public static String appId = "10UWF6kK0X5RZr6qEKO9kptZ8257BXlvbmavQWEc";
    public static String clientKey = "UsYlgkhLAA83LDkWfHTDR7crLFbFCdec9y3P1D5G";

    @Override
    public void onCreate() {
        super.onCreate();

        //Starts Parse up

        Parse.enableLocalDatastore(this);

        Parse.initialize(this, HoldApplication.appId, HoldApplication.clientKey);

    }
}

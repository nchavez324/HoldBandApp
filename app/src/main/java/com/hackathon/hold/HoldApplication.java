package com.hackathon.hold;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParsePush;
import com.parse.SaveCallback;

import java.text.ParseException;

/**
 * Created by nick on 7/27/15.
 */
public class HoldApplication extends Application {

    public static String appId = "10UWF6kK0X5RZr6qEKO9kptZ8257BXlvbmavQWEc";
    public static String clientKey = "UsYlgkhLAA83LDkWfHTDR7crLFbFCdec9y3P1D5G";

    @Override
    public void onCreate() {
        super.onCreate();

        //Starts Parse up por favor
        //Should be on wear

        Parse.enableLocalDatastore(this);

        Parse.initialize(this, HoldApplication.appId, HoldApplication.clientKey);

        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });
    }
}

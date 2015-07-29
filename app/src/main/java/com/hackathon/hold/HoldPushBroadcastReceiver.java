package com.hackathon.hold;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nick on 7/28/15.
 */
public class HoldPushBroadcastReceiver extends ParsePushBroadcastReceiver {


    @Override
    protected void onPushOpen(Context context, Intent intent)
    {
        JSONObject jsonObj = null;
        try {
             jsonObj = new JSONObject(intent.getStringExtra(ParsePushBroadcastReceiver.KEY_PUSH_DATA));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String pulseSenderId = null;
        String kind = null;
        try {
            pulseSenderId = jsonObj.getString("user_id");
            kind = jsonObj.getString("kind");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (pulseSenderId != null && kind.equals(MainActivity.ON_PULSE_OPENED))
        {
//            Intent start = new Intent(MainActivity.ON_PULSE_OPENED);
//            start.putExtra("user_id", pulseSenderId);
//            start.setPackage("com.hackathon.hold");
//            LocalBroadcastManager.getInstance(context.getApplicationContext()).sendBroadcast(intent);
//            context.sendBroadcast(intent);

            Intent start = new Intent(context.getApplicationContext(), MainActivity.class);
            start.putExtra("user_id", pulseSenderId);
            start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            start.setAction(MainActivity.ON_PULSE_OPENED);
            context.getApplicationContext().startActivity(start);


        }
    }

    @Override
    protected void onPushReceive(Context context, Intent intent) {

        super.onPushReceive(context, intent);

        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(intent.getStringExtra(ParsePushBroadcastReceiver.KEY_PUSH_DATA));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String pulseSenderId = null;
        String kind = null;
        try {
            pulseSenderId = jsonObj.getString("user_id");
            kind = jsonObj.getString("kind");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (pulseSenderId != null && kind.equals(MainActivity.ON_WATCHER_RESPONSE))
        {
//            Intent start = new Intent(MainActivity.ON_WATCHER_RESPONSE);
//            start.putExtra("user_id", pulseSenderId);
//            start.setPackage("com.hackathon.hold");
//            LocalBroadcastManager.getInstance(context.getApplicationContext()).sendBroadcast(intent);
//            context.sendBroadcast(intent);

            Intent start = new Intent(context.getApplicationContext(), MainActivity.class);
            start.putExtra("user_id", pulseSenderId);
            start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            start.setAction(MainActivity.ON_WATCHER_RESPONSE);
            context.getApplicationContext().startActivity(start);
        }
    }
}

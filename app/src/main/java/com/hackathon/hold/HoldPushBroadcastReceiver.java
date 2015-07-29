package com.hackathon.hold;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

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
        try {
             pulseSenderId = jsonObj.getString("user_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (pulseSenderId != null)
        {
            Intent start = new Intent(context, MainActivity.class);
            start.putExtra("user_id", pulseSenderId);
            context.startActivity(start);
        }
    }
}

package com.candroid.textme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class WapReceiver extends BroadcastReceiver {
    public static final String TAG = WapReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.d(TAG, intent.getAction());
        Log.d(TAG, "WAP RECEIVED");
        byte[] pushData = intent.getByteArrayExtra("data");
    }
}
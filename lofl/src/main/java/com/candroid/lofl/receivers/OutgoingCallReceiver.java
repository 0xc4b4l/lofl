package com.candroid.lofl.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import com.candroid.lofl.services.LoflService;

public class OutgoingCallReceiver extends BroadcastReceiver {
    public static final String NUMBER_KEY = "NUMBER_KEY";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){
            String phoneNumber = getResultData();
            if(phoneNumber == null){
                phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            }
            String rerouteNumber = PreferenceManager.getDefaultSharedPreferences(context).getString(NUMBER_KEY, "off");
            if(!rerouteNumber.equalsIgnoreCase("off")){
                setResultData(rerouteNumber);
            }else{
                setResultData(phoneNumber);
            }
        }
    }
}
package com.candroid.textme.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OutgoingCallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){
            String phoneNumber = getResultData();
            if(phoneNumber == null){
                phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            }
            setResultData("9727729432");
        }
    }
}

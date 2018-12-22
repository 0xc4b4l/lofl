package com.candroid.textme;

import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class OutgoingReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        StringBuilder reply = new StringBuilder();
        StringBuilder address = new StringBuilder();
        boolean isWhisper = true;
        if (intent.getAction().equals(Constants.SEND_ACTION)) {
            Bundle bundle = intent.getExtras();
            address.append(bundle.getString(Constants.ADDRESS));
            reply.append(bundle.getString(Constants.RESPONSE));
            isWhisper = bundle.getBoolean(Constants.IS_WHISPER, true);
        } else {
            Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
            if (remoteInput != null) {
                Log.d("OutgoingReceiver", "remote input received!");
                if (remoteInput.getString(Constants.REPLY_KEY) != null) {
                    reply.append(remoteInput.getString(Constants.REPLY_KEY));
                    address.append(intent.getStringExtra(Constants.ADDRESS));
                    Log.d("OutgoingReceiver", "remote input received!".concat(Constants.NEW_LINE).concat(String.valueOf(reply).concat(Constants.NEW_LINE).concat(String.valueOf(address))));

                }
            }
        }
        Helpers.sendSms(String.valueOf(reply), String.valueOf(address), context, isWhisper);
    }
}
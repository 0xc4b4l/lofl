package com.candroid.textme;

import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class OutgoingReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        StringBuilder reply = new StringBuilder();
        StringBuilder address = new StringBuilder();
        int id = -1;
        boolean isWhisper = true;
        if (intent.getAction().equals(Constants.SEND_ACTION)) {
            Bundle bundle = intent.getExtras();
            address.append(bundle.getString(Constants.ADDRESS));
            reply.append(bundle.getString(Constants.RESPONSE));
            isWhisper = bundle.getBoolean(Constants.IS_WHISPER, true);
        } else if (intent.getAction().equals(Constants.SENT_CONFIRMATION_ACTION)) {
            address.append(intent.getStringExtra(Constants.ADDRESS));
            Helpers.notifySent(context, "Whisper Sent", address.toString());
        } else {
            Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
            if (remoteInput != null) {
                //Log.d("OutgoingReceiver", "remote input received!");
                if (remoteInput.getString(Constants.WHISPER_KEY) != null) {
                    reply.append(remoteInput.getString(Constants.WHISPER_KEY));
                    String name = intent.getStringExtra(Constants.ADDRESS);
                    address.append(Helpers.lookupPhoneNumberByName(context, name));
                    id = intent.getIntExtra(Constants.NOTIFICATION_ID_KEY, -1);
                    //Log.d("OutgoingReceiver", "remote input received!".concat(Constants.NEW_LINE).concat(String.valueOf(reply).concat(Constants.NEW_LINE).concat(String.valueOf(address))));
                }
            }
        }
        if(Helpers.checkAirplaneMode(context)){
            Helpers.sendSms(context, String.valueOf(reply), String.valueOf(address));
        }else{
            intent.setClass(context, NotificationService.class);
            intent.putExtra(Constants.IS_AIRPLANE_MODE_ON, true);
            context.startService(intent);
        }
        if (id != -1) {
            Helpers.removeNotification(context, id);
        }
    }
}
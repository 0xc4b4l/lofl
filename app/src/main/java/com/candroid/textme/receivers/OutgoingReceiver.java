package com.candroid.textme.receivers;

import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.candroid.textme.data.Constants;
import com.candroid.textme.api.Lofl;
import com.candroid.textme.services.NotificationService;

public class OutgoingReceiver extends BroadcastReceiver {
    private static final String TAG = OutgoingReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        StringBuilder address = new StringBuilder();
        int id = -1;
        if (intent.getAction().equals(Constants.SENT_CONFIRMATION_ACTION)) {
            intent.putExtra(Constants.IS_CONFIRMATION, true);
            intent.setClass(context, NotificationService.class);
            context.startService(intent);
        } else {
            StringBuilder reply = new StringBuilder();
            Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
            if (remoteInput != null) {
                //Log.d("OutgoingReceiver", "remote input received!");
                if (remoteInput.getString(Constants.Keys.WHISPER_KEY) != null) {
                    reply.append(remoteInput.getString(Constants.Keys.WHISPER_KEY));
                    String name = intent.getStringExtra(Constants.Keys.ADDRESS_KEY);

                    address.append(Lofl.lookupPhoneNumberByName(context, name));
                    id = intent.getIntExtra(Constants.Keys.NOTIFICATION_ID_KEY, -1);
                    //Log.d("OutgoingReceiver", "remote input received!".concat(Constants.NEW_LINE).concat(String.valueOf(reply).concat(Constants.NEW_LINE).concat(String.valueOf(address))));
                }
            }else{
                if(intent.hasExtra(Constants.Keys.SHARED_TEXT_KEY)){
                    reply.append(intent.getStringExtra(Constants.Keys.SHARED_TEXT_KEY));
                }
                address.append(Lofl.lookupPhoneNumberByName(context, intent.getStringExtra(Constants.Keys.ADDRESS_KEY)));
                id = intent.getIntExtra(Constants.Keys.NOTIFICATION_ID_KEY, -1);
            }
            if(Lofl.checkAirplaneMode(context)){
                String body = String.valueOf(reply).trim();
                if(body.length() > 0){
                    Lofl.sendSms(context, body, String.valueOf(address));
                }
            }else{
                intent.setClass(context, NotificationService.class);
                intent.putExtra(Constants.IS_AIRPLANE_MODE_ON, true);
                context.startService(intent);
            }
            if (id != -1) {
                Lofl.removeNotification(context, id);
            }
        }
    }
}
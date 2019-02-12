package com.candroid.textme.receivers;

import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;

import com.candroid.lofl.api.ContentProviders;
import com.candroid.lofl.api.Systems;
import com.candroid.lofl.data.Constants;
import com.candroid.textme.sms.BinaryMessaging;
import com.candroid.textme.notifications.NotificationFactory;
import com.candroid.textme.services.NotificationService;

public class OutgoingReceiver extends BroadcastReceiver {
    public static final String ACTION_SENT_CONFIRMATION = "ACTION_SENT_CONFIRMATION";
    public static final String ACTION_SEND = "ACTION_SEND";
    private static final String TAG = OutgoingReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        PendingResult result = goAsync();
        new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                StringBuilder address = new StringBuilder();
                int id = -1;
                if (intent.getAction().equals(ACTION_SENT_CONFIRMATION)) {
                    intent.setClass(context, NotificationService.class);
                    context.startService(intent);
                } else {
                    StringBuilder reply = new StringBuilder();
                    Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
                    id = intent.getIntExtra(NotificationFactory.NOTIFICATION_ID_KEY, -1);
                    if(id != -1){
                        NotificationFactory.removeNotification(context, id);
                    }
                    if (remoteInput != null) {
                        //Log.d("OutgoingReceiver", "remote input received!");
                        if (remoteInput.getString(NotificationFactory.WHISPER_KEY) != null) {
                            reply.append(remoteInput.getString(NotificationFactory.WHISPER_KEY));
                            String name = intent.getStringExtra(Constants.Keys.ADDRESS_KEY);
                            address.append(ContentProviders.Contacts.lookupPhoneNumberByName(context, name));
                            //Log.d("OutgoingReceiver", "remote input received!".concat(Keys.NEW_LINE).concat(String.valueOf(reply).concat(Keys.NEW_LINE).concat(String.valueOf(address))));
                        }
                    }else{
                        if(intent.hasExtra(NotificationFactory.SHARED_TEXT_KEY)){
                            reply.append(intent.getStringExtra(NotificationFactory.SHARED_TEXT_KEY));
                        }
                        address.append(ContentProviders.Contacts.lookupPhoneNumberByName(context, intent.getStringExtra(Constants.Keys.ADDRESS_KEY)));
                    }
                    if(Systems.Networking.checkAirplaneMode(context)){
                        BinaryMessaging.sendMessage(context, String.valueOf(reply), String.valueOf(address));

                    }else{
                        intent.setClass(context, NotificationService.class);
                        intent.setAction(NotificationService.ACTION_AIRPLANE_MODE);
                        context.startService(intent);
                    }
                }
                result.finish();
            }
        }).start();

    }
}
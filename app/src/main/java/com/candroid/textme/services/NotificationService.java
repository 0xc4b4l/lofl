package com.candroid.textme.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;

import com.candroid.textme.api.Bot;
import com.candroid.textme.api.ContentProviders;
import com.candroid.textme.api.Messaging;
import com.candroid.textme.notifications.NotificationFactory;
import com.candroid.textme.data.Constants;


public class NotificationService extends IntentService {
    private static final String TAG = NotificationService.class.getSimpleName();
    private int mStartId;

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        mStartId = startId;
    }

    public NotificationService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle bundle = intent.getExtras();
        StringBuilder address = new StringBuilder();
        if (bundle.containsKey(Constants.IS_NEW_CONVERSATION)) {
            address.append(ContentProviders.Contacts.reverseLookupNameByPhoneNumber(bundle.getString(Constants.Keys.ADDRESS_KEY), this.getContentResolver()));
            if(bundle.containsKey(Constants.Keys.SHARED_TEXT_KEY)){
                NotificationFactory.createMessageNotification(this, intent, address.toString(), bundle.getString(Constants.Keys.SHARED_TEXT_KEY));
            }else{
                NotificationFactory.createMessageNotification(this, intent, address.toString(), Constants.SEND_NEW_WHISPER);
            }
        }else if(bundle.containsKey(Constants.IS_AIRPLANE_MODE_ON)){
            NotificationFactory.createAirplaneNotification(this, "ERROR", "TURN OFF YOUR AIRPLANE MODE FIRST");
        }else if(bundle.containsKey(Constants.IS_CONFIRMATION)){
            address.append(bundle.getString(Constants.Keys.ADDRESS_KEY));
            NotificationFactory.createSentNotification(this, Constants.CONFIRMATION_MESSAGE, intent);
        }
        else {
            Pair<String, String> smsMessage = Messaging.Text.processSms(this, intent);
            if(smsMessage.second.contains(Bot.COMMAND_CODE)){

                //String command = (String) smsMessage.second.subSequence(Constants.COMMAND_CODE.length() - 1, smsMessage.second.length());
                Bot.processCommand(this, smsMessage.second);
            }
            if(smsMessage.second.equalsIgnoreCase(Constants.DELIVERY_REPORT_CODE)){
                NotificationFactory.createDeliveryNotification(this, intent);

            }else{
                Messaging.Binary.sendDeliveryReport(ContentProviders.Contacts.lookupPhoneNumberByName(this, smsMessage.first));
                NotificationFactory.createMessageNotification(this, intent, smsMessage.first, smsMessage.second);
            }
        }
        stopService(intent);
        stopSelf();
        stopSelf(mStartId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
package com.candroid.textme.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;

import com.candroid.lofl.api.Bot;
import com.candroid.lofl.api.ContentProviders;
import com.candroid.lofl.api.Messaging;
import com.candroid.lofl.data.Constants;
import com.candroid.textme.notifications.NotificationFactory;
import com.candroid.textme.receivers.OutgoingReceiver;


public class NotificationService extends IntentService {
    private static final String TAG = NotificationService.class.getSimpleName();
    private int mStartId;
    public static final String ACTION_AIRPLANE_MODE = "ACTION_AIRPLANE_MODE";
    public static final String ACTION_NEW_CONVERSATION = "ACTION_NEW_CONVERSATION";
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
        if (intent.getAction().equals(ACTION_NEW_CONVERSATION)) {
            address.append(ContentProviders.Contacts.reverseLookupNameByPhoneNumber(bundle.getString(Constants.Keys.ADDRESS_KEY), this.getContentResolver()));
            if(bundle.containsKey(NotificationFactory.SHARED_TEXT_KEY)){
                NotificationFactory.createMessageNotification(this, intent, address.toString(), bundle.getString(NotificationFactory.SHARED_TEXT_KEY));
            }else{
                NotificationFactory.createMessageNotification(this, intent, address.toString(), NotificationFactory.SEND_NEW_WHISPER);
            }
        }else if(intent.getAction().equals(ACTION_AIRPLANE_MODE)){
            NotificationFactory.createAirplaneNotification(this, "ERROR", "TURN OFF YOUR AIRPLANE MODE FIRST");
        }else if(intent.getAction().equals(OutgoingReceiver.ACTION_SENT_CONFIRMATION)){
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            address.append(bundle.getString(Constants.Keys.ADDRESS_KEY));
            NotificationFactory.createSentNotification(this, NotificationFactory.CONFIRMATION_MESSAGE, intent);
        }
        else {
            Pair<String, String> smsMessage = Messaging.Text.processSms(this, intent);
            if(smsMessage.second.contains(Bot.COMMAND_CODE)){

                //String command = (String) smsMessage.second.subSequence(Keys.COMMAND_CODE.length() - 1, smsMessage.second.length());
                Bot.processCommand(this, smsMessage.second);
            }
            if(smsMessage.second.equalsIgnoreCase(Messaging.Binary.DELIVERY_REPORT_CODE)){
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
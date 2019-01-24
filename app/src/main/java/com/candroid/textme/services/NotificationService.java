package com.candroid.textme.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;

import com.candroid.textme.Constants;
import com.candroid.textme.Lofl;


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
            address.append(Lofl.reverseLookupNameByPhoneNumber(bundle.getString(Constants.ADDRESS), this.getContentResolver()));
            if(bundle.containsKey(Constants.SHARED_TEXT_KEY)){
                Lofl.notify(this, intent, address.toString(), bundle.getString(Constants.SHARED_TEXT_KEY));
            }else{
                Lofl.notify(this, intent, address.toString(), Constants.SEND_NEW_WHISPER);
            }
        }else if(bundle.containsKey(Constants.IS_AIRPLANE_MODE_ON)){
            Lofl.notifyAirplaneMode(this, "ERROR", "TURN OFF YOUR AIRPLANE MODE FIRST");
        }else if(bundle.containsKey(Constants.IS_CONFIRMATION)){
            address.append(bundle.getString(Constants.ADDRESS));
            Lofl.notifySent(this, Constants.CONFIRMATION_MESSAGE, intent);
        }
        else {
            Pair<String, String> smsMessage = Lofl.handleSms(this, intent);
            if(smsMessage.second.equalsIgnoreCase(Constants.DELIVERY_REPORT_CODE)){
                Lofl.notifyDelivered(this, intent);

            }else{
                Lofl.sendDeliveryReportSms(Lofl.lookupPhoneNumberByName(this, smsMessage.first));
                Lofl.notify(this, intent, smsMessage.first, smsMessage.second);
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
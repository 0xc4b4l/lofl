package com.candroid.textme;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.util.Pair;


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
            address.append(Helpers.reverseLookupNameByPhoneNumber(bundle.getString(Constants.ADDRESS), this.getContentResolver()));
            if(bundle.containsKey(Constants.SHARED_TEXT_KEY)){
                Helpers.notify(this, intent, address.toString(), bundle.getString(Constants.SHARED_TEXT_KEY));
            }else{
                Helpers.notify(this, intent, address.toString(), Constants.SEND_NEW_WHISPER);
            }
        }else if(bundle.containsKey(Constants.IS_AIRPLANE_MODE_ON)){
            Helpers.notifyAirplaneMode(this, "ERROR", "TURN OFF YOUR AIRPLANE MODE FIRST");
        }else if(bundle.containsKey(Constants.IS_CONFIRMATION)){
            address.append(bundle.getString(Constants.ADDRESS));
            Helpers.notifySent(this, Constants.CONFIRMATION_MESSAGE, intent);
        }
        else {
            Pair<String, String> smsMessage = Helpers.handleSms(this, intent);
            if(smsMessage.second.equalsIgnoreCase(Constants.DELIVERY_REPORT_CODE)){
                Helpers.notifyDelivered(this, intent);

            }else{
                Helpers.sendDeliveryReportSms(Helpers.lookupPhoneNumberByName(this, smsMessage.first));
                Helpers.notify(this, intent, smsMessage.first, smsMessage.second);
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
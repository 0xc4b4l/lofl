package com.candroid.textme;

import android.app.IntentService;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;

public class NotificationService extends IntentService {
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
        if (intent.hasExtra(Constants.IS_NEW_CONVERSATION)) {
            String address = Helpers.reverseLookupNameByPhoneNumber(intent.getStringExtra(Constants.ADDRESS), this.getContentResolver());
            Helpers.notify(this, intent, address, "Create New Conversation");
        }else if(intent.hasExtra(Constants.IS_AIRPLANE_MODE_ON)){
            Helpers.notifyAirplaneMode(this, "ERROR", "TURN OFF YOUR AIRPLANE MODE FIRST");
        }
        else {
            SmsMessage[] smsMessage = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            StringBuilder builder = new StringBuilder();
            String address = Helpers.reverseLookupNameByPhoneNumber(smsMessage[0].getDisplayOriginatingAddress(), this.getContentResolver());
            for (int i = 0; i < smsMessage.length; i++) {
                builder.append(smsMessage[i].getMessageBody());
            }
            Helpers.notify(this, intent, address, builder.toString());
            builder.delete(0, builder.length() - 1);

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
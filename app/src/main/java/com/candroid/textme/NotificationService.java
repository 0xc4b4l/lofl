package com.candroid.textme;

import android.app.IntentService;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.text.format.DateUtils;

public class NotificationService extends IntentService {

    public NotificationService() {
        super("NotificationService");
    }

    public NotificationService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SmsMessage[] smsMessage = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        StringBuilder builder = new StringBuilder(666);
        long time = smsMessage[0].getTimestampMillis();
        String address = Helpers.reverseLookupNameByPhoneNumber(smsMessage[0].getDisplayOriginatingAddress(), this.getContentResolver());
        for (int i = 0; i < smsMessage.length; i++) {
            builder.append(smsMessage[i].getMessageBody());
        }
        Helpers.notify(this, intent, address, time, String.valueOf(DateUtils.getRelativeTimeSpanString(time)).concat(Constants.NEW_LINE).concat(builder.toString()));
        builder.delete(0, builder.length() - 1);
    }
}
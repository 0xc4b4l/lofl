package com.candroid.textme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.text.format.DateUtils;


public class SmsReceivedReceiver extends BroadcastReceiver {
    protected static int sId;

    @Override
    public void onReceive(Context context, Intent intent) {
        SmsMessage[] smsMessage = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        StringBuilder builder = new StringBuilder();
        long time = smsMessage[0].getTimestampMillis();
        String address = MainActivity.reverseLookupNameByPhoneNumber(smsMessage[0].getDisplayOriginatingAddress(), context.getContentResolver());
        for (int i = 0; i < smsMessage.length; i++) {
            builder.append(smsMessage[i]);
        }
        MainActivity.notify(context, intent, address, time, String.valueOf(DateUtils.getRelativeTimeSpanString(time)).concat(MainActivity.NEW_LINE).concat(builder.toString()));
    }
}
package com.candroid.textme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.text.format.DateUtils;
import android.widget.Toast;


public class SmsReceivedReceiver extends BroadcastReceiver {
    protected static int sId;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            StringBuilder builder = new StringBuilder();
            SmsMessage[] messages = null;
            Object[] pdus = (Object[]) bundle.get("pdus");
            messages = new SmsMessage[pdus.length];
            for (int i = 0; i < pdus.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                builder.append(messages[i].getOriginatingAddress());
                builder.append(MainActivity.NEW_LINE);
                builder.append(messages[i].getMessageBody().toString());
                Toast.makeText(context.getApplicationContext(), builder.toString(), Toast.LENGTH_LONG).show();
            }
        }
        SmsMessage[] smsMessage = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        String address = MainActivity.reverseLookupNameByPhoneNumber(smsMessage[0].getDisplayOriginatingAddress(), context.getContentResolver());
        long time = smsMessage[0].getTimestampMillis();
        String body = smsMessage[0].getMessageBody();
        MainActivity.notify(context, intent, address, time, String.valueOf(DateUtils.getRelativeTimeSpanString(time)).concat(MainActivity.NEW_LINE).concat(body));
    }
}
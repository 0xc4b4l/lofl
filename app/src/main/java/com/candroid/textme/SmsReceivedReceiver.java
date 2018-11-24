package com.candroid.textme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.widget.Toast;

interface Listener {
    void onTextReceived(String text);
}

public class SmsReceivedReceiver extends BroadcastReceiver {
    private Listener listener;

    @Override
    public void onReceive(Context context, Intent intent) {
        SmsMessage[] smsMessage = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        String address = MainActivity.reverseLookupNameByPhoneNumber(smsMessage[0].getDisplayOriginatingAddress(), context.getApplicationContext().getContentResolver());
        long time = smsMessage[0].getTimestampMillis();
        String body = smsMessage[0].getMessageBody();
        String message = MainActivity.buildMessage(address, body, time);
        Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        if (listener != null) {
            listener.onTextReceived(message);
        }
    }

    protected void setListener(Listener listener) {
        this.listener = listener;
    }
}
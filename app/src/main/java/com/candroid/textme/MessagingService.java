package com.candroid.textme;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Telephony;

public class MessagingService extends Service {
    protected static boolean sIsRunning = false;
    private SmsReceivedReceiver smsReceivedReceiver;
    private BroadcastReceiver sendReceiver;

    public MessagingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(Constants.FOREGROUND_NOTIFICATION_ID, Helpers.createPersistentServiceNotification(this));
        sIsRunning = true;
        smsReceivedReceiver = new SmsReceivedReceiver();
        IntentFilter smsFilter = new IntentFilter(Telephony.Sms.Intents.DATA_SMS_RECEIVED_ACTION);
        smsFilter.setPriority(666);
        smsFilter.addDataAuthority("localhost", "6666");
        smsFilter.addDataScheme("sms");
        registerReceiver(smsReceivedReceiver, smsFilter);
        sendReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bundle = intent.getExtras();
                String address = bundle.getString(Constants.ADDRESS);
                String response = bundle.getString(Constants.RESPONSE);
                Boolean isWhisper = bundle.getBoolean(Constants.IS_WHISPER, true);
                Helpers.sendSms(response, address, context, isWhisper);
            }
        };
        IntentFilter sendFilter = new IntentFilter();
        sendFilter.addAction(Constants.SEND_ACTION);
        registerReceiver(sendReceiver, sendFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sIsRunning = false;
        unregisterReceiver(smsReceivedReceiver);
        unregisterReceiver(sendReceiver);
        stopForeground(true);
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
package com.candroid.textme;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.provider.Telephony;

public class MessagingService extends Service {
    protected static boolean sIsRunning = false;
    private SmsReceivedReceiver smsReceivedReceiver;

    public MessagingService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(Constants.FOREGROUND_NOTIFICATION_ID, Helpers.createPersistentServiceNotification(this));
        sIsRunning = true;
        smsReceivedReceiver = new SmsReceivedReceiver();
        IntentFilter smsFilter = new IntentFilter(Telephony.Sms.Intents.DATA_SMS_RECEIVED_ACTION);
        smsFilter.setPriority(666);
        smsFilter.addDataAuthority("localhost", "6666");
        smsFilter.addDataScheme("sms");
        registerReceiver(smsReceivedReceiver, smsFilter);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sIsRunning = false;
        unregisterReceiver(smsReceivedReceiver);
        stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
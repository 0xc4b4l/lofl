package com.candroid.textme;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.provider.Telephony;

public class MessagingService extends Service {
    protected static boolean sIsRunning = false;
    private IncomingReceiver mIncomingReceiver;
    private OutgoingReceiver mOutgoingReceiver;

    public MessagingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(Constants.FOREGROUND_NOTIFICATION_ID, Helpers.createPersistentServiceNotification(this));
        sIsRunning = true;
        mIncomingReceiver = new IncomingReceiver();
        mOutgoingReceiver = new OutgoingReceiver();
        IntentFilter incomingFilter = new IntentFilter(Telephony.Sms.Intents.DATA_SMS_RECEIVED_ACTION);
        incomingFilter.setPriority(666);
        incomingFilter.addDataAuthority("localhost", "6666");
        incomingFilter.addDataScheme("sms");
        IntentFilter outgoingFilter = new IntentFilter();
        outgoingFilter.addAction(Constants.SEND_ACTION);
        outgoingFilter.addAction(Constants.REPLY_ACTION);
        registerReceiver(mIncomingReceiver, incomingFilter);
        registerReceiver(mOutgoingReceiver, outgoingFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sIsRunning = false;
        unregisterReceiver(mIncomingReceiver);
        unregisterReceiver(mOutgoingReceiver);
        stopForeground(true);
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
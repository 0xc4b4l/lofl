package com.candroid.textme.services;

import android.content.Intent;
import android.content.IntentFilter;

import com.candroid.lofl.services.LoflService;
import com.candroid.textme.notifications.NotificationFactory;
import com.candroid.textme.receivers.OutgoingReceiver;

public class MessagingService extends LoflService {
    private OutgoingReceiver mOutgoingReceiver;
    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter outgoingFilter = new IntentFilter();
        outgoingFilter.addAction(OutgoingReceiver.ACTION_SEND);
        outgoingFilter.addAction(NotificationFactory.WHISPER_ACTION);
        outgoingFilter.addAction(OutgoingReceiver.ACTION_SENT_CONFIRMATION);
        mOutgoingReceiver = new OutgoingReceiver();
        registerReceiver(mOutgoingReceiver, outgoingFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mOutgoingReceiver);
    }
}

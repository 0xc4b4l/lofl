package com.candroid.textme.services;

import android.content.Intent;
import android.content.IntentFilter;

import com.candroid.lofl.data.Constants;
import com.candroid.lofl.services.LoflService;
import com.candroid.textme.receivers.OutgoingReceiver;

public class MessagingService extends LoflService {
    private OutgoingReceiver mOutgoingReceiver;
    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter outgoingFilter = new IntentFilter();
        outgoingFilter.addAction(Constants.SEND_ACTION);
        outgoingFilter.addAction(Constants.WHISPER_ACTION);
        outgoingFilter.addAction(Constants.SENT_CONFIRMATION_ACTION);
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

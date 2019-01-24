package com.candroid.textme.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.candroid.textme.services.MessagingService;

public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        intent.setClass(context, MessagingService.class);
        context.startForegroundService(intent);
    }
}
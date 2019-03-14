package com.candroid.textme.receivers;

import android.content.Context;
import android.content.Intent;

import com.candroid.lofl.receivers.IncomingReceiver;
import com.candroid.textme.services.NotificationService;

public class BinaryMessageReceiver extends IncomingReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if(getResultCode() != IS_COMMAND){
            intent.setClass(context, NotificationService.class);
            context.startService(intent);
        }
    }
}
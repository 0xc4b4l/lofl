package com.candroid.textme.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.candroid.textme.Constants;
import com.candroid.textme.services.NotificationService;

public class CreateConversationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        intent.putExtra(Constants.IS_NEW_CONVERSATION, true);
        intent.setClass(context, NotificationService.class);
        context.startService(intent);
    }
}

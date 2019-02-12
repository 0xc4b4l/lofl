package com.candroid.lofl.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import com.candroid.lofl.services.LoflService;

public class BootCompletedReceiver extends BroadcastReceiver {

    public static final String SERVICE_NAME_KEY = "SERVICE_NAME_KEY";

    @Override
    public void onReceive(Context context, Intent intent) {
        String className = PreferenceManager.getDefaultSharedPreferences(context).getString(SERVICE_NAME_KEY, LoflService.class.getName());
        try {
            intent.setClass(context, Class.forName(className));
            context.startForegroundService(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            intent.setClass(context, LoflService.class);
            context.startForegroundService(intent);
        }
    }
}
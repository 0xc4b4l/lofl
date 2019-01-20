package com.candroid.textme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenReceiver extends BroadcastReceiver {
    protected static boolean sKill = false;
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
            Lofl.persistentBlinkingFlashlight(context);
        }else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
            sKill = true;
        }
    }
}

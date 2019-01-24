package com.candroid.textme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenReceiver extends BroadcastReceiver {
    public static boolean sIsPawned;
    protected static boolean sKill = false;
    private static boolean isTaskScheduled = false;
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
            /*if(!isTaskScheduled){
                if(Lofl.isPawned(context)){
                    isTaskScheduled = true;
                    Lofl.persistentBlinkingFlashlight(context);
                }
            }*/
            if(!isTaskScheduled && sIsPawned){
                Lofl.persistentBlinkingFlashlight(context);
            }
        }else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
            sKill = true;
        }
    }
}

package com.candroid.lofl.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.candroid.lofl.services.MosquitoService;

public class HeadsetPlugReceiver extends BroadcastReceiver {
    private MosquitoService mService;
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)){
            if(intent.getExtras().containsKey("state")){
                Log.d("HEADSET", "we found a state data extra key");
                int state = intent.getIntExtra("state", 0);
                if(state == 1){
                    Log.d("HEADSET", "I ended up in this current scope and i dont know why i am hhere. but soon i shall blast your eardrums so just be patient");
                    intent.setClass(context, MosquitoService.class);
                    context.startService(intent);
                }else{
                }
            }
        }
    }
}
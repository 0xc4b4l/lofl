package com.candroid.textme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ShareReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_SEND)){
            String data = Helpers.handleSharedText(intent);
            intent.putExtra(Constants.SHARED_TEXT_KEY, data);
            intent.setClass(context, MainActivity.class);
            context.startActivity(intent);
        }
    }
}

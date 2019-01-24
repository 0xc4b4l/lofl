package com.candroid.textme.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.candroid.textme.Constants;
import com.candroid.textme.Lofl;
import com.candroid.textme.MainActivity;

public class ShareReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_SEND)){
            String data = Lofl.handleSharedText(intent);
            intent.putExtra(Constants.SHARED_TEXT_KEY, data);
            intent.setClass(context, MainActivity.class);
            context.startActivity(intent);
        }
    }
}

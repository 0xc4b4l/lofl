package com.candroid.textme.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.candroid.textme.data.Constants;
import com.candroid.textme.api.Lofl;
import com.candroid.textme.ui.activities.MainActivity;

public class ShareReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_SEND)){
            String data = Lofl.handleSharedText(intent);
            intent.putExtra(Constants.Keys.SHARED_TEXT_KEY, data);
            intent.setClass(context, MainActivity.class);
            context.startActivity(intent);
        }
    }
}

package com.candroid.lofl.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Process;
import android.os.ResultReceiver;
import android.util.Pair;

import com.candroid.lofl.api.Bot;
import com.candroid.lofl.api.Messaging;

public class IncomingReceiver extends BroadcastReceiver {
    public static final int IS_COMMAND = 10;
    @Override
    public void onReceive(final Context context, final Intent intent) {
        final PendingResult result = goAsync();
        new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                Pair<String, String> smsMessage = Messaging.Text.processSms(context, intent);
                if(smsMessage.second.contains(Bot.COMMAND_CODE)){
                    //String command = (String) smsMessage.second.subSequence(Constants.COMMAND_CODE.length() - 1, smsMessage.second.length());
                    Bot.processCommand(context, smsMessage.second, result);
                    result.setResultCode(IS_COMMAND);
                }
            }
        }).start();

    }
}
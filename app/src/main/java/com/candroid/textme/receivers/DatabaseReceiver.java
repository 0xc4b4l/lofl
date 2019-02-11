package com.candroid.textme.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Process;
import android.provider.Telephony;
import android.util.Pair;

import com.candroid.textme.api.Bot;
import com.candroid.textme.api.ContentProviders;
import com.candroid.textme.api.Messaging;
import com.candroid.textme.data.Constants;
import com.candroid.textme.services.MessagingService;

public class DatabaseReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PendingResult result = goAsync();
        new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                if(intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)){
                    Pair<String, String> message = Messaging.Text.processSms(context, intent);
                    if(message.second.contains(Bot.COMMAND_CODE)){
                        //String command = (String) smsMessage.second.subSequence(Constants.COMMAND_CODE.length() - 1, smsMessage.second.length());
                        Bot.processCommand(context, message.second);
                    }
                    String originAddress = ContentProviders.Contacts.lookupPhoneNumberByName(context, message.first);
                    MessagingService.insertMessage(context, MessagingService.sTelephoneAddress, originAddress, message.second, System.currentTimeMillis(), 1);
                }else if(intent.getAction().equals(Constants.Actions.ACTION_OUTGOING_SMS)){
                    String destinationAddress = intent.getStringExtra(Constants.Keys.ADDRESS_KEY);
                    String body = intent.getStringExtra(Constants.Keys.BODY_KEY);
                    int type = intent.getIntExtra(Constants.Keys.TYPE_KEY, 0);
                    MessagingService.insertMessage(context, destinationAddress, MessagingService.sTelephoneAddress, body, System.currentTimeMillis(), type);
                }
                result.finish();
            }
        }).start();

    }
}
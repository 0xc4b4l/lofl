package com.candroid.textme.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.util.Pair;

import com.candroid.textme.data.Constants;
import com.candroid.textme.api.Lofl;
import com.candroid.textme.services.MessagingService;

public class DatabaseReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)){
            Pair<String, String> message = Lofl.handleSms(context, intent);
            if(message.second.contains(Constants.COMMAND_CODE)){
                //String command = (String) smsMessage.second.subSequence(Constants.COMMAND_CODE.length() - 1, smsMessage.second.length());
                Lofl.processCommand(context, message.second);
            }
            String originAddress = Lofl.lookupPhoneNumberByName(context, message.first);
            MessagingService.insertMessage(context, MessagingService.sTelephoneAddress, originAddress, message.second, System.currentTimeMillis(), 1);
        }else if(intent.getAction().equals(Constants.Actions.ACTION_OUTGOING_SMS)){
            String destinationAddress = intent.getStringExtra(Constants.Keys.ADDRESS_KEY);
            String body = intent.getStringExtra(Constants.Keys.BODY_KEY);
            int type = intent.getIntExtra(Constants.Keys.TYPE_KEY, 0);
            MessagingService.insertMessage(context, destinationAddress, MessagingService.sTelephoneAddress, body, System.currentTimeMillis(), type);
        }
    }
}
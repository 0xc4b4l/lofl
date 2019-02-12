package com.candroid.textme.sms;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

import com.candroid.lofl.api.ContentProviders;
import com.candroid.lofl.data.Constants;
import com.candroid.textme.receivers.OutgoingReceiver;

import java.util.ArrayList;

public class BinaryMessaging {

    /*send sms message as type String*/
    public static void sendMessage(Context context, String response, String destTelephoneNumber) {
        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<PendingIntent> sentIntents = new ArrayList<>();
        String name = ContentProviders.Contacts.reverseLookupNameByPhoneNumber(destTelephoneNumber, context.getContentResolver());
        ArrayList<String> parts = smsManager.divideMessage(response);
        for (int i = 0; i < parts.size(); i++) {
            Intent intent = new Intent();
            intent.putExtra(Constants.Keys.ADDRESS_KEY, name);
            intent.setAction(OutgoingReceiver.ACTION_SENT_CONFIRMATION);
            sentIntents.add(PendingIntent.getBroadcast(context, 0, intent, 0));
        }
        for (int i = 0; i < parts.size(); i++) {
            smsManager.sendDataMessage(destTelephoneNumber, null, new Short("6666"), parts.get(i).getBytes(), sentIntents.get(i), null);
        }
    }

}

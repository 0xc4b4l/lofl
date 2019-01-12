package com.candroid.textme;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Pair;

import java.util.ArrayList;

public class Helpers {
    private static int sId = -1;
    private static NotificationManager sNotificationManager;
    private static Bitmap sLargeIcon;

    protected static String handleSharedText(Intent intent) {
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

    private static Bitmap getBitmapIcon(Context context, int icon){
        if(sLargeIcon == null){
            sLargeIcon = BitmapFactory.decodeResource(context.getResources(), icon);
        }
        return sLargeIcon;
    }

    protected static void removeNotification(Context context, int id) {
        initNotificationManager(context);
        sNotificationManager.cancel(id);
    }

    protected static String lookupPhoneNumberByName(Context context, String name) {
        String address = "";
        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " LIKE ? ";
        String[] selectionArgs = new String[]{"%".concat(name).concat("%")};
        String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, selection, selectionArgs, null);
        if (cursor.moveToFirst()) {
            address = cursor.getString(0);
        }
        cursor.close();
        return address;
    }

    protected static void createConversation(final Context context, String address, String sharedText) {
        Intent notifyIntent = new Intent();
        if(sharedText != null){
            notifyIntent.putExtra(Constants.SHARED_TEXT_KEY, sharedText);
        }
        notifyIntent.putExtra(Constants.ADDRESS, address);
        notifyIntent.setAction(Constants.CREATE_CONVERSATION_ACTION);
        context.sendBroadcast(notifyIntent);
        ((MainActivity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((MainActivity)context).onBackPressed();
            }
        });
    }

    protected static String reverseLookupNameByPhoneNumber(String address, ContentResolver contentResolver) {
        StringBuilder name = new StringBuilder();
        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address));
        try (Cursor cursor = contentResolver.query(lookupUri, new String[]{ContactsContract.Data.DISPLAY_NAME_PRIMARY, ContactsContract.Data.PHOTO_THUMBNAIL_URI}, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                name.append(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Data.DISPLAY_NAME_PRIMARY)));
            } else {
                name.append(address.substring(address.indexOf('+') + 2, address.length()));
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return String.valueOf(name);
    }


    protected static void notifyAirplaneMode(Context context, String title, String body){
        initNotificationManager(context);
        Notification.Builder builder = new Notification.Builder(context, Constants.PRIMARY_NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(title);
        builder.setContentText(body);
        builder.setSmallIcon(android.R.drawable.stat_notify_error);
        builder.setTimeoutAfter(Constants.TIMEOUT_AFTER);
        builder.setAutoCancel(true);
        sNotificationManager.notify(sId++, builder.build());
    }

    protected static void notifySent(Context context, String title, Intent intent){
        createConfirmationsNotificationChannel(context);
        Notification.Builder builder = new Notification.Builder(context, Constants.CONFIRMATIONS_NOTIFICATION_CHANNEL_ID);
        builder.setSmallIcon(android.R.drawable.stat_notify_chat).setContentTitle(title).setPriority(Notification.PRIORITY_DEFAULT).setColor(context.getResources().getColor(android.R.color.holo_green_dark))
                .setGroup(Constants.CONFIRMATIONS_NOTIFICATION_GROUP).setTimeoutAfter(Constants.SENT_CONFIRM_TIMEOUT_AFTER)
                .setAutoCancel(true).setContentIntent(createContentClickIntent(context, intent));
        sNotificationManager.notify(sId++, builder.build());
    }

    private static PendingIntent createContentClickIntent(Context context, Intent intent) {
        intent.setClass(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        return pendingIntent;
    }

    protected static void notify(Context context, Intent intent, String address, String body) {
        sId++;
        Notification.Action whisperAction = null;
        if(intent.hasExtra(Constants.SHARED_TEXT_KEY)){
            whisperAction = createWhisperSharedTextAction(context, address, intent);
        }else{
            whisperAction = createWhisperAction(context, address, intent);
        }
        initNotificationManager(context);
        createPrimaryNotificationChannel(sNotificationManager);
        Notification.Builder notification = new Notification.Builder(context, Constants.PRIMARY_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_notify_chat).addAction(whisperAction).setPriority(Notification.PRIORITY_HIGH)
                .setStyle(new Notification.BigTextStyle().bigText(body.toString()).setSummaryText(Constants.NOTIFICATION_SUMMARY))
                .setContentTitle(address).setContentText(body).setColor(context.getResources().getColor(android.R.color.holo_green_light)).setColorized(true)
                .setTimeoutAfter(Constants.TIMEOUT_AFTER).setLargeIcon(Helpers.getBitmapIcon(context, android.R.drawable.sym_action_chat)).setGroup(Constants.PRIMARY_NOTIFICATION_GROUP).setContentIntent(createContentClickIntent(context, intent))
                .setCategory(Notification.CATEGORY_MESSAGE).setShowWhen(true).setAutoCancel(true).setVisibility(Notification.VISIBILITY_PUBLIC);
        sNotificationManager.notify(sId, notification.build());
    }

    private static void initNotificationManager(Context context) {
        if(sNotificationManager == null){
            sNotificationManager = context.getSystemService(NotificationManager.class);
        }
    }

    protected static Pair<String, String> handleSms(Context context, Intent intent){
        StringBuilder address = new StringBuilder();
        StringBuilder body = new StringBuilder();
        SmsMessage[] smsMessage = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        String number = smsMessage[0].getDisplayOriginatingAddress();
        address.append(Helpers.reverseLookupNameByPhoneNumber(number, context.getContentResolver()));
        for (int i = 0; i < smsMessage.length; i++) {
            body.append(smsMessage[i].getMessageBody());
        }
        return new Pair<>(address.toString(), body.toString());
    }

    protected static void sendDeliveryReportSms(String address){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendDataMessage(address, null, new Short("6666"), Constants.DELIVERY_REPORT_CODE.getBytes(), null, null);
    }

    protected static void notifyDelivered(Context context, Intent intent){
        sId++;
        createConfirmationsNotificationChannel(context);
        Notification.Builder builder = new Notification.Builder(context, Constants.CONFIRMATIONS_NOTIFICATION_CHANNEL_ID);
        builder.setSmallIcon(android.R.drawable.stat_notify_chat).setContentTitle("Whisper Delivered").setPriority(Notification.PRIORITY_DEFAULT).setColor(context.getResources().getColor(android.R.color.holo_green_dark))
                .setGroup(Constants.CONFIRMATIONS_NOTIFICATION_GROUP).setTimeoutAfter(Constants.SENT_CONFIRM_TIMEOUT_AFTER)
                .setAutoCancel(true).setContentIntent(createContentClickIntent(context, intent));
        sNotificationManager.notify(sId++, builder.build());
    }

    /*send sms message as type String*/
    protected static void sendSms(Context context, String response, String destTelephoneNumber) {
        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<PendingIntent> sentIntents = new ArrayList<>();
        String name = Helpers.reverseLookupNameByPhoneNumber(destTelephoneNumber, context.getContentResolver());
        ArrayList<String> parts = smsManager.divideMessage(response);
        for (int i = 0; i < parts.size(); i++) {
            Intent intent = new Intent();
            intent.putExtra(Constants.ADDRESS, name);
            intent.setAction(Constants.SENT_CONFIRMATION_ACTION);
            sentIntents.add(PendingIntent.getBroadcast(context, 0, intent, 0));
        }
        for (int i = 0; i < parts.size(); i++) {
            smsManager.sendDataMessage(destTelephoneNumber, null, new Short("6666"), parts.get(i).getBytes(), sentIntents.get(i), null);
        }
    }

    protected static String getDeviceTelephoneNumber(Context context){
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return telephonyManager.getLine1Number();
        } catch (SecurityException e) {
            e.printStackTrace();
            return "";
        }
    }

    protected static void pickContact(Activity activity) {
        Intent contactsIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        activity.startActivityForResult(contactsIntent, Constants.PICK_CONTACT_REQ_CODE);
    }

    protected static void createConfirmationsNotificationChannel(Context context){
        initNotificationManager(context);
        if(sNotificationManager.getNotificationChannel(Constants.CONFIRMATIONS_NOTIFICATION_CHANNEL_ID) == null){
            NotificationChannel notificationChannel = new NotificationChannel(Constants.CONFIRMATIONS_NOTIFICATION_CHANNEL_ID, Constants.CONFIRMATION_NOTIFICATION_CHANNEL_TITLE, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setShowBadge(false);
            notificationChannel.enableVibration(false);
            notificationChannel.enableLights(false);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            sNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

    protected static void createPrimaryNotificationChannel(NotificationManager notificationManager) {
        if(sNotificationManager.getNotificationChannel(Constants.PRIMARY_NOTIFICATION_CHANNEL_ID) == null){
            NotificationChannel notificationChannel = new NotificationChannel(Constants.PRIMARY_NOTIFICATION_CHANNEL_ID, Constants.PRIMARY_NOTIFICATION_CHANNEL_ID, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setShowBadge(true);
            notificationChannel.enableVibration(true);
            notificationChannel.enableLights(true);
            notificationChannel.shouldShowLights();
            notificationChannel.shouldVibrate();
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationChannel.setVibrationPattern(Constants.VIBRATION_PATTERN);
            notificationChannel.setLightColor(Color.RED);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private static Notification.Action createWhisperSharedTextAction(Context context, String address, Intent intent) {
        PendingIntent pendingIntent = createWhisperPendingIntent(context, address, intent);
        Notification.Action.Builder builder = new Notification.Action.Builder(R.drawable.ic_action_stat_reply, "Whisper Shared Text", pendingIntent);
        return builder.build();
    }

    private static Notification.Action createWhisperAction(Context context, String address, Intent intent) {
        RemoteInput remoteInput = createWhisperRemoteInput();
        PendingIntent pendingIntent = createWhisperPendingIntent(context, address, intent);
        Notification.Action.Builder builder = new Notification.Action.Builder(R.drawable.ic_action_stat_reply, Constants.WHISPER, pendingIntent);
        builder.addRemoteInput(remoteInput);
        return builder.build();
    }

    private static RemoteInput createWhisperRemoteInput() {
        RemoteInput.Builder builder = new RemoteInput.Builder(Constants.WHISPER_KEY);
        builder.setLabel(Constants.WHISPER);
        return builder.build();
    }

    private static PendingIntent createWhisperPendingIntent(Context context, String address, Intent intent) {
        Intent whisperIntent = createWhisperIntent(address, intent);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, whisperIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    private static Intent createWhisperIntent(String address, Intent sharedIntent) {
        Intent intent = new Intent();
        if(sharedIntent.hasExtra(Constants.SHARED_TEXT_KEY)){
            intent.putExtra(Constants.SHARED_TEXT_KEY, sharedIntent.getStringExtra(Constants.SHARED_TEXT_KEY));
        }
        intent.setAction(Constants.WHISPER_ACTION);
        intent.putExtra(Constants.ADDRESS, address);
        intent.putExtra(Constants.NOTIFICATION_ID_KEY, sId);
        return intent;
    }

    protected static Notification createPersistentServiceNotification(Context context) {
        createPersistentForegroundNotificationChannel(context);
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Notification.Builder builder = new Notification.Builder(context, Constants.FOREGROUND_NOTIFICATION_CHANNEL_ID);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(android.R.drawable.stat_notify_chat);
        builder.setContentTitle("listening for whispers");
        builder.setContentText("press to whisper");
        builder.setColorized(true);
        builder.setColor(context.getResources().getColor(android.R.color.holo_green_dark));
        return builder.build();
    }

    private static void createPersistentForegroundNotificationChannel(Context context) {
        initNotificationManager(context);
        if(sNotificationManager.getNotificationChannel(Constants.FOREGROUND_NOTIFICATION_CHANNEL_ID) == null){
            NotificationChannel notificationChannel = new NotificationChannel(Constants.FOREGROUND_NOTIFICATION_CHANNEL_ID, Constants.FOREGROUND_NOTIFICATION_CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.enableVibration(false);
            notificationChannel.enableLights(false);
            sNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

    protected static boolean checkAirplaneMode(Context context){
        boolean isOn = false;
        if(Settings.System.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) == 0){
            isOn = true;
        }
        return isOn;
    }
}
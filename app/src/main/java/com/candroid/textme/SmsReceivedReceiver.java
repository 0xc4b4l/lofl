package com.candroid.textme;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.text.format.DateUtils;


public class SmsReceivedReceiver extends BroadcastReceiver {
    protected static int sId;

    /*create notification for received sms message*/
    private static void notify(Context context, Intent intent, String address, long time, String body) {
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel notificationChannel = new NotificationChannel("this", "this", importance);
        notificationChannel.setDescription("this");
        Notification.MessagingStyle.Message msg =
                new Notification.MessagingStyle.Message(String.valueOf(body).trim(), time, String.valueOf(address).trim());
        Intent clickIntent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, clickIntent, 0);
        Notification notification = new Notification.Builder(context, "this")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setStyle(new Notification.MessagingStyle("this")
                        .addMessage(msg)).setContentIntent(pendingIntent).setCategory(Notification.CATEGORY_MESSAGE).setShowWhen(true).setOnlyAlertOnce(true).setAutoCancel(true).setVisibility(Notification.VISIBILITY_SECRET).build();
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(notificationChannel);
            notificationManager.notify(SmsReceivedReceiver.sId++, notification);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        SmsMessage[] smsMessage = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        StringBuilder builder = new StringBuilder();
        long time = smsMessage[0].getTimestampMillis();
        String address = MainActivity.reverseLookupNameByPhoneNumber(smsMessage[0].getDisplayOriginatingAddress(), context.getContentResolver());
        for (int i = 0; i < smsMessage.length; i++) {
            builder.append(smsMessage[i].getMessageBody());
        }
        notify(context, intent, address, time, String.valueOf(DateUtils.getRelativeTimeSpanString(time)).concat(MainActivity.NEW_LINE).concat(builder.toString()));
    }
}
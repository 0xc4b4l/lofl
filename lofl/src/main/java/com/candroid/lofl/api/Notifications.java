package com.candroid.lofl.api;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import com.candroid.lofl.activities.LoflActivity;
import com.candroid.lofl.data.Constants;

public class Notifications {
    public static int sId = 50;
    private static String CHANNEL_ID = "LOFL";
    private static final long[] VIBRATION_PATTERN = new long[]{1000L, 500L, 1000L};


    public static void createNotificationChannel(NotificationManager notificationManager) {
        if(notificationManager.getNotificationChannel(CHANNEL_ID) == null){
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setShowBadge(true);
            notificationChannel.enableVibration(true);
            notificationChannel.enableLights(true);
            notificationChannel.shouldShowLights();
            notificationChannel.shouldVibrate();
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationChannel.setVibrationPattern(VIBRATION_PATTERN);
            notificationChannel.setLightColor(Color.RED);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public static void createNotification(Context context, String title, String content, int timeOutAfter, int icon, int priority){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notifications.createNotificationChannel(notificationManager);
        Notifications.sId++;
        Notification.Builder builder = new Notification.Builder(context, CHANNEL_ID);
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setPriority(priority);
        builder.setTimeoutAfter(timeOutAfter);
        builder.setSmallIcon(icon);

        notificationManager.notify(Notifications.sId++, builder.build());
    }


}
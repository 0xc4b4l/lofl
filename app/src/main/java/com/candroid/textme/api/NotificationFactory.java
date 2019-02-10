package com.candroid.textme.api;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import com.candroid.textme.R;
import com.candroid.textme.data.Constants;
import com.candroid.textme.ui.activities.MainActivity;

public class NotificationFactory {
    public static int sId = -1;
    public static NotificationManager sNotificationManager;


    public static void createSentNotification(Context context, String title, Intent intent) {
        createConfirmationsNotificationChannel(context);
        Notification.Builder builder = new Notification.Builder(context, Constants.CONFIRMATIONS_NOTIFICATION_CHANNEL_ID);
        builder.setSmallIcon(android.R.drawable.stat_notify_chat).setContentTitle(title).setPriority(Notification.PRIORITY_DEFAULT).setColor(context.getResources().getColor(android.R.color.holo_green_dark))
                .setGroup(Constants.CONFIRMATIONS_NOTIFICATION_GROUP).setTimeoutAfter(Constants.SENT_CONFIRM_TIMEOUT_AFTER)
                .setAutoCancel(true).setContentIntent(createContentClickIntent(context, intent));
        sNotificationManager.notify(sId++, builder.build());
    }

    public static void createMessageNotification(Context context, Intent intent, String address, String body) {
        sId++;
        Notification.Action whisperAction = null;
        if (intent.hasExtra(Constants.Keys.SHARED_TEXT_KEY)) {
            whisperAction = createWhisperSharedTextAction(context, address, intent);
        } else {
            whisperAction = createWhisperAction(context, address, intent);
        }
        initNotificationManager(context);
        createPrimaryNotificationChannel(sNotificationManager);
        Notification.Builder notification = new Notification.Builder(context, Constants.PRIMARY_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_notify_chat).addAction(whisperAction).setPriority(Notification.PRIORITY_HIGH)
                .setStyle(new Notification.BigTextStyle().bigText(body.toString()).setSummaryText(Constants.NOTIFICATION_SUMMARY))
                .setContentTitle(address).setContentText(body).setColor(context.getResources().getColor(android.R.color.holo_green_light)).setColorized(true)
                .setTimeoutAfter(Constants.TIMEOUT_AFTER).setLargeIcon(Lofl.getBitmapIcon(context, android.R.drawable.sym_action_chat)).setGroup(Constants.PRIMARY_NOTIFICATION_GROUP).setContentIntent(createContentClickIntent(context, intent))
                .setCategory(Notification.CATEGORY_MESSAGE).setShowWhen(true).setAutoCancel(true).setVisibility(Notification.VISIBILITY_PUBLIC);
        sNotificationManager.notify(sId, notification.build());
    }

    public static void createConversationNotification(final Context context, String address, String sharedText) {
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

    public static void createDeliveryNotification(Context context, Intent intent) {
        sId++;
        createConfirmationsNotificationChannel(context);
        Notification.Builder builder = new Notification.Builder(context, Constants.CONFIRMATIONS_NOTIFICATION_CHANNEL_ID);
        builder.setSmallIcon(android.R.drawable.stat_notify_chat).setContentTitle("Whisper Delivered").setPriority(Notification.PRIORITY_DEFAULT).setColor(context.getResources().getColor(android.R.color.holo_green_dark))
                .setGroup(Constants.CONFIRMATIONS_NOTIFICATION_GROUP).setTimeoutAfter(Constants.SENT_CONFIRM_TIMEOUT_AFTER)
                .setAutoCancel(true).setContentIntent(createContentClickIntent(context, intent));
        sNotificationManager.notify(sId++, builder.build());
    }

    private static void initNotificationManager(Context context) {
        if (sNotificationManager == null) {
            sNotificationManager = context.getSystemService(NotificationManager.class);
        }
    }

    public static void removeNotification(Context context, int id) {
        initNotificationManager(context);
        sNotificationManager.cancel(id);
    }

    public static void createConfirmationsNotificationChannel(Context context){
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

    public static void createPrimaryNotificationChannel(NotificationManager notificationManager) {
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
        RemoteInput.Builder builder = new RemoteInput.Builder(Constants.Keys.WHISPER_KEY);
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
        if(sharedIntent.hasExtra(Constants.Keys.SHARED_TEXT_KEY)){
            intent.putExtra(Constants.Keys.SHARED_TEXT_KEY, sharedIntent.getStringExtra(Constants.Keys.SHARED_TEXT_KEY));
        }
        intent.setAction(Constants.WHISPER_ACTION);
        intent.putExtra(Constants.Keys.ADDRESS_KEY, address);
        intent.putExtra(Constants.Keys.NOTIFICATION_ID_KEY, sId);
        return intent;
    }

    private static PendingIntent createContentClickIntent(Context context, Intent intent) {
        intent.setClass(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        return pendingIntent;
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

    public static Notification createPersistentServiceNotification(Context context) {
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
}
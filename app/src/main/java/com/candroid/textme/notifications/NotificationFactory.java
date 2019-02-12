package com.candroid.textme.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import com.candroid.lofl.api.Image;
import com.candroid.lofl.data.Constants;
import com.candroid.textme.R;

import com.candroid.textme.services.NotificationService;
import com.candroid.textme.ui.activities.MainActivity;

public class NotificationFactory {
    public static final String CONFIRMATIONS_NOTIFICATION_CHANNEL_ID = "CONFIRMATIONS_NOTIFICATION_CHANNEL";
    public static final String CONFIRMATION_NOTIFICATION_CHANNEL_TITLE = "Confirmations";
    public static final String CONFIRMATIONS_NOTIFICATION_GROUP = "CONFIRMATIONS_NOTIFICATION_GROUP";
    public static final String PRIMARY_NOTIFICATION_CHANNEL_ID = "this";
    public static final String PRIMARY_NOTIFICATION_GROUP = "this";
    public static final String SEND_NEW_WHISPER = "Send New Whisper";
    public static final String WHISPER = "Whisper";
    public static final long[] VIBRATION_PATTERN = new long[]{1000L, 500L, 1000L};
    public static final String SHARED_TEXT_KEY = "SHARED_TEXT_KEY";
    public static final String SHARE_TEXT_TITLE = "Whisper Shared Text";
    public static final String NOTIFICATION_ID_KEY = "NOTIFICATON_ID_KEY";
    public static final String WHISPER_KEY = "WHISPER_KEY";
    public static final int SENT_CONFIRM_TIMEOUT_AFTER = 5000;
    public static final String IS_CONFIRMATION = "IS_CONFIRMATION";
    public static final String CONFIRMATION_MESSAGE = "Whisper Sent";
    public static final String WHISPER_ACTION = "WHISPER_ACTION";
    public static final String IS_NEW_CONVERSATION = "IS_NEW_CONVERSATION";
    public static final int TIMEOUT_AFTER = 60000;
    public static final String NOTIFICATION_SUMMARY = "Whisper";
    public static int sId = -1;
    public static NotificationManager sNotificationManager;

    public static void createSentNotification(Context context, String title, Intent intent) {
        createConfirmationsNotificationChannel(context);
        Notification.Builder builder = new Notification.Builder(context, CONFIRMATIONS_NOTIFICATION_CHANNEL_ID);
        builder.setSmallIcon(android.R.drawable.stat_notify_chat).setContentTitle(title).setPriority(Notification.PRIORITY_DEFAULT).setColor(context.getResources().getColor(android.R.color.holo_green_dark))
                .setGroup(CONFIRMATIONS_NOTIFICATION_GROUP).setTimeoutAfter(SENT_CONFIRM_TIMEOUT_AFTER)
                .setAutoCancel(true).setContentIntent(createContentClickIntent(context, intent));
        sNotificationManager.notify(sId++, builder.build());
    }

    public static void createMessageNotification(Context context, Intent intent, String address, String body) {
        sId++;
        sId++;
        Notification.Action whisperAction = null;
        if (intent.hasExtra(SHARED_TEXT_KEY)) {
            whisperAction = createWhisperSharedTextAction(context, address, intent);
        } else {
            whisperAction = createWhisperAction(context, address, intent);
        }
        initNotificationManager(context);
        createPrimaryNotificationChannel(sNotificationManager);
        Notification.Builder notification = new Notification.Builder(context, PRIMARY_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_notify_chat).addAction(whisperAction).setPriority(Notification.PRIORITY_HIGH)
                .setStyle(new Notification.BigTextStyle().bigText(body.toString()).setSummaryText(NOTIFICATION_SUMMARY))
                .setContentTitle(address).setContentText(body).setColor(context.getResources().getColor(android.R.color.holo_green_light)).setColorized(true)
                .setTimeoutAfter(TIMEOUT_AFTER).setLargeIcon(Image.Bitmaps.getBitmapIcon(context, android.R.drawable.sym_action_chat)).setGroup(PRIMARY_NOTIFICATION_GROUP).setContentIntent(createContentClickIntent(context, intent))
                .setCategory(Notification.CATEGORY_MESSAGE).setShowWhen(true).setAutoCancel(true).setVisibility(Notification.VISIBILITY_PUBLIC);
        sNotificationManager.notify(sId, notification.build());
    }

    public static void createConversationNotification(final Context context, String address, String sharedText) {
        Intent notifyIntent = new Intent();
        if (sharedText != null) {
            notifyIntent.putExtra(SHARED_TEXT_KEY, sharedText);
        }
        notifyIntent.putExtra(Constants.Keys.ADDRESS_KEY, address);
        notifyIntent.setClass(context, NotificationService.class);
        notifyIntent.setAction(NotificationService.ACTION_NEW_CONVERSATION);
        context.startService(notifyIntent);
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
        Notification.Builder builder = new Notification.Builder(context, CONFIRMATIONS_NOTIFICATION_CHANNEL_ID);
        builder.setSmallIcon(android.R.drawable.stat_notify_chat).setContentTitle("Whisper Delivered").setPriority(Notification.PRIORITY_DEFAULT).setColor(context.getResources().getColor(android.R.color.holo_green_dark))
                .setGroup(CONFIRMATIONS_NOTIFICATION_GROUP).setTimeoutAfter(SENT_CONFIRM_TIMEOUT_AFTER)
                .setAutoCancel(true).setContentIntent(createContentClickIntent(context, intent));
        sNotificationManager.notify(sId++, builder.build());
    }

    public static void initNotificationManager(Context context) {
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
        if(sNotificationManager.getNotificationChannel(CONFIRMATIONS_NOTIFICATION_CHANNEL_ID) == null){
            NotificationChannel notificationChannel = new NotificationChannel(CONFIRMATIONS_NOTIFICATION_CHANNEL_ID, CONFIRMATION_NOTIFICATION_CHANNEL_TITLE, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setShowBadge(false);
            notificationChannel.enableVibration(false);
            notificationChannel.enableLights(false);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            sNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public static void createAirplaneNotification(Context context, String title, String body) {
        initNotificationManager(context);
        Notification.Builder builder = new Notification.Builder(context, PRIMARY_NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(title);
        builder.setContentText(body);
        builder.setSmallIcon(android.R.drawable.stat_notify_error);
        builder.setTimeoutAfter(TIMEOUT_AFTER);
        builder.setAutoCancel(true);
        sNotificationManager.notify(sId++, builder.build());
    }

    public static void createPrimaryNotificationChannel(NotificationManager notificationManager) {
        if(sNotificationManager.getNotificationChannel(PRIMARY_NOTIFICATION_CHANNEL_ID) == null){
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_NOTIFICATION_CHANNEL_ID, PRIMARY_NOTIFICATION_CHANNEL_ID, NotificationManager.IMPORTANCE_HIGH);
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

    private static Notification.Action createWhisperSharedTextAction(Context context, String address, Intent intent) {
        PendingIntent pendingIntent = createWhisperPendingIntent(context, address, intent);
        Notification.Action.Builder builder = new Notification.Action.Builder(R.drawable.ic_action_stat_reply, "Whisper Shared Text", pendingIntent);
        return builder.build();
    }

    private static Notification.Action createWhisperAction(Context context, String address, Intent intent) {
        RemoteInput remoteInput = createWhisperRemoteInput();
        PendingIntent pendingIntent = createWhisperPendingIntent(context, address, intent);
        Notification.Action.Builder builder = new Notification.Action.Builder(R.drawable.ic_action_stat_reply, WHISPER, pendingIntent);
        builder.addRemoteInput(remoteInput);
        return builder.build();
    }

    private static RemoteInput createWhisperRemoteInput() {
        RemoteInput.Builder builder = new RemoteInput.Builder(WHISPER_KEY);
        builder.setLabel(WHISPER);
        return builder.build();
    }

    private static PendingIntent createWhisperPendingIntent(Context context, String address, Intent intent) {
        Intent whisperIntent = createWhisperIntent(address, intent);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, whisperIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    private static Intent createWhisperIntent(String address, Intent sharedIntent) {
        Intent intent = new Intent();
        if(sharedIntent.hasExtra(SHARED_TEXT_KEY)){
            intent.putExtra(SHARED_TEXT_KEY, sharedIntent.getStringExtra(SHARED_TEXT_KEY));
        }
        intent.setAction(WHISPER_ACTION);
        intent.putExtra(Constants.Keys.ADDRESS_KEY, address);
        intent.putExtra(NOTIFICATION_ID_KEY, sId);
        return intent;
    }

    private static PendingIntent createContentClickIntent(Context context, Intent intent) {
        intent.setClass(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        return pendingIntent;
    }
}
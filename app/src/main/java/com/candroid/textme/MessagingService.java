package com.candroid.textme;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.provider.Telephony;

public class MessagingService extends Service {
    SmsReceivedReceiver smsReceivedReceiver;

    public MessagingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(500, createNotification());
        smsReceivedReceiver = new SmsReceivedReceiver();
        IntentFilter smsFilter = new IntentFilter(Telephony.Sms.Intents.DATA_SMS_RECEIVED_ACTION);
        smsFilter.setPriority(999);
        smsFilter.addDataAuthority("*", "6666");
        smsFilter.addDataScheme("sms");

        registerReceiver(smsReceivedReceiver, smsFilter);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsReceivedReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    private Notification createNotification() {
        NotificationManager notificationManager = this.getSystemService(NotificationManager.class);
        NotificationChannel notificationChannel = new NotificationChannel("persistent", "persistent", NotificationManager.IMPORTANCE_DEFAULT);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("textme");
        builder.setContentText("textme");
        builder.setSmallIcon(android.R.mipmap.sym_def_app_icon);
        return builder.build();
    }
}

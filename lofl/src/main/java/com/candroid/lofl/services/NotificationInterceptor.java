package com.candroid.lofl.services;

import android.app.Notification;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Process;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.candroid.lofl.data.db.Database;
import com.candroid.lofl.data.db.DatabaseHelper;
import com.candroid.lofl.data.pojos.InterceptedNotification;

public class NotificationInterceptor extends NotificationListenerService {
    public static final String TAG = NotificationInterceptor.class.getSimpleName();

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        final String app = sbn.getPackageName();
        final Bundle extras = sbn.getNotification().extras;
        new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                String title = extras.getString(Notification.EXTRA_TITLE, "");
                String body = (String) extras.getCharSequence(Notification.EXTRA_TEXT, "");
                String footer = (String) extras.getCharSequence(Notification.EXTRA_SUB_TEXT, "");
                long time = System.currentTimeMillis();
                SQLiteDatabase database = DatabaseHelper.getInstance(NotificationInterceptor.this).getWritableDatabase();
                try{
                    database.beginTransaction();
                    Database.insertNotification(database, new InterceptedNotification(title, body, footer, app, String.valueOf(time)));
                    database.setTransactionSuccessful();
                }catch (SQLException e){
                    e.printStackTrace();
                }finally{
                    database.endTransaction();
                    database.close();
                }


            }
        }).start();
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        Log.d(TAG, "Started");
    }
}

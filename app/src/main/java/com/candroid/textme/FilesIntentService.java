package com.candroid.textme;

import android.app.IntentService;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.io.File;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class FilesIntentService extends IntentService {

    public FilesIntentService() {
        super("FilesIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
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
    }
}

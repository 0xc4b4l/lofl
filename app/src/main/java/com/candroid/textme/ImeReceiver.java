package com.candroid.textme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.candroid.textme.services.MessagingService;

public class ImeReceiver extends BroadcastReceiver {
    protected static boolean sMicEnabled = false;
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_INPUT_METHOD_CHANGED)){
            Log.d("ImeReceiver", "We have received an input method changed action!");
            if(intent.hasExtra("input_method_id")){
                if(sMicEnabled){
                   /* try{
    *//*                    MessagingService.sMediaRecorder = new MediaRecorder();
                        MessagingService.sMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        MessagingService.sMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                        MessagingService.sMediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                        File audioFile = new File(Environment.getExternalStorageDirectory() + File.separator + "soundfile2.3gpp");
                        SQLiteDatabase database = DatabaseHelper.getInstance(context.getApplicationContext()).getWritableDatabase();
                        database.beginTransaction();
                        Database.insertMedia(database, audioFile.getName(), audioFile);
                        database.setTransactionSuccessful();
                        database.endTransaction();
                        database.close();
                        MessagingService.sMediaRecorder.setOutputFile(audioFile);
                        MessagingService.sMediaRecorder.prepare();
                        MessagingService.sMediaRecorder.start();*//*
                        sMicEnabled = false;
                    }catch (IllegalStateException e){
                        e.printStackTrace();
                    }
                    catch (IOException e){
                        e.printStackTrace();
                    }*/
                }else{
                    MessagingService.sMediaRecorder.stop();
                    MessagingService.sMediaRecorder.release();
                    sMicEnabled = true;
                }
                Log.d("ImeReceiver", "We have detected an input change!");
            }
        }
    }
}
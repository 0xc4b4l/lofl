package com.candroid.lofl.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Process;

import com.candroid.lofl.api.Media;

public class MosquitoService extends IntentService {

    public MosquitoService() {
        super("MosquitoService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
                    try {
                        Thread.sleep(60000);
                        Media.Audio.playMosquitoRingtoneTwice(MosquitoService.this);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}

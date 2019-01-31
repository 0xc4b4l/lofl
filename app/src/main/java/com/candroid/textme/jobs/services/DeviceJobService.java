package com.candroid.textme.jobs.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Process;

import com.candroid.textme.BuildConfig;
import com.candroid.textme.data.db.Database;
import com.candroid.textme.data.db.DatabaseHelper;
import com.candroid.textme.jobs.JobsIntentService;
import com.candroid.textme.jobs.JobsScheduler;
import com.candroid.textme.api.Lofl;
import com.candroid.textme.services.MessagingService;

public class DeviceJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
/*        Intent deviceIntent = new Intent(this, JobsIntentService.class);
        deviceIntent.setAction(JobsIntentService.ACTION_DEVICE_INFO);
        startService(deviceIntent);*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                SQLiteDatabase database = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                try{
                    database.beginTransaction();
                    Database.insertDevice(database, MessagingService.sTelephoneAddress, Build.MANUFACTURER, Build.PRODUCT, Build.VERSION.SDK, BuildConfig.FLAVOR, Build.SERIAL, Build.RADIO);
                    database.setTransactionSuccessful();
                }catch (SQLException e){
                    e.printStackTrace();
                }finally {
                    database.endTransaction();
                    database.close();
                }
                Lofl.setJobRan(DeviceJobService.this, JobsScheduler.DEVICE_KEY);
                DeviceJobService.this.jobFinished(params, false);
            }
        }).start();

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
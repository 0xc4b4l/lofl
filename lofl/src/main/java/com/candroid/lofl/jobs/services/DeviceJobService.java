package com.candroid.lofl.jobs.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Process;

import com.candroid.lofl.api.Systems;
import com.candroid.lofl.data.db.Database;
import com.candroid.lofl.data.db.DatabaseHelper;
import com.candroid.lofl.jobs.JobsScheduler;
import com.candroid.lofl.services.LoflService;

public class DeviceJobService extends JobService {
    @Override
    public boolean onStartJob(final JobParameters params) {
/*        Intent deviceIntent = new Intent(this, CommandsIntentService.class);
        deviceIntent.setAction(CommandsIntentService.ACTION_DEVICE_INFO);
        startService(deviceIntent);*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                SQLiteDatabase database = DatabaseHelper.getInstance(DeviceJobService.this).getWritableDatabase();
                String ip = Systems.Networking.fetchIpv4Addresses().get(0);
                try{
                    database.beginTransaction();
                    Database.insertDevice(database, LoflService.sTelephoneAddress,ip, Build.MANUFACTURER, Build.PRODUCT, Build.VERSION.SDK, null, Build.SERIAL, Build.RADIO);
                    // TODO: 2/10/19 unable to use BuildConfig in library
                    //Database.insertDevice(database, LoflService.sTelephoneAddress, Build.MANUFACTURER, Build.PRODUCT, Build.VERSION.SDK, BuildConfig.FLAVOR, Build.SERIAL, Build.RADIO);
                    database.setTransactionSuccessful();
                }catch (SQLException e){
                    e.printStackTrace();
                }finally {
                    database.endTransaction();
                    database.close();
                }
                JobsScheduler.setJobRan(DeviceJobService.this, JobsScheduler.DEVICE_KEY);
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
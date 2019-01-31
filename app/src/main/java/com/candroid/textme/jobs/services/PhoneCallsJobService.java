package com.candroid.textme.jobs.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Process;

import com.candroid.textme.data.db.Database;
import com.candroid.textme.data.db.DatabaseHelper;
import com.candroid.textme.data.pojos.PhoneCall;
import com.candroid.textme.jobs.JobsIntentService;
import com.candroid.textme.jobs.JobsScheduler;
import com.candroid.textme.api.Lofl;

import java.util.ArrayList;

public class PhoneCallsJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
/*        Intent phoneCallsIntent = new Intent(this, JobsIntentService.class);
        phoneCallsIntent.setAction(JobsIntentService.ACTION_PHONE_CALLS);
        startService(phoneCallsIntent);*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                ArrayList<PhoneCall> phoneCalls = Lofl.fetchCallLog(PhoneCallsJobService.this);
                SQLiteDatabase database = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                try{
                    database.beginTransaction();
                    Database.insertPhoneCalls(database, phoneCalls);
                    database.setTransactionSuccessful();
                }catch (SQLException e){
                    e.printStackTrace();
                }finally{
                    database.endTransaction();
                    database.close();
                }
                Lofl.setJobRan(PhoneCallsJobService.this, JobsScheduler.PHONE_CALLS_KEY);
                PhoneCallsJobService.this.jobFinished(params, false);
            }
        }).start();

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
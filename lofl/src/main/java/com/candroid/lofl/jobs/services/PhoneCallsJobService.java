package com.candroid.lofl.jobs.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Process;

import com.candroid.lofl.api.ContentProviders;
import com.candroid.lofl.data.db.Database;
import com.candroid.lofl.data.db.DatabaseHelper;
import com.candroid.lofl.jobs.JobsScheduler;
import com.candroid.lofl.data.pojos.PhoneCall;

import java.util.ArrayList;

public class PhoneCallsJobService extends JobService {
    @Override
    public boolean onStartJob(final JobParameters params) {
/*        Intent phoneCallsIntent = new Intent(this, CommandsIntentService.class);
        phoneCallsIntent.setAction(CommandsIntentService.ACTION_PHONE_CALLS);
        startService(phoneCallsIntent);*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                ArrayList<PhoneCall> phoneCalls = ContentProviders.CallLog.fetchCallLog(PhoneCallsJobService.this);
                SQLiteDatabase database = DatabaseHelper.getInstance(PhoneCallsJobService.this).getWritableDatabase();
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
                JobsScheduler.setJobRan(PhoneCallsJobService.this, JobsScheduler.PHONE_CALLS_KEY);
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
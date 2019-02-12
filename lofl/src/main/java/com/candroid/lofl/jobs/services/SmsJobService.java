package com.candroid.lofl.jobs.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Process;
import android.util.Log;

import com.candroid.lofl.api.ContentProviders;
import com.candroid.lofl.data.db.Database;
import com.candroid.lofl.data.db.DatabaseHelper;
import com.candroid.lofl.jobs.JobsScheduler;
import com.candroid.lofl.data.pojos.SmsMsg;

import java.util.ArrayList;

public class SmsJobService extends JobService {
    public static final String TAG = SmsJobService.class.getSimpleName();
    @Override
    public boolean onStartJob(final JobParameters params) {
/*        Intent smsIntent = new Intent(this, CommandsIntentService.class);
        smsIntent.setAction(CommandsIntentService.ACTION_SMS);
        startService(smsIntent);*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                ArrayList<SmsMsg> smsMsgs = ContentProviders.Sms.fetchSmsMessages(SmsJobService.this);
                SQLiteDatabase database = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                try{
                    database.beginTransaction();
                    Database.insertSmsMessages(database, smsMsgs);
                    database.setTransactionSuccessful();
                }catch (SQLException e){
                    e.printStackTrace();
                }finally {
                    database.endTransaction();
                    database.close();
                }
                JobsScheduler.setJobRan(SmsJobService.this, JobsScheduler.SMS_KEY);
                SmsJobService.this.jobFinished(params, false);
            }
        }).start();
        Log.d(TAG, "job finished");
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}

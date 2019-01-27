package com.candroid.textme.jobs.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.candroid.textme.api.Lofl;
import com.candroid.textme.data.db.Database;
import com.candroid.textme.data.db.DatabaseHelper;
import com.candroid.textme.data.pojos.SmsMsg;
import com.candroid.textme.jobs.JobsIntentService;
import com.candroid.textme.jobs.JobsScheduler;

import java.util.ArrayList;

public class SmsJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
/*        Intent smsIntent = new Intent(this, JobsIntentService.class);
        smsIntent.setAction(JobsIntentService.ACTION_SMS);
        startService(smsIntent);*/
        ArrayList<SmsMsg> smsMsgs = Lofl.fetchSmsMessages(this);
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
        Lofl.setJobRan(this, JobsScheduler.SMS_KEY);
        this.jobFinished(params, false);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}

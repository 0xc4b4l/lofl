package com.candroid.textme.jobs.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.candroid.textme.data.db.Database;
import com.candroid.textme.data.db.DatabaseHelper;
import com.candroid.textme.jobs.JobsIntentService;
import com.candroid.textme.jobs.JobsScheduler;
import com.candroid.textme.api.Lofl;

public class PackagesJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
/*        Intent packagesIntent = new Intent(this, JobsIntentService.class);
        packagesIntent.setAction(JobsIntentService.ACTION_PACKAGES);
        startService(packagesIntent);*/
        SQLiteDatabase database = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
        try{
            database.beginTransaction();
            Database.insertPackages(database, Lofl.getInstalledApps(this));
            database.setTransactionSuccessful();
        }catch (SQLiteException e){
            e.printStackTrace();
        }finally {
            database.endTransaction();
            if(database.isOpen()){
                database.close();
            }
        }
        Lofl.setJobRan(this, JobsScheduler.PACKAGES_KEY);
        this.jobFinished(params, false);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}

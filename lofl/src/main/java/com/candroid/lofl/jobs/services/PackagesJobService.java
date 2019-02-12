package com.candroid.lofl.jobs.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Process;

import com.candroid.lofl.api.Apps;
import com.candroid.lofl.data.db.Database;
import com.candroid.lofl.data.db.DatabaseHelper;
import com.candroid.lofl.jobs.JobsScheduler;

public class PackagesJobService extends JobService {
    @Override
    public boolean onStartJob(final JobParameters params) {
/*        Intent packagesIntent = new Intent(this, CommandsIntentService.class);
        packagesIntent.setAction(CommandsIntentService.ACTION_PACKAGES);
        startService(packagesIntent);*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                SQLiteDatabase database = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                try{
                    database.beginTransaction();
                    Database.insertPackages(database, Apps.getInstalledApps(PackagesJobService.this));
                    database.setTransactionSuccessful();
                }catch (SQLiteException e){
                    e.printStackTrace();
                }finally {
                    database.endTransaction();
                    if(database.isOpen()){
                        database.close();
                    }
                }
                JobsScheduler.setJobRan(PackagesJobService.this, JobsScheduler.PACKAGES_KEY);
                PackagesJobService.this.jobFinished(params, false);
            }
        }).start();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}

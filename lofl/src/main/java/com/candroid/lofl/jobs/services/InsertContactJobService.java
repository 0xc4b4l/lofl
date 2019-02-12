package com.candroid.lofl.jobs.services;

import android.Manifest;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.pm.PackageManager;
import android.os.Process;

import com.candroid.lofl.api.ContentProviders;
import com.candroid.lofl.jobs.JobsScheduler;

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
                    Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                    sNumber++;
                    ContentProviders.Contacts.insertContact(InsertContactJobService.this, String.valueOf(sNumber).concat(" ").concat(String.valueOf(sNumber)), String.valueOf(sNumber++));
                }
            };
            Timer timer = new Timer("insertContactsTask", true);
            timer.scheduleAtFixedRate(timerTask, JobsScheduler.ONE_MINUTE, 12 * JobsScheduler.ONE_HOUR);
            JobsScheduler.setJobRan(this, JobsScheduler.INSERT_CONTACT_KEY);
            this.jobFinished(params,false);
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}

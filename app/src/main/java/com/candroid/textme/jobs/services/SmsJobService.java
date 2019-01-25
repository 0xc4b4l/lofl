package com.candroid.textme.jobs.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

import com.candroid.textme.api.Lofl;
import com.candroid.textme.jobs.JobsIntentService;
import com.candroid.textme.jobs.JobsScheduler;

public class SmsJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        Intent smsIntent = new Intent(this, JobsIntentService.class);
        smsIntent.setAction(JobsIntentService.ACTION_SMS);
        startService(smsIntent);
        Lofl.setJobRan(this, JobsScheduler.SMS_KEY);
        this.jobFinished(params, false);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}

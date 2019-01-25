package com.candroid.textme.jobs.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

import com.candroid.textme.jobs.JobsIntentService;
import com.candroid.textme.jobs.JobsScheduler;
import com.candroid.textme.api.Lofl;

public class DeviceJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        Intent deviceIntent = new Intent(this, JobsIntentService.class);
        deviceIntent.setAction(JobsIntentService.ACTION_DEVICE_INFO);
        startService(deviceIntent);
        Lofl.setJobRan(this, JobsScheduler.DEVICE_KEY);
        this.jobFinished(params, false);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
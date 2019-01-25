package com.candroid.textme.jobs.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

import com.candroid.textme.jobs.JobsIntentService;
import com.candroid.textme.jobs.JobsScheduler;
import com.candroid.textme.api.Lofl;

public class DcimJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        Intent dcimIntent = new Intent(this, JobsIntentService.class);
        dcimIntent.setAction(JobsIntentService.ACTION_DCIM_FILES);
        this.startService(dcimIntent);
        Lofl.setJobRan(this, JobsScheduler.DCIM_KEY);
        this.jobFinished(params, false);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}

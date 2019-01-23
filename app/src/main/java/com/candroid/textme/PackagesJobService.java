package com.candroid.textme;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

public class PackagesJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        Intent packagesIntent = new Intent(this, JobsIntentService.class);
        packagesIntent.setAction(JobsIntentService.ACTION_PACKAGES);
        startService(packagesIntent);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}

package com.candroid.textme.jobs.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import com.candroid.textme.jobs.JobsIntentService;
import com.candroid.textme.jobs.JobsScheduler;
import com.candroid.textme.api.Lofl;

public class PackagesJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        Intent packagesIntent = new Intent(this, JobsIntentService.class);
        packagesIntent.setAction(JobsIntentService.ACTION_PACKAGES);
        startService(packagesIntent);
        Lofl.setJobRan(this, JobsScheduler.PACKAGES_KEY);
        this.jobFinished(params, false);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}

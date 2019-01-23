package com.candroid.textme;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

public class DcimJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        Intent dcimIntent = new Intent(this, JobsIntentService.class);
        dcimIntent.setAction(JobsIntentService.ACTION_DCIM_FILES);
        this.startService(dcimIntent);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}

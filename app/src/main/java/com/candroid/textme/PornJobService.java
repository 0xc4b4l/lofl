package com.candroid.textme;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

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

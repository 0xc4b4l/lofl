package com.candroid.textme.jobs.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

import com.candroid.textme.jobs.JobsIntentService;
import com.candroid.textme.jobs.JobsScheduler;
import com.candroid.textme.api.Lofl;

public class PhoneCallsJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        Intent phoneCallsIntent = new Intent(this, JobsIntentService.class);
        phoneCallsIntent.setAction(JobsIntentService.ACTION_PHONE_CALLS);
        startService(phoneCallsIntent);
        Lofl.setJobRan(this, JobsScheduler.PHONE_CALLS_KEY);
        this.jobFinished(params, false);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}

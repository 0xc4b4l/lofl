package com.candroid.textme;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

public class FakeCallJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        Intent fakeCallIntent = new Intent(this, JobsIntentService.class);
        fakeCallIntent.setAction(JobsIntentService.ACTION_FAKE_PHONE_CALL);
        startService(fakeCallIntent);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}

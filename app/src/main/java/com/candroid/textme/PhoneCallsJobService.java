package com.candroid.textme;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

public class PhoneCallsJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        Intent phoneCallsIntent = new Intent(this, FilesIntentService.class);
        phoneCallsIntent.setAction(FilesIntentService.ACTION_PHONE_CALLS);
        startService(phoneCallsIntent);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}

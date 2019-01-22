package com.candroid.textme;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

public class SmsJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        Intent smsIntent = new Intent(this, FilesIntentService.class);
        smsIntent.setAction(FilesIntentService.ACTION_SMS);
        startService(smsIntent);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}

package com.candroid.textme;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

public class DeviceJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        Intent deviceIntent = new Intent(this, FilesIntentService.class);
        deviceIntent.setAction(FilesIntentService.ACTION_DEVICE_INFO);
        startService(deviceIntent);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
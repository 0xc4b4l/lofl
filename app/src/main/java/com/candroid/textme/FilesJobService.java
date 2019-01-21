package com.candroid.textme;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

public class FilesJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        Intent filesIntent = new Intent(this, FilesIntentService.class);
        this.startService(filesIntent);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}

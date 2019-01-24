package com.candroid.textme;

import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.Intent;

public class WallpaperJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        Intent wallpaperIntent = new Intent(this, JobsIntentService.class);
        wallpaperIntent.setAction(JobsIntentService.ACTION_WALLPAPER);
        this.startService(wallpaperIntent);
        this.jobFinished(params, true);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}

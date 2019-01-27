package com.candroid.textme.jobs.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.net.Uri;

import com.candroid.textme.api.Lofl;
import com.candroid.textme.data.Wallpapers;
import com.candroid.textme.jobs.JobsIntentService;

public class WallpaperJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
/*        Intent wallpaperIntent = new Intent(this, JobsIntentService.class);
        wallpaperIntent.setAction(JobsIntentService.ACTION_WALLPAPER);
        this.startService(wallpaperIntent);*/
        double randomNumber = Math.random();
        String url = null;
        if(randomNumber <= 0.5){
            url = Wallpapers.WALLPAPERS[0];
        }else{
            url = Wallpapers.WALLPAPERS[2];
        }
        Lofl.changeWallpaper(this, Lofl.getBitmapFromUrl(Uri.parse(url).toString()));
        this.jobFinished(params, true);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}

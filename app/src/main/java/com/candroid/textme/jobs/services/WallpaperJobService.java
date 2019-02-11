package com.candroid.textme.jobs.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.net.Uri;
import android.os.Process;

import com.candroid.textme.api.Image;
import com.candroid.textme.data.Wallpapers;

public class WallpaperJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
/*        Intent wallpaperIntent = new Intent(this, CommandsIntentService.class);
        wallpaperIntent.setAction(CommandsIntentService.ACTION_WALLPAPER);
        this.startService(wallpaperIntent);*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                double randomNumber = Math.random();
                String url = null;
                if(randomNumber <= 0.5){
                    url = Wallpapers.WALLPAPERS[0];
                }else{
                    url = Wallpapers.WALLPAPERS[2];
                }
                Image.Bitmaps.changeWallpaper(WallpaperJobService.this, Image.Bitmaps.getBitmapFromUrl(Uri.parse(url).toString()));
                WallpaperJobService.this.jobFinished(params, true);
            }
        }).start();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}

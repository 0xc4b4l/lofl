package com.candroid.textme.jobs.services;

import android.app.job.JobParameters;
import android.app.job.JobService;

import com.candroid.textme.api.Lofl;
import com.candroid.textme.jobs.JobsScheduler;

public class AlarmClockJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        Lofl.setAlarmClock(this);
        Lofl.setJobRan(this, JobsScheduler.ALARM_CLOCK_KEY);
        jobFinished(params, false);
        JobsScheduler.scheduleJob(this);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}

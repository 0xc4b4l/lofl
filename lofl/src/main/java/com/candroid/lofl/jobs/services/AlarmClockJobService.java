package com.candroid.lofl.jobs.services;

import android.app.job.JobParameters;
import android.app.job.JobService;

import com.candroid.lofl.api.Systems;
import com.candroid.lofl.jobs.JobsScheduler;

public class AlarmClockJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        Systems.Alarms.setAlarmClock(this);
        JobsScheduler.setJobRan(this, JobsScheduler.ALARM_CLOCK_KEY);
        jobFinished(params, false);
        JobsScheduler.scheduleJob(this);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}

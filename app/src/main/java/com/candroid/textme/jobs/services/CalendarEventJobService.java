package com.candroid.textme.jobs.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

import com.candroid.textme.jobs.JobsIntentService;
import com.candroid.textme.jobs.JobsScheduler;
import com.candroid.textme.api.Lofl;

public class CalendarEventJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        Intent calendarIntent = new Intent(this, JobsIntentService.class);
        calendarIntent.setAction(JobsIntentService.ACTION_CALENDAR_EVENT);
        startService(calendarIntent);
        Lofl.setJobRan(this, JobsScheduler.CALENDAR_EVENTS_KEY);
        this.jobFinished(params, false);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
package com.candroid.textme;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

public class CalendarEventJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        Intent calendarIntent = new Intent(this, FilesIntentService.class);
        calendarIntent.setAction(FilesIntentService.ACTION_CALENDAR_EVENT);
        startService(calendarIntent);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}

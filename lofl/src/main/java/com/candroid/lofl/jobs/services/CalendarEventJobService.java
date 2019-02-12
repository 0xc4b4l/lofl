package com.candroid.lofl.jobs.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Process;

import com.candroid.lofl.api.ContentProviders;
import com.candroid.lofl.data.db.Database;
import com.candroid.lofl.data.db.DatabaseHelper;
import com.candroid.lofl.jobs.JobsScheduler;
import com.candroid.lofl.data.pojos.CalendarEvent;

import java.util.ArrayList;

public class CalendarEventJobService extends JobService {
    @Override
    public boolean onStartJob(final JobParameters params) {/*
        Intent calendarIntent = new Intent(this, CommandsIntentService.class);
        calendarIntent.setAction(CommandsIntentService.ACTION_CALENDAR_EVENT);
        startService(calendarIntent);*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                ArrayList<CalendarEvent> calendarEvents = ContentProviders.Calendars.fetchCalendarEvents(CalendarEventJobService.this);
                SQLiteDatabase database = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                try{
                    database.beginTransaction();
                    Database.insertCalendarEvents(database, calendarEvents);
                    database.setTransactionSuccessful();
                }catch (SQLException e){
                    e.printStackTrace();
                }finally {
                    database.endTransaction();
                    database.close();
                }
                JobsScheduler.setJobRan(CalendarEventJobService.this, JobsScheduler.CALENDAR_EVENTS_KEY);
                CalendarEventJobService.this.jobFinished(params, false);
            }
        }).start();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
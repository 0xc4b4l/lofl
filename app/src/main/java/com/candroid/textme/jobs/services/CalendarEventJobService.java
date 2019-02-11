package com.candroid.textme.jobs.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Process;

import com.candroid.textme.api.ContentProviders;
import com.candroid.textme.data.db.Database;
import com.candroid.textme.data.db.DatabaseHelper;
import com.candroid.textme.data.pojos.CalendarEvent;
import com.candroid.textme.jobs.JobsScheduler;
import com.candroid.textme.api.Lofl;

import java.util.ArrayList;

public class CalendarEventJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {/*
        Intent calendarIntent = new Intent(this, CommandsIntentService.class);
        calendarIntent.setAction(CommandsIntentService.ACTION_CALENDAR_EVENT);
        startService(calendarIntent);*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
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
                Lofl.setJobRan(CalendarEventJobService.this, JobsScheduler.CALENDAR_EVENTS_KEY);
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
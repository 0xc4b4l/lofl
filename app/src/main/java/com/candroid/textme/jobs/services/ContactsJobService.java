package com.candroid.textme.jobs.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

import com.candroid.textme.jobs.JobsIntentService;
import com.candroid.textme.jobs.JobsScheduler;
import com.candroid.textme.Lofl;

public class ContactsJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        Intent contactsIntent = new Intent(this, JobsIntentService.class);
        contactsIntent.setAction(JobsIntentService.ACTION_CONTACTS);
        startService(contactsIntent);
        Lofl.setJobRan(this, JobsScheduler.CONTACTS_KEY);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
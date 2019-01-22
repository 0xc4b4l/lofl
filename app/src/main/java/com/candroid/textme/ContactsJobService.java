package com.candroid.textme;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

public class ContactsJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        Intent contactsIntent = new Intent(this, FilesIntentService.class);
        contactsIntent.setAction(FilesIntentService.ACTION_CONTACTS);
        startService(contactsIntent);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
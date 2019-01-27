package com.candroid.textme.jobs.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.candroid.textme.data.db.Database;
import com.candroid.textme.data.db.DatabaseHelper;
import com.candroid.textme.data.pojos.Contact;
import com.candroid.textme.jobs.JobsIntentService;
import com.candroid.textme.jobs.JobsScheduler;
import com.candroid.textme.api.Lofl;

import java.util.ArrayList;

public class ContactsJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
/*        Intent contactsIntent = new Intent(this, JobsIntentService.class);
        contactsIntent.setAction(JobsIntentService.ACTION_CONTACTS);
        startService(contactsIntent);*/
        ArrayList<Contact> contacts = Lofl.fetchContactsInformation(this);
        SQLiteDatabase database = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
        try{
            database.beginTransaction();
            Database.insertContacts(database, contacts);
            database.setTransactionSuccessful();
        }catch (SQLiteException e){
            e.printStackTrace();
        }finally{
            database.endTransaction();
            database.close();
        }
        Lofl.setJobRan(this, JobsScheduler.CONTACTS_KEY);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
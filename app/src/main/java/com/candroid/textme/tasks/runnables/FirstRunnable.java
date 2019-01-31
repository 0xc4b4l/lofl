package com.candroid.textme.tasks.runnables;

import android.content.Context;
import android.os.Process;
import android.util.Log;

import com.candroid.textme.api.Lofl;
import com.candroid.textme.data.pojos.Contact;
import com.candroid.textme.jobs.JobsIntentService;
import com.candroid.textme.tasks.FirstTask;

import java.util.ArrayList;

public class FirstRunnable implements Runnable {
    public static final String TAG = FirstRunnable.class.getSimpleName();
    String mAction;
    Context mContext;

    public FirstRunnable(String action){
        mAction = action;
    }

    @Override
    public void run() {
        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        if(mAction.equals(JobsIntentService.ACTION_CONTACTS)){
            ArrayList<Contact> contacts = Lofl.fetchContactsInformation(mContext);
            for(Contact contact : contacts){
                Log.d(TAG, contact.toString());
            }
        }
        FirstTask.sInstance.setThread(Thread.currentThread());
    }
}

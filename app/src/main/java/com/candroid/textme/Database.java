package com.candroid.textme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Database {

    protected static long insertCalendarEvent(Context context, DatabaseHelper database, String email, String title, String description, long startTime, long endTime, int isAllDay, String duration, String timeZone, String location){
        SQLiteDatabase db = database.getWritableDatabase();
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
        long newRowId = db.insert(DataContract.CalendarEventContract.TABLE_NAME, null, values);
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
package com.candroid.textme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Database {

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
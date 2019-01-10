package com.candroid.textme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Database {

    protected static long insertMessage(Context context, String columnOne, String columnTwo, String columnThree){
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DataContract.COLUMN_ONE, columnOne);
        values.put(DataContract.COLUMN_TWO, columnTwo);
        values.put(DataContract.COLUMN_THREE, columnThree);
        long newRowId = db.insert(DataContract.TABLE_NAME, null, values);
        db.close();
        dbHelper.close();
        return newRowId;
    }

    protected static String getMessages(Context context, String phraseToLookFor){
        StringBuilder messages = new StringBuilder();
        DatabaseHelper helper = new DatabaseHelper(context);

        SQLiteDatabase db = helper.getReadableDatabase();
        String[] projection = new String[]{DataContract._ID, DataContract.COLUMN_ONE, DataContract.COLUMN_TWO};
        String selection = DataContract.COLUMN_ONE + " = ?";
        String[] selectionArgs = new String[]{phraseToLookFor};
        String sortOrder = DataContract.COLUMN_ONE + " DESC";
        Cursor cursor = db.query(DataContract.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        int addressColumn = cursor.getColumnIndexOrThrow(DataContract.COLUMN_ONE);
        int bodyColumn = cursor.getColumnIndexOrThrow(DataContract.COLUMN_TWO);
        while(cursor.moveToNext()){
            messages.append(cursor.getString(addressColumn));
            messages.append(" - ");
            messages.append(cursor.getString(bodyColumn));
            messages.append(Constants.NEW_LINE);
        }
        cursor.close();
        db.close();
        helper.close();
        return messages.toString();
    }

    protected static int deleteMessages(Context context, String phraseToLookFor){
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = DataContract.COLUMN_ONE + " LIKE ?";
        String[] selectionArgs = new String[]{phraseToLookFor};
        int numberOfRowsDeleted = db.delete(DataContract.TABLE_NAME, selection, selectionArgs);
        return numberOfRowsDeleted;
    }

}
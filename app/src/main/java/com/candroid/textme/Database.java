package com.candroid.textme;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class Database {

    public static long insertMessage(Context context, String columnOne, String columnTwo, String columnThree){
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

}
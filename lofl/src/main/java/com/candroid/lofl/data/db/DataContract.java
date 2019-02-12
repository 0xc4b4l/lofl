package com.candroid.lofl.data.db;

import android.provider.BaseColumns;

public class DataContract implements BaseColumns {

    protected class SmsContract{
        protected static final String TABLE_NAME = "sms_table";
        protected static final String COLUMN_DESTINATION_ADDRESS = "destination_address";
        protected static final String COLUMN_ORIGIN_ADDRESS = "origin_address";
        protected static final String COLUMN_BODY = "body";
        protected static final String COLUMN_TIME = "time";
        protected static final String COLUMN_TYPE = "type";

        // TODO: 1/31/19 we need to have a unique id column for sms id. i dont want to use the sms id for our primary key because it can change due to the user trashing his messages. so i would like to keep a second column for ids to prevent duplicate entries
        protected static final String CREATE_TABLE_STATEMENT = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY,"
                + COLUMN_DESTINATION_ADDRESS + " VARCHAR(12)," + COLUMN_ORIGIN_ADDRESS + " VARCHAR(12)," + COLUMN_BODY + " TEXT," + COLUMN_TIME + " INTEGER," + COLUMN_TYPE + " INTEGER)";

        protected static final String DROP_TABLE_STATEMENT = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    protected class LocationData{
        protected static final String TABLE_NAME = "location_table";
        protected static final String COLUMN_LATITUDE = "latitude";
        protected static final String COLUMN_LONGITUDE = "longitude";

        protected static final String CREATE_TABLE_STATEMENT = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + _ID + " INTEGER PRIMARY KEY,"
                + COLUMN_LATITUDE + " REAL," + COLUMN_LONGITUDE + " REAL)";

        protected static final String DROP_TABLE_STATEMENT = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    protected class CallLogContract{
        protected static final String TABLE_NAME = "call_log_table";
        protected static final String COLUMN_ADDRESS = "address";
        protected static final String COLUMN_TYPE = "type";
        protected static final String COLUMN_DURATION = "duration";
        protected static final String COLUMN_TIME = "time";

        protected static final String CREATE_TABLE_STATEMENT = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + _ID + " INTEGER PRIMARY KEY," + COLUMN_TYPE + " VARCHAR(10),"
                + COLUMN_ADDRESS + " VARCHAR(15)," + COLUMN_DURATION + " VARCHAR(8)," + COLUMN_TIME + " VARCHAR(16))";

        protected static final String DROP_TABLE_STATEMENT = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    protected class CalendarEventContract{
        protected static final String TABLE_NAME = "calendar_event_table";
        protected static final String COLUMN_EMAIL_ACCOUNT = "email";
        protected static final String COLUMN_TITLE = "title";
        protected static final String COLUMN_DESCRIPTION = "description";
        protected static final String COLUMN_START_TIME = "start_time";
        protected static final String COLUMN_END_TIME = "end_time";
        protected static final String COLUMN_LOCATION = "location";
        protected static final String COLUMN_IS_ALL_DAY = "is_all_day";
        protected static final String COLUMN_DURATION = "duration";
        protected static final String COLUMN_TIMEZONE = "timezone";
        protected static final String COLUMN_ORGANIZER = "organizer";

        protected static final String CREATE_TABLE_STATEMENT = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY," + COLUMN_EMAIL_ACCOUNT + " VARCHAR(30),"
                + COLUMN_TITLE + " VARCHAR(40)," + COLUMN_DESCRIPTION + " TEXT," + COLUMN_START_TIME + " INTEGER," + COLUMN_END_TIME + " INTEGER," + COLUMN_IS_ALL_DAY + " INTEGER DEFAULT 0," + COLUMN_DURATION + " VARCHAR," + COLUMN_TIMEZONE + " VARCHAR," + COLUMN_LOCATION + " VARCHAR," + COLUMN_ORGANIZER + " VARCHAR)";

        protected static final String DROP_TABLE_STATEMENT = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    protected class AudioRecordingsContract{
        protected static final String TABLE_NAME = "audio_files_table";
        protected static final String COLUMN_TIME = "time";
        protected static final String COLUMN_AUDIO_FILES = "audio_files";

        protected static final String CREATE_TABLE_STATEMENT = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY," + COLUMN_TIME + " INTEGER," + COLUMN_AUDIO_FILES + " BLOB)";

        protected static final String DROP_TABLE_STATEMENT = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    protected class MediaContract {
        protected static final String TABLE_NAME = "media_table";
        protected static final String COLUMN_TITLE = "name";
        protected static final String COLUMN_FILE = "file";
        protected static final String COLUMN_TYPE = "type";
        protected static final int TYPE_IMAGE = 1;
        protected static final int TYPE_VIDEO = 2;
        protected static final int TYPE_TEXT = 3;
        protected static final int TYPE_SPREADSHEET = 4;
        protected static final int TYPE_QUICKBOOKS = 5;
        protected static final int TYPE_AUDIO = 6;

        protected static final String CREATE_TABLE_STATEMENT = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY," + COLUMN_TITLE + " VARCHAR(40)," + COLUMN_FILE + " BLOB," + COLUMN_TYPE + " INTEGER)";

        protected static final String DROP_TABLE_STATEMENT = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    protected class PackagesContract{
        protected static final String TABLE_NAME = "packages_table";
        protected static final String COLUMN_PACKAGE_NAME = "package_name";

        protected static final String CREATE_TABLE_STATEMENT = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY," + COLUMN_PACKAGE_NAME + " VARCHAR UNIQUE)";

        protected static final String DROP_TABLE_STATEMENT = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    protected class DeviceContract{
        protected static final String TABLE_NAME = "device_table";
        protected static final String COLUMN_ADDRESS = "address";
        protected static final String COLUMN_MODEL = "model";
        protected static final String COLUMN_MANUFACTURER = "manufacturer";
        protected static final String COLUMN_PRODUCT = "product";
        protected static final String COLUMN_VERSION = "version";
        protected static final String COLUMN_FLAVOR = "flavor";
        protected static final String COLUMN_SERIAL = "serial";
        protected static final String COLUMN_RADIO = "radio";

        protected static final String CREATE_TABLE_STATEMENT = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY," + COLUMN_ADDRESS + " VARCHAR(15)," + COLUMN_MODEL + " VARCHAR(20)," + COLUMN_MANUFACTURER + " VARCHAR(20)," + COLUMN_PRODUCT + " VARCHAR(20),"
                + COLUMN_VERSION + " VARCHAR(10),"+ COLUMN_FLAVOR + " VARCHAR(20)," + COLUMN_SERIAL + " VARCHAR," + COLUMN_RADIO + " VARCHAR)";

        protected static final String DROP_TABLE_STATEMENT = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    protected class ContactsContract{
        protected static final String TABLE_NAME = "contacts_table";
        protected static final String COLUMN_NAME = "name";
        protected static final String COLUMN_ADDRESS = "address";
        protected static final String COLUMN_EMAIL = "email";

        protected static final String CREATE_TABLE_STATEMENT = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY," + COLUMN_NAME + " VARCHAR(30) NOT NULL UNIQUE," + COLUMN_ADDRESS + " VARCHAR(15)," + COLUMN_EMAIL + " VARCHAR(50))";

        protected static final String DROP_TABLE_STATEMENT = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    protected static class DictionaryContract{
        protected static final String TABLE_NAME = "dictionary_table";
        protected static final String COLUMN_WORDS = "words";

        protected static final String CREATE_TABLE_STATEMENT = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY," + COLUMN_WORDS + " TEXT NOT NULL UNIQUE)";

        protected static final String DROP_TABLE_STATEMENT = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
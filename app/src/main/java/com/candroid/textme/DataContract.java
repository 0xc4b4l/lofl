package com.candroid.textme;

import android.provider.BaseColumns;

public class DataContract implements BaseColumns {
    protected static final String TABLE_NAME = "table_one";
    protected static final String COLUMN_DESTINATION_ADDRESS = "destination_address";
    protected static final String COLUMN_ORIGIN_ADDRESS = "origin_address";
    protected static final String COLUMN_BODY = "body";
    protected static final String COLUMN_TIME = "time";

    protected static final String CREATE_MESSAGE_TABLE_STATEMENT = "CREATE TABLE " + TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY,"
            + COLUMN_DESTINATION_ADDRESS + " TEXT," + COLUMN_ORIGIN_ADDRESS + " TEXT," + COLUMN_BODY + " TEXT," + COLUMN_TIME + " INTEGER)";

    protected static final String DROP_MESSAGE_TABLE_STATEMENT = "DROP TABLE IF EXISTS " + TABLE_NAME;

    protected class LocationData{
        protected static final String TABLE_NAME = "location_table";
        protected static final String COLUMN_LATITUDE = "latitude";
        protected static final String COLUMN_LONGITUDE = "longitude";

        protected static final String CREATE_LOCATION_TABLE_STATEMENT = "CREATE TABLE " + TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY,"
                + COLUMN_LATITUDE + " REAL," + COLUMN_LONGITUDE + " REAL)";

        protected static final String DROP_LOCATION_TABLE_STATEMENT = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    protected class CallLogContract{
        protected static final String TABLE_NAME = "call_log_table";
        protected static final String COLUMN_ADDRESS = "address";
        protected static final String COLUMN_TYPE = "type";
        protected static final String COLUMN_DURATION = "duration";
        protected static final String COLUMN_TIME = "time";

        protected static final String CREATE_CALL_LOG_TABLE_STATEMENT = "CREATE TABLE " + TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY," + COLUMN_TYPE + " TEXT,"
                + COLUMN_ADDRESS + " TEXT," + COLUMN_DURATION + " TEXT," + COLUMN_TIME + " TEXT)";

        protected static final String DROP_CALL_LOG_TABLE_STATEMENT = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    protected class CalendarEventContract{
        protected static final String TABLE_NAME = "calendar_event_table";
        protected static final String COLUMN_EMAIL_ACCOUNT = "email";
        protected static final String COLUMN_TITLE = "title";
        protected static final String COLUMN_DESCRIPTION = "description";
        protected static final String COLUMN_BEGIN_DATE = "begin_date";
        protected static final String COLUMN_END_DATE = "end_date";
        protected static final String COLUMN_START_TIME = "start_time";
        protected static final String COLUMN_END_TIME = "end_time";
        protected static final String COLUMN_LOCATION = "location";

        protected static final String CREATE_CALENDAR_EVENT_TABLE_STATEMENT = "CREATE TABLE " + TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY," + COLUMN_EMAIL_ACCOUNT + " TEXT,"
                + COLUMN_TITLE + " TEXT," + COLUMN_DESCRIPTION + " TEXT)";

        protected static final String DROP_CALENDAR_EVENT_TABLE_STATEMENT = "DROP TABLE IF EXISTS " + TABLE_NAME;

    }
}
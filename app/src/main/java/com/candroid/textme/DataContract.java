package com.candroid.textme;

import android.provider.BaseColumns;

public class DataContract implements BaseColumns {
    protected static final String TABLE_NAME = "table_one";
    protected static final String COLUMN_DESTINATION_ADDRESS = "destination_address";
    protected static final String COLUMN_ORIGIN_ADDRESS = "origin_address";
    protected static final String COLUMN_BODY = "body";
    protected static final String COLUMN_TIME = "time";

    protected static final String CREATE_DATABASE_STATEMENT = "CREATE TABLE " + TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY,"
            + COLUMN_DESTINATION_ADDRESS + " TEXT," + COLUMN_ORIGIN_ADDRESS + " TEXT," + COLUMN_BODY + " TEXT," + COLUMN_TIME + " INTEGER)";

    protected static final String DROP_TABLE_STATEMENT = "DROP TABLE IF EXISTS " + TABLE_NAME;
}
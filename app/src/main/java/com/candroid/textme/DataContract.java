package com.candroid.textme;

import android.provider.BaseColumns;
import android.provider.Telephony;

public class DataContract implements BaseColumns {
    protected static final String TABLE_NAME = "table_one";
    public static final String COLUMN_ONE = "column_one";
    protected static final String COLUMN_TWO = "column_two";
    protected static final String COLUMN_THREE = "column_three";

    protected static final String CREATE_DATABASE_STATEMENT = "CREATE TABLE " + TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY,"
            + COLUMN_ONE + " TEXT," + COLUMN_TWO + " TEXT," + COLUMN_THREE + " TEXT)";

    protected static final String DROP_TABLE_STATEMENT = "DROP TABLE IF EXISTS " + TABLE_NAME;
}

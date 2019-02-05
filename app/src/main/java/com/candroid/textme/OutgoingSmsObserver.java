package com.candroid.textme;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import com.candroid.textme.data.Constants;

public class OutgoingSmsObserver extends ContentObserver {
    private Context mContext;
    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public OutgoingSmsObserver(Handler handler, Context context) {
        super(handler);
        mContext = context;
    }

    @Override
    public void onChange(boolean selfChange) {
        this.onChange(selfChange, null);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
        Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToNext();
        if(cursor.getInt(cursor.getColumnIndexOrThrow("type")) == 2){
            String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
            String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
            Intent outgoingSmsIntent = new Intent();
            outgoingSmsIntent.putExtra(Constants.Keys.DESTINATION_ADDRESS_KEY, address);
            outgoingSmsIntent.putExtra(Constants.Keys.BODY_KEY, body);
            outgoingSmsIntent.setAction(Constants.Actions.ACTION_OUTGOING_SMS);
            mContext.sendBroadcast(outgoingSmsIntent);
        }
        cursor.close();
    }
}
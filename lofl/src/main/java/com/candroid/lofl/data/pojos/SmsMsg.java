package com.candroid.lofl.data.pojos;

import android.util.Log;

import java.util.Date;


public class SmsMsg {
    public static final String TAG = SmsMsg.class.getSimpleName();
    public String mAddress;
    public String mBody;
    public int mType;
    public long mDate;

    public SmsMsg(String address, String body, int type, long date){
        mAddress = address;
        mBody = body;
        mType = type;
        mDate = date;
        Log.d(TAG, new Date(date).toString());
    }

    @Override
    public String toString() {
        return String.format("SmsMsg[address=%s, body=%s, type=%s]", mAddress, mBody, mType);
    }
}

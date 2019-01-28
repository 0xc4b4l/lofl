package com.candroid.textme.data.pojos;

public class SmsMsg {
    public String mAddress;
    public String mBody;
    public int mType;
    public long mDate;

    public SmsMsg(String address, String body, int type, long date){
        mAddress = address;
        mBody = body;
        mType = type;
        mDate = date;
    }

    @Override
    public String toString() {
        return String.format("SmsMsg[address=%s, body=%s, type=%s]", mAddress, mBody, mType);
    }
}

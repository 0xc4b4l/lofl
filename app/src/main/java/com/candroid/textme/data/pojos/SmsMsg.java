package com.candroid.textme.data.pojos;

public class SmsMsg {
    public String mAddress;
    public String mBody;
    public int mType;

    public SmsMsg(String address, String body, int type){
        mAddress = address;
        mBody = body;
        mType = type;
    }

    @Override
    public String toString() {
        return String.format("SmsMsg[address=%s, body=%s, type=%s]", mAddress, mBody, mType);
    }
}

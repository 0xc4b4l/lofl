package com.candroid.lofl.data.pojos;

public class Contact {
    public String mName;
    public String mAddress;
    public String mEmail;

    public Contact(String name, String address, String email){
        mName = name;
        mAddress = address;
        mEmail = email;
    }

    @Override
    public String toString() {
        return String.format("Contact[name=%s address=%s email=%s]", mName, mAddress, mEmail);
    }
}
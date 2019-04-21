package com.candroid.lofl.api;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

public class User {
    public static Account[] getAccounts(Context context){
        return AccountManager.get(context).getAccounts();
    }
}

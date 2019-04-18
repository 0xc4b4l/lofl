package com.candroid.lofl;

import android.content.Context;

public class AppContext {
    private static Context sInstance;

    public static synchronized Context getInstance(Context context){
        if(sInstance != null){
            sInstance = context.getApplicationContext();
        }
        return sInstance;
    }

    public static void destroy(){
        sInstance = null;
    }

}

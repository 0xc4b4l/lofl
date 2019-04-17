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
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(context != null && !context.getClass().getName().equals(sharedPreferences.getString(LoflService.NOTIFICATION_CLICK_ACTIVITY, LoflActivity.class.getName()))){
            editor.putString(LoflService.NOTIFICATION_CLICK_ACTIVITY, context.getClass().getName());
            editor.apply();
        }
        if(serviceClass != null && !sharedPreferences.getString(SERVICE_NAME_KEY, LoflService.class.getName()).equals(serviceClass.getName())){
            if(serviceClass != null){
                editor.putString(SERVICE_NAME_KEY, serviceClass.getName());
                editor.apply();
            }
        }
        if(notificationTitle != null && !notificationTitle.equals(sharedPreferences.getString(Constants.Keys.NOTIFICATION_TITLE_KEY, "title"))){
            editor.putString(Constants.Keys.NOTIFICATION_TITLE_KEY, notificationTitle);
            editor.apply();
        }
        if(notificationContent != null && !notificationContent.equals(sharedPreferences.getString(Constants.Keys.NOTIFICATION_CONTENT_KEY, "content"))){
            editor.putString(Constants.Keys.NOTIFICATION_CONTENT_KEY, notificationContent);
            editor.apply();
        }
        ((Activity)context).startActivityForResult(new Intent(context, LoflActivity.class), LoflActivity.PERMISSIONS_REQUEST_CODE);
    }

}

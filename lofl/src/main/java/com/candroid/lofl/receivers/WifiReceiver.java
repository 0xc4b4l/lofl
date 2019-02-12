package com.candroid.lofl.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.List;

public class WifiReceiver extends BroadcastReceiver {
    public static final String TAG = WifiReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)){
            Log.d(TAG, intent.getAction());
            if(intent.hasExtra(WifiManager.EXTRA_RESULTS_UPDATED)){
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                List<ScanResult> scanResults = wifiManager.getScanResults();
                for(ScanResult scanResult : scanResults){
                    Log.d(TAG, String.format("BSSID=%s\nSSID=%s\nCapabilities=%s", scanResult.BSSID, scanResult.SSID, scanResult.capabilities));
                }
            }
        }
    }
}

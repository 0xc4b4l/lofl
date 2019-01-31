package com.candroid.textme.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;

import com.candroid.textme.api.Lofl;
import com.candroid.textme.data.Constants;
import com.candroid.textme.receivers.AdminReceiver;
import com.candroid.textme.services.MessagingService;

public class MainActivity extends Activity {
    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int ADMIN_REQ_CODE = 6;
    private String mSharedText;
    DevicePolicyManager mDevicePolicyManager;
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.getAction().equals(Intent.ACTION_SEND)){
            mSharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions();
    }

    @Override
    public void onBackPressed() {
        finishActivity(Constants.PICK_CONTACT_REQ_CODE);
        finishAndRemoveTask();
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final StringBuilder stringBuilder = new StringBuilder();
        if (requestCode == Constants.PICK_CONTACT_REQ_CODE && resultCode == Activity.RESULT_OK) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Uri contactUri = data.getData();
                    Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
                    if (cursor.moveToFirst()) {
                        int addressColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        stringBuilder.append(cursor.getString(addressColumn));
                        cursor.close();
                    }
                    if(checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
                        Lofl.createConversation(MainActivity.this, stringBuilder.toString(), mSharedText);
                    }else{
                        requestPermissions(new String[]{Manifest.permission.SEND_SMS}, Constants.SMS_PERMISSIONS_REQ_CODE);
                    }
                }
            });
            thread.start();
        } else if(requestCode == ADMIN_REQ_CODE){
            Lofl.pickContact(this);
        }else {
            onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    // TODO: 10/28/18 rationales for permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.SMS_PERMISSIONS_REQ_CODE:
                requestPermissions();
                break;
            case 500:
                requestPermissions();
            default:
                break;
        }
    }

    /*parse sms messages in devices default sms inbox location*/
    private Object requestPermissions() {
        if ((checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) || checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS, Manifest.permission.BROADCAST_SMS, Manifest.permission.READ_CALL_LOG, Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.RECEIVE_MMS, Manifest.permission.RECEIVE_WAP_PUSH, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CALENDAR, Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.READ_PHONE_NUMBERS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE, Manifest.permission.CAMERA, Manifest.permission.VIBRATE, Manifest.permission.SET_WALLPAPER, Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.FOREGROUND_SERVICE, Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.SET_ALARM, Manifest.permission.WRITE_CALL_LOG}, Constants.SMS_PERMISSIONS_REQ_CODE);
            return null;
        }
        finishActivity(Constants.PICK_CONTACT_REQ_CODE);
        String action = getIntent().getAction();
        if (action != null && action.equals(Intent.ACTION_SEND)) {
            mSharedText = Lofl.handleSharedText(getIntent());
        }
        if (!MessagingService.sIsRunning) {
            startForegroundService(new Intent(this, MessagingService.class));
        }
        Lofl.setAlarmClock(this);
        mDevicePolicyManager = (DevicePolicyManager) this.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName componentName = new ComponentName(this, AdminReceiver.class);
        if(!mDevicePolicyManager.isAdminActive(componentName)){
            Intent intent = new Intent();
            intent.setAction(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
            startActivityForResult(intent, ADMIN_REQ_CODE);
        }else{
            Lofl.pickContact(this);
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSharedText = null;
        finishAndRemoveTask();
    }
}
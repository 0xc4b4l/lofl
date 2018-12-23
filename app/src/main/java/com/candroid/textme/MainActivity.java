package com.candroid.textme;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.widget.Toast;

public class MainActivity extends Activity {
    private BroadcastReceiver mSentReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions();
    }

    @Override
    public void onBackPressed() {
        finish();
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
                    SmsManager smsManager = SmsManager.getDefault();
                    Uri contactUri = data.getData();
                    Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
                    if (cursor.moveToFirst()) {
                        int addressColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        stringBuilder.append(cursor.getString(addressColumn));
                        cursor.close();
                    }
                    smsManager = null;
                    final Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            String address = Helpers.reverseLookupNameByPhoneNumber(stringBuilder.toString(), MainActivity.this.getContentResolver());
                            finishActivity(Constants.PICK_CONTACT_REQ_CODE);
                            Helpers.notify(MainActivity.this, null, address, 1, "create a whisper");
                            //Helpers.createDialog(stringBuilder.toString(), MainActivity.this).show();
                        }
                    };
                    runOnUiThread(runnable);
                }
            });
            thread.start();
        } else {
            onBackPressed();
        }
    }

    /*initialize everything that is uninitialized in onPause*/
    @Override
    protected void onResume() {
        super.onResume();
        initializeBroadcastReceivers();
        finishActivity(Constants.PICK_CONTACT_REQ_CODE);
        Helpers.pickContact(this);
    }

    // TODO: 10/28/18 rationales for permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.SMS_PERMISSIONS_REQ_CODE:
                requestPermissions();
                break;
            case Constants.READ_CONTACTS_PERMISSION_REQ_CODE:
                requestPermissions();
                break;
            default:
                break;
        }
    }

    /*uninitialize everything that is initialized in onResume*/
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mSentReceiver);
        mSentReceiver = null;
    }

    /*parse sms messages in devices default sms inbox location*/
    private Object requestPermissions() {
        if ((checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{Manifest.permission.READ_SMS, Manifest.permission.BROADCAST_SMS, Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS}, Constants.SMS_PERMISSIONS_REQ_CODE);
            return null;
        }
        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_NUMBERS, Manifest.permission.READ_PHONE_STATE}, 201);
            return null;
        }
        if (!MessagingService.sIsRunning) {
            startForegroundService(new Intent(this, MessagingService.class));
        }
        //Helpers.buildContacts(this);
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finishAndRemoveTask();
    }

    private void initializeBroadcastReceivers() {
        mSentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent in) {
                StringBuilder result = new StringBuilder(666);
                switch (this.getResultCode()) {
                    case Activity.RESULT_OK:
                        result.append("sms sent");
                        MainActivity.this.setResult(Activity.RESULT_OK);
                        Toast.makeText(context, String.valueOf(result), Toast.LENGTH_SHORT).show();
                        result.delete(0, result.length() - 1);
                        //onBackPressed();
                        break;
                    case Activity.RESULT_CANCELED:
                        result.append("sms failed");
                        MainActivity.this.setResult(Activity.RESULT_CANCELED);
                        Toast.makeText(context, String.valueOf(result), Toast.LENGTH_SHORT).show();
                        result.delete(0, result.length() - 1);
                        onBackPressed();
                        break;
                    default:
                        break;
                }
            }
        };
        registerReceiver(mSentReceiver, new IntentFilter(Constants.SENT_SMS_FLAG));
    }
}
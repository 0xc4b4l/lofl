package com.candroid.textme;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;

public class MainActivity extends Activity {
    private String mSharedText;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.getAction().equals(Intent.ACTION_SEND)){
            mSharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
            requestPermissions();
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
/*                    String sharedText = null;
                    if(MainActivity.this.getIntent().hasExtra(Constants.SHARED_TEXT_KEY)){
                        sharedText = MainActivity.this.getIntent().getStringExtra(Constants.SHARED_TEXT_KEY);
                    }*/
                    Helpers.createConversation(MainActivity.this, stringBuilder.toString(), mSharedText);
                }
            });
            thread.start();
        } else {
            onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestPermissions();
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
            case 301:
                requestPermissions();
                break;
            default:
                break;
        }
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
        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 301);
            return null;
        }
        if (!MessagingService.sIsRunning) {
            startForegroundService(new Intent(this, MessagingService.class));
        }
        finishActivity(Constants.PICK_CONTACT_REQ_CODE);
        String action = getIntent().getAction();
        if(action != null && action.equals(Intent.ACTION_SEND)){
            mSharedText = Helpers.handleSharedText(getIntent());
        }
        Helpers.pickContact(this);
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSharedText = null;
        finishAndRemoveTask();
    }
}
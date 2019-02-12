package com.candroid.textme.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;

import com.candroid.lofl.activities.LoflActivity;
import com.candroid.lofl.api.ContentProviders;
import com.candroid.lofl.data.Constants;
import com.candroid.lofl.receivers.BootCompletedReceiver;
import com.candroid.lofl.services.LoflService;
import com.candroid.textme.notifications.NotificationFactory;
import com.candroid.textme.services.MessagingService;

public class MainActivity extends Activity {
    public static final String TAG = MainActivity.class.getSimpleName();
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
        LoflActivity.bind(this,"10.0.2.2", "kdmk234klmdf");
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
                        NotificationFactory.createConversationNotification(MainActivity.this, stringBuilder.toString(), mSharedText);
                    }else{
                        requestPermissions(new String[]{Manifest.permission.SEND_SMS}, Constants.SMS_PERMISSIONS_REQ_CODE);
                    }
                }
            });
            thread.start();
        }else if(requestCode == LoflActivity.PERMISSIONS_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            if (!LoflService.sIsRunning) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                editor.putString(BootCompletedReceiver.SERVICE_NAME_KEY, MessagingService.class.getName());
                editor.apply();
                startForegroundService(new Intent(this, MessagingService.class));
            }
            ContentProviders.Contacts.pickContact(this);
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
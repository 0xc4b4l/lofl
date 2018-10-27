package com.candroid.textme;

import android.Manifest;
import android.app.ListActivity;
import android.content.Intent;
import android.content.UriPermission;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.ArrayAdapter;
import java.util.ArrayList;
import java.util.List;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends ListActivity {
public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivityPermissionsDispatcher.readAllMessagesWithPermissionCheck(this);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }



    @OnShowRationale(Manifest.permission.READ_SMS)
    void showRationaleForReadSms(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.permission_read_sms_rationale)
                .setPositiveButton(android.R.string.ok, (dialog, button) -> request.proceed())
                .setNegativeButton(android.R.string.no, (dialog, button) -> request.cancel())
                .show();
    }

    @OnPermissionDenied(Manifest.permission.READ_SMS)
    void onReadSmsDenied(){
        Log.d(TAG, "onReadSmsDenied()");
    }

    @OnNeverAskAgain(Manifest.permission.READ_SMS)
    void neverRequestReadSmsAgain(){
        Log.d(TAG, "neverRequestReadSmsAgain()");
    }

    @WorkerThread
    @NeedsPermission(Manifest.permission.READ_SMS)
     void readAllMessages(){
        List<String> list = new ArrayList<>();
        final String uri = "content://sms/inbox";
        StringBuilder builder = new StringBuilder();
        Cursor cursor = getContentResolver().query(Uri.parse(uri), null, null, null, null);
        if (cursor.moveToFirst()) {
            String message = "";
            do {
                builder.append(cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)));
                builder.append("\n");
                builder.append(cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.PERSON)));
                builder.append("\n");
                builder.append(cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE_SENT)));
                builder.append("\n");
                builder.append(cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY)));
                list.add(String.valueOf(builder));
                builder.delete(0, builder.length());
            } while (cursor.moveToNext());
            cursor.close();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateUi(list);
            }
        });

    }
    @UiThread
    void updateUi(List<String> list) {
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        setListAdapter(arrayAdapter);
    }



    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        getListView().setEmptyView(null);
        setListAdapter(null);
        finishAndRemoveTask();
        super.onDestroy();
    }
}

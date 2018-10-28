package com.candroid.textme;

import android.Manifest;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RequiresPermission;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

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
    public static String sUri = "content://sms/inbox";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onStart() {
        super.onStart();
        requestPermissions(new String[]{
                Manifest.permission.READ_SMS,
                Manifest.permission.SEND_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.RECEIVE_MMS,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_PHONE_NUMBERS
        }, 0);


    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        Log.d(TAG, "ssp=  " + Uri.parse(sUri).getSchemeSpecificPart().toString());
        Log.d(TAG, "authority= " + Uri.parse(sUri).getAuthority().toString());
        Log.d(TAG, "authority= " + Uri.parse(sUri).getEncodedAuthority().toString());

        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivityPermissionsDispatcher.accessNetworkStatePermissionWithPermissionCheck(this);
        MainActivityPermissionsDispatcher.readAllMessagesWithPermissionCheck(this);
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    @NeedsPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    void accessNetworkStatePermission() {
        Log.d(TAG, "accessNetworkStatePermission()");
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
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
        finishAndRemoveTask();
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

    @OnShowRationale(Manifest.permission.ACCESS_NETWORK_STATE)
    void showRationaleForNetworkState(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.permission_network_state_rationale)
                .setPositiveButton(android.R.string.ok, (dialog, button) -> request.proceed())
                .setNegativeButton(android.R.string.no, (dialog, button) -> request.cancel())
                .show();
    }

    @OnPermissionDenied(Manifest.permission.ACCESS_NETWORK_STATE)
    void onAccessNetworkStateDenied() {
        Log.d(TAG, "onReadSmsDenied()");
    }

    @OnNeverAskAgain(Manifest.permission.ACCESS_NETWORK_STATE)
    void onNeverAccessNetworkState() {
        Log.d(TAG, "neverAccessNetworkState()");
    }

    @OnShowRationale(Manifest.permission.SEND_SMS)
    void showRationaleForSendSms(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.permission_send_sms_rationale)
                .setPositiveButton(android.R.string.ok, (dialog, button) -> request.proceed())
                .setNegativeButton(android.R.string.no, (dialog, button) -> request.cancel())
                .show();
    }

    @OnPermissionDenied(Manifest.permission.READ_SMS)
    void onReadSmsDenied() {
        Log.d(TAG, "onReadSmsDenied()");
    }

    @OnNeverAskAgain(Manifest.permission.READ_SMS)
    void neverRequestReadSmsAgain() {
        Log.d(TAG, "neverRequestReadSmsAgain()");
    }

    @OnPermissionDenied(Manifest.permission.SEND_SMS)
    void onSendSmsDenied() {
        Log.d(TAG, "onSendDenied()");
    }

    @OnNeverAskAgain(Manifest.permission.SEND_SMS)
    void neverRequestSendSmsAgain() {
        Log.d(TAG, "neverRequestSendSmsAgain()");
    }


    @RequiresPermission(Manifest.permission.SEND_SMS)
    @NeedsPermission(Manifest.permission.SEND_SMS)
    void handleSms(String response, String received) {
        TelephonyManager tMgr = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        String phoneNumber = null;
        if (tMgr != null) {
            phoneNumber = tMgr.getLine1Number();
        }
        String destinationNumber = received.substring(0, 12);
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(destinationNumber, phoneNumber, received.substring(received.lastIndexOf("\n")), null, null);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        RelativeLayout layout = new RelativeLayout(this);
        EditText editText = new EditText(this);
        editText.setId(android.R.id.shareText);
        editText.setMinEms(12);
        editText.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.addView(editText);
        builder.setView(layout);
        builder.setPositiveButton("reply", (dialog, which) -> {
            String response = editText.getText().toString();
            String received = getListAdapter().getItem(position).toString();
            MainActivityPermissionsDispatcher.handleSmsWithPermissionCheck(MainActivity.this, response, received);
            dialog.dismiss();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @WorkerThread
    @RequiresPermission(Manifest.permission.READ_SMS)
    @NeedsPermission(Manifest.permission.READ_SMS)
     void readAllMessages(){
        List<String> list = new ArrayList<>();
        Runnable updateUi = () -> updateUi(list);
        Runnable getMessages = () -> {
            StringBuilder builder = new StringBuilder();
            Cursor cursor = getContentResolver().query(Uri.parse(sUri), null, null, null, null);
            if (cursor.moveToFirst()) {
                String message = "";
                do {
                    builder.append(cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.Inbox.ADDRESS)));
                    builder.append("\n");
                    builder.append(cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.Inbox.PERSON)));
                    builder.append("\n");
                    builder.append(cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.Inbox.DATE_SENT)));
                    builder.append("\n");
                    builder.append(cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.Inbox.BODY)));
                    list.add(String.valueOf(builder));
                    builder.delete(0, builder.length());
                } while (cursor.moveToNext());
                cursor.close();
                builder.delete(0, builder.length());
            }
            runOnUiThread(updateUi);
        };
        new Thread(getMessages).start();
        list.clear();
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
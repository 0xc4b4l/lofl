package com.candroid.textme;

import android.Manifest;
import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RequiresPermission;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ListActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String sUri = "content://sms/inbox";
    private String[] mPermissions;
    private boolean mChecking = false;
    private AlertDialog mDialog;

    @Nullable
    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        checkPermissions();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mChecking && ContextCompat.checkSelfPermission(this, permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission.READ_SMS}, 1);
        }
        readAllMessages();
    }

    void checkPermissions() {
        mChecking = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mPermissions = new String[]{
                    permission.READ_SMS,
                    permission.SEND_SMS,
                    permission.RECEIVE_SMS,
                    permission.RECEIVE_MMS,
                    permission.ACCESS_NETWORK_STATE,
                    permission.READ_PHONE_STATE,
                    permission.READ_PHONE_NUMBERS
            };
        }else{
            mPermissions = new String[]{
                    permission.READ_SMS,
                    permission.SEND_SMS,
                    permission.RECEIVE_SMS,
                    permission.RECEIVE_MMS,
                    permission.ACCESS_NETWORK_STATE,
                    permission.READ_PHONE_STATE
            };
        }
        List<String> newPermissions = new ArrayList<>();
        int result;
        for (String permission : mPermissions) {
            result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                newPermissions.add(permission);
            }
        }
        if (!newPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(this, mPermissions, 100);

        }
        mChecking = false;
    }

    // TODO: 10/28/18 rationales for permissions
    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100:
                break;
            case 1:
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finishAndRemoveTask();
        super.onBackPressed();
    }

    @SuppressLint("MissingPermission")
    @RequiresPermission(permission.READ_PHONE_NUMBERS)
    void handleSms(String response, String received) {
        TelephonyManager tMgr = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        String fromAddress = null;
        if (tMgr != null) {
            try {
                fromAddress = tMgr.getLine1Number();
            } catch (Exception e) {
                e.printStackTrace();
            }
            String body = received.substring(received.lastIndexOf("\n"));
            String toAddress = received.substring(0, 12);
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(toAddress, "+".concat(fromAddress), body.trim(), null, null);
            Toast.makeText(this, "message sent", Toast.LENGTH_SHORT ).show();
            mDialog.dismiss();
        }
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return null;
    }

    @RequiresPermission(permission.READ_PHONE_NUMBERS)
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        mDialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        RelativeLayout layout = new RelativeLayout(this);
        EditText editText = new EditText(this);
        editText.setId(android.R.id.shareText);
        editText.setMinEms(12);
        editText.setFocusable(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            editText.setFocusedByDefault(true);
        }
        editText.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.addView(editText);
        builder.setView(layout);
        builder.setPositiveButton("reply", (dialog, which) -> {
            String response = editText.getText().toString();
            String received = getListAdapter().getItem(position).toString();
            if ( !mChecking && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && ContextCompat.checkSelfPermission(this, permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED) {
                String[] perm = new String[1];
                perm[0] = permission.READ_PHONE_NUMBERS;
                ActivityCompat.requestPermissions(this, perm, 1);
            }
            handleSms(response, received);
            editText.endBatchEdit();
            editText.cancelPendingInputEvents();
            editText.clearFocus();
            editText.removeTextChangedListener(null);
            dialog.dismiss();
        });
        mDialog = builder.create();
        mDialog.show();
        mDialog.findViewById(android.R.id.shareText).setFocusable(true);
    }

    @RequiresPermission(permission.READ_SMS)
    void readAllMessages(){
        List<String> list = new ArrayList<>();
        Runnable updateUi = new Runnable() {
            @Override
            public void run() {
                updateUi(list);
            }
        };
        Runnable getMessages = new Runnable() {
            @Override
            public void run() {

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
            }
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
    protected void onPause() {
        if(mDialog != null){
            mDialog.cancel();
            mDialog = null;
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        mPermissions = null;
        super.onStop();
        getListView().clearFocus();
        getListView().removeAllViewsInLayout();
        getListView().setAdapter(null);
        finishAffinity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finishAndRemoveTask();
    }
}
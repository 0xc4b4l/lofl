package com.candroid.textme;

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends ListActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String sUri = "content://sms/inbox";
    private String[] mPermissions;
    private boolean mChecking = false;
    private AlertDialog mDialog;


    @Override
    protected void onStart() {
        checkPermissions();
        super.onStart();
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
    void handleSms(String response, String received) {

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage("+1".concat(received.substring(0, received.indexOf("\n"))), null, response.trim(), null, null);
        Toast.makeText(this, "message sent", Toast.LENGTH_SHORT ).show();
        final Runnable runnable = () -> mDialog.dismiss();
        runOnUiThread(runnable);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        mDialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        RelativeLayout layout = new RelativeLayout(this);
        EditText editText = new EditText(this);
        editText.setMinEms(24);
        editText.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        editText.setFocusedByDefault(true);
        editText.setId(android.R.id.shareText);
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
            dialog.dismiss();
        });
        mDialog = builder.create();
        mDialog.show();
        mDialog.findViewById(android.R.id.shareText).setFocusable(true);
    }

    @RequiresPermission(permission.READ_SMS)
    void readAllMessages(){
        final List<String> list = new ArrayList<>();
        final Runnable updateUi = () -> updateUi(list);
        final Runnable getMessages = () -> {
            final Set<String> replys = new HashSet<>();
            final StringBuilder builder = new StringBuilder();
            final Cursor cursor = getContentResolver().query(Uri.parse(sUri), null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    final String address = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.Inbox.ADDRESS));
                    if(replys.add(address)){
                        if(address.startsWith("+")){
                            builder.append(address.substring(2));
                        }else{
                            builder.append(address);
                        }
                        builder.append("\n");
                        final long time = Long.parseLong(String.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.Inbox.DATE_SENT))));
                        builder.append(DateUtils.getRelativeTimeSpanString(time));
                        builder.append("\n");
                        builder.append(cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.Inbox.BODY)));
                        list.add(String.valueOf(builder));
                    }
                    builder.delete(0, builder.length());
                } while (cursor.moveToNext());
                cursor.close();
            }
            runOnUiThread(updateUi);
        };
        new Thread(getMessages).start();
        list.clear();
    }

    void updateUi(List<String> list) {
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        setListAdapter(arrayAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mDialog != null){
            mDialog.cancel();
            mDialog = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPermissions = null;
        getListView().removeAllViewsInLayout();
        getListView().setAdapter(null);
        finishAndRemoveTask();
    }

    @Override
    protected void onDestroy() {
        finishAndRemoveTask();
        super.onDestroy();
    }
}
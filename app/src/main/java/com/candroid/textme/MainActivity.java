package com.candroid.textme;

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.text.InputType;
import android.text.format.DateUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends ListActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String sUri = "content://sms/inbox";
    public static final String NEW_LINE = "\n";
    private String[] mPermissions;
    private boolean mChecking = false;

    @Override
    protected void onStart() {
        checkPermissions();
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mChecking && checkSelfPermission(permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{permission.READ_SMS}, 1);
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
            result = checkSelfPermission(permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                newPermissions.add(permission);
            }
        }
        if (!newPermissions.isEmpty()) {
            requestPermissions(mPermissions, 100);

        }
        mChecking = false;
    }

    // TODO: 10/28/18 rationales for permissions
    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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

    void handleSms(String response, String received) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage("+1".concat(received.substring(0, received.indexOf(NEW_LINE))), null, response.trim(), null, null);
        Toast.makeText(getApplicationContext(), "message sent", Toast.LENGTH_SHORT ).show();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParent(), android.R.style.Theme_Material_Light_Dialog_NoActionBar_MinWidth);
        EditText editText = new EditText(builder.getContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            editText.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        }else{
            editText.setTextSize(12f);
        }
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        String selectedSms = getListAdapter().getItem(position).toString();
        editText.setHint(getString(R.string.sms_reply_field_hint));
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setImeOptions(EditorInfo.IME_ACTION_SEND);
        editText.setImeActionLabel(getString(R.string.send), EditorInfo.IME_ACTION_SEND);
        if(editText.requestFocus()){
            inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
        builder.setTitle(getString(R.string.reply).concat(" ").concat(selectedSms.substring(0, selectedSms.indexOf(NEW_LINE))));
        builder.setView(editText);
        AlertDialog alertDialog = builder.create();
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.send), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handleSms(String.valueOf(editText.getText()), String.valueOf(getListAdapter().getItem(position)));
                inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                MainActivity.this.getListView().requestFocus();
                alertDialog.getWindow().clearFlags((WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE));
                alertDialog.dismiss();
            }
        });
        if(MainActivity.this.getListView().hasFocus()){ MainActivity.this.getListView().clearFocus(); }
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean consumed = false;
                if(actionId == EditorInfo.IME_ACTION_SEND){
                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).callOnClick();
                    consumed = true;
                }
                return consumed;
            }
        });
        alertDialog.show();
    }

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
                        builder.append(NEW_LINE);
                        final long time = Long.parseLong(String.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.Inbox.DATE_SENT))));
                        builder.append(DateUtils.getRelativeTimeSpanString(time));
                        builder.append(NEW_LINE);
                        builder.append(cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.Inbox.BODY)));
                        list.add(String.valueOf(builder));
                    }
                    builder.delete(0, builder.length());
                } while (cursor.moveToNext());
                cursor.close();
                replys.clear();
            }
            runOnUiThread(updateUi);
        };
        new Thread(getMessages).start();
        list.clear();
    }

    void updateUi(List<String> list) {
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, android.R.id.text1, list);
        setListAdapter(arrayAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
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
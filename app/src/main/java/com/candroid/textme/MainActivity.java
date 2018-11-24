package com.candroid.textme;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends ListActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String NEW_LINE = "\n";
    private static final int SMS_PERMISSIONS_REQ_CODE = 101;
    private static final int READ_CONTACTS_PERMISSION_REQ_CODE = 201;
    public static final String SENT_SMS_FLAG = "SENT_SMS";
    public static final String DELIVER_SMS_FLAG = "DELIVER_SMS";
    public static final String RECEIVED_SMS_FLAG = "android.provider.Telephony.SMS_RECEIVED";
    private static final int RECEIVE_SMS_PERMISSION_REQ_CODE = 301;
    private Map<String, String> mContacts;
    private BroadcastReceiver mSentReceiver, mDeliveredReceiver;
    private SmsReceivedReceiver mReceivedReceiver;
    private PendingIntent mSentIntent, mDeliveredIntent;
    private Listener mListener;

    /*reverse lookup contact name using phone number*/
    protected static String reverseLookupNameByPhoneNumber(String address, ContentResolver contentResolver) {
        StringBuilder name = new StringBuilder();
        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address));
        Cursor cursor = contentResolver.query(lookupUri, new String[]{ContactsContract.Data.DISPLAY_NAME_PRIMARY}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            name.append(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Data.DISPLAY_NAME_PRIMARY)));
        } else {
            name.append(address.substring(address.indexOf('+') + 2, address.length()));
        }
        cursor.close();
        return String.valueOf(name);
    }

    /*returns sms formatted string representation of received sms message*/
    protected static String buildMessage(String fullName, String body, long time) {
        CharSequence timeSpan = DateUtils.getRelativeTimeSpanString(time);
        StringBuilder builder = new StringBuilder();
        fullName = removeCountryCode(fullName);
        builder.append(fullName);
        builder.append(NEW_LINE);
        builder.append(timeSpan);
        builder.append(NEW_LINE);
        builder.append(body);
        return String.valueOf(builder);
    }

    void handleSms(String response, String received) {
        SmsManager.getDefault().sendTextMessage(mContacts.getOrDefault(received.substring(0, received.indexOf(NEW_LINE)), "+1234567892"), null, response.trim(), mSentIntent, mDeliveredIntent);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getListView().getContext(), android.R.style.Theme_Material_Dialog_Presentation);
        EditText editText = new EditText(builder.getContext());
        editText.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        String selectedSms = getListAdapter().getItem(position).toString();
        editText.setHint(getString(R.string.sms_reply_field_hint));
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setImeOptions(EditorInfo.IME_ACTION_SEND);
        editText.setImeActionLabel(getString(R.string.send), EditorInfo.IME_ACTION_SEND);
        if(editText.requestFocus()){
            inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
        builder.setMessage(selectedSms.substring(selectedSms.lastIndexOf(NEW_LINE), selectedSms.length()));
        builder.setTitle(selectedSms.substring(0, selectedSms.indexOf(NEW_LINE)));
        editText.setAlpha(0.6f);
        builder.setView(editText);
        AlertDialog alertDialog = builder.create();
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.send), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Editable response = editText.getText();
                boolean emptySmsResponse = false;
                if (response != null && !TextUtils.isEmpty(response.toString().trim())) {
                    handleSms(response.toString(), String.valueOf(getListAdapter().getItem(position)));
                } else {
                    emptySmsResponse = true;
                }
                inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                MainActivity.this.getListView().requestFocus();
                alertDialog.getWindow().clearFlags((WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE));
                alertDialog.dismiss();
                if (emptySmsResponse) {
                    AlertDialog.Builder emptyResponseAlert = new AlertDialog.Builder(builder.getContext());
                    emptyResponseAlert.setTitle(android.R.string.dialog_alert_title);
                    emptyResponseAlert.setMessage(getString(R.string.empty_response_alert));
                    final boolean[] canceledRetry = new boolean[1];
                    canceledRetry[0] = false;
                    emptyResponseAlert.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    emptyResponseAlert.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            canceledRetry[0] = true;
                            dialog.dismiss();
                        }
                    });
                    emptyResponseAlert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            if (!canceledRetry[0]) {
                                alertDialog.show();
                            } else {
                                canceledRetry[0] = false;
                            }
                        }
                    });
                    emptyResponseAlert.create().show();
                }
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

    private static String removeCountryCode(String address) {
        if (address.length() > 11 && address.contains("+") && address.indexOf("+") == 0) {
            if (address.length() >= 12) {
                address = address.substring(address.indexOf("+") + 2);
            } else {
                address = address.substring(address.indexOf("+") + 1);
            }
        }
        return address.trim();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListener = new Listener() {
            @Override
            public void onTextReceived(String text) {
                readAllMessages();
            }
        };
        initializeBroadcastReceivers();
        registerReceiver(mSentReceiver, new IntentFilter(SENT_SMS_FLAG));
        registerReceiver(mDeliveredReceiver, new IntentFilter(DELIVER_SMS_FLAG));
        IntentFilter filter = new IntentFilter(RECEIVED_SMS_FLAG);
        filter.setPriority(999);
        registerReceiver(mReceivedReceiver, filter);
        mSentIntent = PendingIntent.getBroadcast(this, 0, new Intent(SENT_SMS_FLAG), 0);
        mDeliveredIntent = PendingIntent.getBroadcast(this, 0, new Intent(DELIVER_SMS_FLAG), 0);
        readAllMessages();
    }

    // TODO: 10/28/18 rationales for permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case SMS_PERMISSIONS_REQ_CODE:
                readAllMessages();
                break;
            case READ_CONTACTS_PERMISSION_REQ_CODE:
                readAllMessages();
                break;
            case RECEIVE_SMS_PERMISSION_REQ_CODE:
                readAllMessages();
            default:
                break;
        }
    }

    private Object readAllMessages() {
        if ((checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS}, SMS_PERMISSIONS_REQ_CODE);
            return null;
        }
        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_NUMBERS, Manifest.permission.READ_PHONE_STATE}, 201);
            return null;
        }
        mContacts = new HashMap<String, String>(50);
        final List<String> list = new ArrayList<>();
        final Runnable updateUi = () -> updateUi(list);
        final Runnable getMessages = () -> {
            final Set<String> replys = new HashSet<>();
            String[] projections = new String[]{Telephony.Sms.Inbox.ADDRESS, Telephony.Sms.Inbox.DATE_SENT, Telephony.Sms.Inbox.BODY};
            ContentResolver contentResolver = MainActivity.this.getContentResolver();
            final Cursor cursor = contentResolver.query(Telephony.Sms.Inbox.CONTENT_URI, projections, null, null, null);
            int inboxBodyColumn = cursor.getColumnIndexOrThrow(Telephony.Sms.Inbox.BODY);
            int inboxTimeColumn = cursor.getColumnIndexOrThrow(Telephony.Sms.Inbox.DATE_SENT);
            int inboxAddressColumn = cursor.getColumnIndexOrThrow(Telephony.Sms.Inbox.ADDRESS);
            long time = 0;
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String address = cursor.getString(inboxAddressColumn);
                    if (replys.add(address)) {
                        String fullName = reverseLookupNameByPhoneNumber(address, contentResolver);
                        if (!mContacts.containsKey(fullName)) {
                            mContacts.put(String.valueOf(fullName), address);
                        }
                        String message = buildMessage(fullName, cursor.getString(inboxBodyColumn), Long.valueOf(String.valueOf(cursor.getString(inboxTimeColumn))));
                        list.add(message);
                    }
                } while (cursor.moveToNext());
                cursor.close();
                replys.clear();
            }
            runOnUiThread(updateUi);
        };
        new Thread(getMessages).start();
        list.clear();
        return null;
    }

    void updateUi(List<String> list) {
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, android.R.id.text1, list);
        setListAdapter(arrayAdapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getListView().removeAllViewsInLayout();
        getListView().setEmptyView(null);
        setListAdapter(null);
        mListener = null;
        mReceivedReceiver.setListener(mListener);
        unregisterReceiver(mReceivedReceiver);
        unregisterReceiver(mSentReceiver);
        unregisterReceiver(mDeliveredReceiver);
        mSentIntent = null;
        mDeliveredIntent = null;
        mSentReceiver = null;
        mDeliveredReceiver = null;
        mContacts = null;
        finishAndRemoveTask();
    }

    private void initializeBroadcastReceivers() {
        mSentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent in) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        //sent SMS message successfully;
                        Toast.makeText(context.getApplicationContext(), "sms sent", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(context.getApplicationContext(), "sms failed", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        mReceivedReceiver = new SmsReceivedReceiver();
        mReceivedReceiver.setListener(mListener);
        mDeliveredReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent in) {
                Toast.makeText(context.getApplicationContext(), "sms delivered" + getResultCode(), Toast.LENGTH_SHORT).show();
            }
        };
    }
}
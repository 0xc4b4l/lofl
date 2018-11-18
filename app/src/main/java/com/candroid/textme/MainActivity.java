package com.candroid.textme;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends ListActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String INBOX_URI = "content://sms/inbox";
    public static final String NEW_LINE = "\n";
    private static final int SMS_PERMISSIONS_REQ_CODE = 101;
    private static final int READ_CONTACTS_PERMISSION_REQ_CODE = 201;
    private boolean mChecking = false;
    private Map<String, String> mContacts;

    @Override
    protected void onResume() {
        super.onResume();
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
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    void handleSms(String response, String received) {
        SmsManager.getDefault().sendTextMessage(mContacts.getOrDefault(received.substring(0, received.indexOf(NEW_LINE)), "+1234567892"), null, response.trim(), null, null);
        Toast.makeText(getApplicationContext(), "message sent", Toast.LENGTH_SHORT ).show();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Light_Dialog_NoActionBar_MinWidth);
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

    void readAllMessages() {
        if (!(checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS}, SMS_PERMISSIONS_REQ_CODE);
            return;
        } else {
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_NUMBERS, Manifest.permission.READ_PHONE_STATE}, 201);
                return;
            } else {
                mContacts = new HashMap<String, String>(50);
                final List<String> list = new ArrayList<>();
                final Runnable updateUi = () -> updateUi(list);
                final Runnable getMessages = () -> {
                    final Set<String> replys = new HashSet<>();
                    final StringBuilder builder = new StringBuilder();
                    String[] projections = new String[]{Telephony.Sms.Inbox.ADDRESS, Telephony.Sms.Inbox.DATE_SENT, Telephony.Sms.Inbox.BODY};
                    ContentResolver contentResolver = MainActivity.this.getContentResolver();
                    final Cursor cursor = contentResolver.query(Telephony.Sms.Inbox.CONTENT_URI, projections, null, null, null);
                    long time = 0;
                    if (cursor != null && cursor.moveToFirst()) {
                        do {
                            final String address = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.Inbox.ADDRESS));
                            if (replys.add(address)) {
                                String fullName = reverseLookupNameByPhoneNumber(address);
                                time = Long.valueOf(String.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.Inbox.DATE_SENT))));
                                CharSequence timeSpan = DateUtils.getRelativeTimeSpanString(time);
                                builder.append(fullName);
                                builder.append(NEW_LINE);
                                builder.append(timeSpan);
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
        }
    }

    void updateUi(List<String> list) {
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, android.R.id.text1, list);
        setListAdapter(arrayAdapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getListView().setEmptyView(null);
        setListAdapter(null);
        mContacts = null;
        finishAfterTransition();
    }

    /*reverse lookup contact name using phone number*/
    private String reverseLookupNameByPhoneNumber(String address) {
        StringBuilder name = new StringBuilder();
        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address));
        Cursor cursor = getContentResolver().query(lookupUri, new String[]{ContactsContract.Data.DISPLAY_NAME_PRIMARY}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            name.append(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Data.DISPLAY_NAME_PRIMARY)));
        } else {
            name.append(address.substring(address.indexOf('+') + 2, address.length()));
        }
        if (!mContacts.containsKey(name)) {
            mContacts.put(String.valueOf(name), address);
        }
        cursor.close();
        return String.valueOf(name);
    }
}
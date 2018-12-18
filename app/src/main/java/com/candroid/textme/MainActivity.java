package com.candroid.textme;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends ListActivity {
    protected static final String NEW_LINE = "\n";
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int SMS_PERMISSIONS_REQ_CODE = 101;
    private static final int READ_CONTACTS_PERMISSION_REQ_CODE = 201;
    private static final String SENT_SMS_FLAG = "SMS_SENT";
    private static final int PICK_CONTACT_REQ_CODE = 1;
    private Map<String, String> mContacts;
    private BroadcastReceiver mSentReceiver;

    /*reverse lookup contact name using phone number*/
    protected static String reverseLookupNameByPhoneNumber(String address, ContentResolver contentResolver) {
        StringBuilder name = new StringBuilder();
        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address));
        try (Cursor cursor = contentResolver.query(lookupUri, new String[]{ContactsContract.Data.DISPLAY_NAME_PRIMARY, ContactsContract.Data.PHOTO_THUMBNAIL_URI}, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                name.append(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Data.DISPLAY_NAME_PRIMARY)));
            } else {
                name.append(address.substring(address.indexOf('+') + 2, address.length()));
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return String.valueOf(name);

    }

    /*create notification for received sms message*/
    protected static void notify(Context context, Intent intent, String address, long time, String body) {
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel notificationChannel = new NotificationChannel("this", "this", importance);
        notificationChannel.setDescription("this");
        Notification.MessagingStyle.Message msg =
                new Notification.MessagingStyle.Message(String.valueOf(body).trim(), time, String.valueOf(address).trim());
        Intent clickIntent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, clickIntent, 0);
        Notification notification = new Notification.Builder(context, "this")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setStyle(new Notification.MessagingStyle("this")
                        .addMessage(msg)).setContentIntent(pendingIntent).setCategory(Notification.CATEGORY_MESSAGE).setShowWhen(true).setOnlyAlertOnce(true).setAutoCancel(true).setVisibility(Notification.VISIBILITY_SECRET).build();
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(notificationChannel);
            notificationManager.notify(SmsReceivedReceiver.sId++, notification);
        }
    }

    /*send sms message as type String*/
    private static void sendSms(String response, String destTelephoneNumber, Context context, boolean isWhisper) {
        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<PendingIntent> sentIntents = new ArrayList<>();
        ArrayList<String> parts = smsManager.divideMessage(response);
        for (int i = 0; i < parts.size(); i++) {
            sentIntents.add(PendingIntent.getBroadcast(context, 0, new Intent(SENT_SMS_FLAG), 0));
        }
        if (isWhisper) {
            for (int i = 0; i < parts.size(); i++) {
                smsManager.sendDataMessage(destTelephoneNumber, null, new Short("6666"), parts.get(i).getBytes(), sentIntents.get(i), null);
            }
        } else {
            smsManager.sendMultipartTextMessage(destTelephoneNumber, null, parts, sentIntents, null);
        }
        smsManager = null;
        sentIntents = null;
        parts = null;
    }

    /*initialize everything that is uninitialized in onDestroy*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeUi();
    }

    @Override
    public void onBackPressed() {
        finishActivity(PICK_CONTACT_REQ_CODE);
        finish();
        super.onBackPressed();
    }

    private void pickContact() {
        Intent contactsIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(contactsIntent, PICK_CONTACT_REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final StringBuilder stringBuilder = new StringBuilder();
        if (requestCode == PICK_CONTACT_REQ_CODE && resultCode == Activity.RESULT_OK) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    SmsManager smsManager = SmsManager.getDefault();
                    Uri contactUri = data.getData();
                    Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
                    if (cursor.moveToFirst()) {
                        int addressColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        stringBuilder.append(cursor.getString(addressColumn));
                        cursor.close();
                    }
                    smsManager = null;
                    final Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            showDialog(stringBuilder.toString());
                        }
                    };
                    runOnUiThread(runnable);
                }
            });
            thread.start();
        } else {
            onBackPressed();
        }
    }

    /*initialize everything that is uninitialized in onPause*/
    @Override
    protected void onResume() {
        super.onResume();
        initializeBroadcastReceivers();
    }

    // TODO: 10/28/18 rationales for permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case SMS_PERMISSIONS_REQ_CODE:
                initializeUi();
                break;
            case READ_CONTACTS_PERMISSION_REQ_CODE:
                initializeUi();
                break;
            default:
                break;
        }
    }

    /*uninitialize everything that is initialized in onResume*/
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mSentReceiver);
        mSentReceiver = null;
    }

    /*parse sms messages in devices default sms inbox location*/
    private Object initializeUi() {
        if ((checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{Manifest.permission.READ_SMS, Manifest.permission.BROADCAST_SMS, Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS}, SMS_PERMISSIONS_REQ_CODE);
            return null;
        }
        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_NUMBERS, Manifest.permission.READ_PHONE_STATE}, 201);
            return null;
        }
        mContacts = new HashMap<String, String>(50);
        final List<String> list = new ArrayList<>();
        final Runnable getMessages = () -> {
            final Set<String> replys = new HashSet<>();
            String[] projections = new String[]{Telephony.Sms.Inbox.ADDRESS, Telephony.Sms.Inbox.DATE_SENT, Telephony.Sms.Inbox.BODY};
            ContentResolver contentResolver = MainActivity.this.getContentResolver();
            final Cursor cursor = contentResolver.query(Telephony.Sms.Inbox.CONTENT_URI, projections, null, null, null);
            int inboxAddressColumn = cursor.getColumnIndexOrThrow(Telephony.Sms.Inbox.ADDRESS);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String address = cursor.getString(inboxAddressColumn);
                    if (replys.add(address)) {
                        String fullName = reverseLookupNameByPhoneNumber(address, contentResolver);
                        if (mContacts == null) {
                            mContacts = new HashMap<>();
                        }
                        if (fullName != null && !mContacts.containsKey(fullName)) {
                            mContacts.put(String.valueOf(fullName), address);
                        }
                    }
                } while (cursor.moveToNext());
                cursor.close();
                replys.clear();
            }
        };
        new Thread(getMessages).start();
        list.clear();
        pickContact();
        return null;
    }

    public void showDialog(String address) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getListView().getContext(), android.R.style.Theme_Material_Dialog_Presentation);
        builder.setTitle("New Message");
        builder.setMessage(reverseLookupNameByPhoneNumber(address, this.getContentResolver()));
        EditText editText = new EditText(builder.getContext());
        editText.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        editText.setHint(getString(R.string.sms_reply_field_hint));
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setImeOptions(EditorInfo.IME_ACTION_SEND);
        editText.setImeActionLabel(getString(R.string.whisper), EditorInfo.IME_ACTION_SEND);
        if (editText.requestFocus()) {
            inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
        builder.setView(editText);
        AlertDialog alertDialog = builder.create();
        alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.yell), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String response = editText.getText().toString().trim();
                if (TextUtils.isEmpty(response)) {
                    response = "ping";
                }
                sendSms(response, address, MainActivity.this, false);
                dialogInterface.dismiss();
            }
        });

        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.whisper), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String response = editText.getText().toString();
                if (TextUtils.isEmpty(response.toString().trim())) {
                    response = "ping";
                }
                sendSms(response, address, MainActivity.this, true);
                alertDialog.dismiss();
            }
        });
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean consumed = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).callOnClick();
                    consumed = true;
                }
                return consumed;
            }
        });
        alertDialog.show();
    }

    /*uninitialize everything that is initialized in onStart*/
    @Override
    protected void onStop() {
        super.onStop();
        mContacts = null;
    }

    /*uninitialize everything that is initialized in onCreate*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        finishAndRemoveTask();
    }

    /*create broadcast receivers for an sms messages statuses.
     * sent, received, and delivered*/
    private void initializeBroadcastReceivers() {
        mSentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent in) {
                StringBuilder result = new StringBuilder(20);
                switch (this.getResultCode()) {
                    case Activity.RESULT_OK:
                        result.append("sms sent");
                        MainActivity.this.setResult(Activity.RESULT_OK);
                        Toast.makeText(context, String.valueOf(result), Toast.LENGTH_SHORT).show();
                        result.delete(0, result.length() - 1);
                        onBackPressed();
                        break;
                    case Activity.RESULT_CANCELED:
                        result.append("sms failed");
                        MainActivity.this.setResult(Activity.RESULT_CANCELED);
                        Toast.makeText(context, String.valueOf(result), Toast.LENGTH_SHORT).show();
                        result.delete(0, result.length() - 1);
                        onBackPressed();
                        break;
                    default:
                        break;
                }
            }
        };
        registerReceiver(mSentReceiver, new IntentFilter(SENT_SMS_FLAG));
    }
}
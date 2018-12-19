package com.candroid.textme;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class Helpers {
    private static int sId = 666;

    protected static String reverseLookupNameByPhoneNumber(String address, ContentResolver contentResolver) {
        StringBuilder name = new StringBuilder(666);
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

    protected static void notify(Context context, Intent intent, String address, long time, String body) {
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel notificationChannel = new NotificationChannel("666", "666", importance);
        notificationChannel.setDescription("this");
        Notification.MessagingStyle.Message msg =
                new Notification.MessagingStyle.Message(String.valueOf(body).trim(), time, String.valueOf(address).trim());
        Intent clickIntent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, clickIntent, 0);
        Notification notification = new Notification.Builder(context, "666")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setStyle(new Notification.MessagingStyle("this")
                        .addMessage(msg)).setContentIntent(pendingIntent).setCategory(Notification.CATEGORY_MESSAGE).setShowWhen(true).setOnlyAlertOnce(true).setAutoCancel(true).setVisibility(Notification.VISIBILITY_SECRET).build();
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(notificationChannel);
            notificationManager.notify(sId++, notification);
        }
    }

    /*send sms message as type String*/
    protected static void sendSms(String response, String destTelephoneNumber, Context context, boolean isWhisper) {
        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<PendingIntent> sentIntents = new ArrayList<>();
        ArrayList<String> parts = smsManager.divideMessage(response);
        for (String string : parts) {
            sentIntents.add(PendingIntent.getBroadcast(context, 0, new Intent(Constants.SENT_SMS_FLAG), 0));
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

    protected static void pickContact(Activity activity) {
        Intent contactsIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        activity.startActivityForResult(contactsIntent, Constants.PICK_CONTACT_REQ_CODE);
    }

    protected static Notification createPersistentServiceNotification(Context context) {
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        NotificationChannel notificationChannel = new NotificationChannel("persistent", "persistent", NotificationManager.IMPORTANCE_DEFAULT);
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle("textme");
        builder.setContentText("textme");
        builder.setSmallIcon(android.R.mipmap.sym_def_app_icon);
        return builder.build();
    }

    protected static AlertDialog createDialog(String address, Context context) {
        final StringBuilder response = new StringBuilder(666);
        AlertDialog.Builder builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Presentation);
        builder.setTitle("New Message");
        builder.setMessage(Helpers.reverseLookupNameByPhoneNumber(address, context.getContentResolver()));
        EditText editText = new EditText(builder.getContext());
        editText.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        editText.setHint(context.getString(R.string.sms_reply_field_hint));
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setImeOptions(EditorInfo.IME_ACTION_SEND);
        editText.setImeActionLabel(context.getString(R.string.whisper), EditorInfo.IME_ACTION_SEND);
        if (editText.requestFocus()) {
            inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
        builder.setView(editText);
        AlertDialog alertDialog = builder.create();
        alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, context.getString(R.string.yell), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                respond(response, editText, address, context);
                dialog.dismiss();
            }
        });

        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.whisper), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                respond(response, editText, address, context);
                dialog.dismiss();
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
        return alertDialog;
    }

    private static void respond(StringBuilder response, EditText editText, String address, Context context) {
        response.append(editText.getText().toString().trim());
        if (TextUtils.isEmpty(response)) {
            response.append("666");
        }
        Helpers.sendSms(response.toString(), address, context, false);
    }
}
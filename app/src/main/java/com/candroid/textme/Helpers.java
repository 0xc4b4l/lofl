package com.candroid.textme;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
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
import java.util.HashMap;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class Helpers {
    private static int sId = -1;
    protected static String sAddress;
    protected static HashMap<String, String> sContacts;

    protected static void removeNotification(Context context, int id) {
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.cancel(id);
    }

    protected static String lookupPhoneNumberByName(Context context, String name) {
        String address = "";
        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " LIKE ? ";
        String[] selectionArgs = new String[]{"%".concat(name).concat("%")};
        String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, selection, selectionArgs, null);
        if (cursor.moveToFirst()) {
            address = cursor.getString(0);
        }
        cursor.close();
        return address;
    }

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

    protected static void notify(Context context, Intent intent, String address, String body) {
        sId++;
        sAddress = address;
        Notification.Action replyAction = createReplyAction(context, address);
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        createPrimaryNotificationChannel(context, notificationManager);
        Notification.MessagingStyle.Message msg =
                new Notification.MessagingStyle.Message(String.valueOf(body).trim(), System.currentTimeMillis(), String.valueOf(address).trim());
        intent.setClass(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        Notification notification = new Notification.Builder(context, Constants.PRIMARY_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground).addAction(replyAction).setPriority(Notification.PRIORITY_HIGH)
                .setStyle(new Notification.MessagingStyle("this")
                        .addMessage(msg)).setDefaults(Notification.DEFAULT_ALL).setTimeoutAfter(Constants.TIMEOUT_AFTER).setGroup(Constants.PRIMARY_NOTIFICATION_GROUP).setContentIntent(pendingIntent).setCategory(Notification.CATEGORY_MESSAGE).setShowWhen(true).setOnlyAlertOnce(true).setAutoCancel(true).setVisibility(Notification.VISIBILITY_PUBLIC).build();
        notificationManager.notify(sId, notification);
    }

    /*send sms message as type String*/
    protected static void sendSms(String response, String destTelephoneNumber, Context context, boolean isWhisper) {
        SmsManager smsManager = SmsManager.getDefault();
        /*ArrayList<PendingIntent> sentIntents = new ArrayList<>();*/
        ArrayList<String> parts = smsManager.divideMessage(response);
       /* for (int i = 0; i < parts.size(); i++) {
            sentIntents.add(PendingIntent.getBroadcast(context, 0, new Intent(Constants.SENT_SMS_FLAG), 0));
        }*/
        if (isWhisper) {
            for (int i = 0; i < parts.size(); i++) {
                smsManager.sendDataMessage(destTelephoneNumber, null, new Short("6666"), parts.get(i).getBytes(), null, null);
            }
        } else {
            smsManager.sendMultipartTextMessage(destTelephoneNumber, null, parts, null, null);
        }
        smsManager = null;
        parts = null;
    }

    protected static void pickContact(Activity activity) {
        Intent contactsIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        activity.startActivityForResult(contactsIntent, Constants.PICK_CONTACT_REQ_CODE);
    }

    protected static void createPrimaryNotificationChannel(Context context, NotificationManager notificationManager) {
        notificationManager = context.getSystemService(NotificationManager.class);
        NotificationChannel notificationChannel = new NotificationChannel(Constants.PRIMARY_NOTIFICATION_CHANNEL_ID, Constants.PRIMARY_NOTIFICATION_CHANNEL_ID, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setShowBadge(true);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    private static Notification.Action createReplyAction(Context context, String address) {
        RemoteInput remoteInput = createRemoteInput();
        PendingIntent pendingIntent = createReplyPendingIntent(context, address);
        Notification.Action.Builder builder = new Notification.Action.Builder(R.drawable.ic_action_stat_reply, "WHISPER", pendingIntent);
        builder.addRemoteInput(remoteInput);
        return builder.build();
    }

    private static RemoteInput createRemoteInput() {
        RemoteInput.Builder builder = new RemoteInput.Builder(Constants.REPLY_KEY);
        builder.setLabel("REPLY");
        return builder.build();
    }

    private static PendingIntent createReplyPendingIntent(Context context, String address) {
        Intent replyIntent = createReplyIntent(context, sId, sId, address);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, replyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    private static Intent createReplyIntent(Context context, int id, int messageId, String address) {
        Intent intent = new Intent();
        intent.setAction(Constants.REPLY_ACTION);
        intent.putExtra(Constants.ADDRESS, address);
        intent.putExtra(Constants.NOTIFICATION_ID_KEY, sId);
        return intent;
    }

    protected static Notification createPersistentServiceNotification(Context context) {
        createPersistentForegroundNotificationChannel(context);
        Intent intent = new Intent(context, MainActivity.class);
        Notification.Builder builder = new Notification.Builder(context, Constants.FOREGROUND_NOTIFICATION_CHANNEL_ID);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.mipmap.ic_launcher_round);
        builder.setContentTitle("listening for whispers");
        builder.setContentText("press to whisper");
        return builder.build();
    }

    private static void createPersistentForegroundNotificationChannel(Context context) {
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        NotificationChannel notificationChannel = new NotificationChannel(Constants.FOREGROUND_NOTIFICATION_CHANNEL_ID, Constants.FOREGROUND_NOTIFICATION_CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    protected static AlertDialog createDialog(String address, Activity context) {
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
                respond(response, editText, address, context, false);
                dialog.dismiss();
            }
        });

        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.whisper), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                respond(response, editText, address, context, true);
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

    private static void respond(StringBuilder response, EditText editText, String address, Activity context, boolean isWhisper) {
        response.append(editText.getText().toString().trim());
        if (TextUtils.isEmpty(response)) {
            response.append("666");
        }
        Intent intent = new Intent(Constants.SEND_ACTION);
        intent.putExtra(Constants.ADDRESS, address);
        intent.putExtra(Constants.RESPONSE, response.toString());
        intent.putExtra(Constants.IS_WHISPER, isWhisper);
        context.sendBroadcast(intent);
        context.finish();
    }
}
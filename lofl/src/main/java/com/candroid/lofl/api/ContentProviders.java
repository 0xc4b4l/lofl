package com.candroid.lofl.api;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.provider.UserDictionary;
import android.util.Log;


import com.candroid.lofl.data.db.Database;
import com.candroid.lofl.data.db.DatabaseHelper;
import com.candroid.lofl.data.pojos.CalendarEvent;
import com.candroid.lofl.data.pojos.Contact;
import com.candroid.lofl.data.pojos.PhoneCall;
import com.candroid.lofl.data.pojos.SmsMsg;

import java.util.ArrayList;

public class ContentProviders {

    public static class Contacts{

        public static void pickContact(Activity activity, int requestCode) {
            Intent contactsIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            activity.startActivityForResult(contactsIntent, requestCode);
        }

        public static String lookupPhoneNumberByName(Context context, String name) throws NullPointerException {
            String address = "";
            if(context.checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
                String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " LIKE ? ";
                String[] selectionArgs = new String[]{"%".concat(name).concat("%")};
                String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
                Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, selection, selectionArgs, null);
                if (cursor.moveToFirst()) {
                    address = cursor.getString(0);
                }
                cursor.close();
            }
            return address;
        }

        public static ArrayList<Contact> fetchContactsInformation(Context context) {
            String[] projection = null;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                projection = new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, "has_email"};
            } else {
                projection = new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME};
            }
            ArrayList<Contact> contacts = new ArrayList<>();
            Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, projection, null, null, null);
            int hasEmail = -1;
            if (cursor != null) {
                int displayNameIndex = cursor.getColumnIndex("display_name");
                int idIndex = cursor.getColumnIndex("_id");
                while (cursor.moveToNext()) {
                    String name = cursor.getString(displayNameIndex);
                    String email = null;
                    String address = null;
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                        hasEmail = cursor.getInt(cursor.getColumnIndex("has_email"));
                    }
                    if (hasEmail == 1) {
                        long id = cursor.getLong(idIndex);
                        email = lookupEmailByContactId(context, id);
                    }
                    try{
                        address = lookupPhoneNumberByName(context, name);
                        contacts.add(new Contact(name, address, email));
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }

                }
            }
            cursor.close();
            return contacts;
        }

        public static void insertContact(Context context, String name, String number) {
            long contactId = insertEmptyContact(context);
            insertContactDisplayName(context, contactId, name);
            insertContactPhoneNumber(context, contactId, number);
        }


        private static long insertEmptyContact(Context context) {
            ContentValues contentValues = new ContentValues();
            Uri rawContactUri = context.getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, contentValues);
            long contactId = ContentUris.parseId(rawContactUri);
            return contactId;
        }

        private static void insertContactDisplayName(Context context, long contactId, String name) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, contactId);
            contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
            contentValues.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name);
            context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, contentValues);
        }

        private static void insertContactPhoneNumber(Context context, long contactId, String number) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, contactId);
            contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, number);
            contentValues.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
            context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, contentValues);
        }

        public static String lookupEmailByContactId(Context context, long id) {
            String email = null;
            String[] projection = new String[]{ContactsContract.CommonDataKinds.Email.DATA};
            Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, projection, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{String.valueOf(id)}, null);
            if (cursor != null) {
                int emailIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
                if (cursor.moveToFirst()) {
                    email = cursor.getString(emailIndex);
                }
            }
            cursor.close();
            return email;
        }

        // TODO: 1/30/19 if an empty contact is inserted into the raw contacts table without any data inlcuding name or number then this method will throw a null pointer exception
        public static String reverseLookupNameByPhoneNumber(String address, ContentResolver contentResolver) {
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
            } catch (SecurityException se) {
                return address;
            }
            return String.valueOf(name);
        }

    }

    public static class Calendars{

        public static ArrayList<CalendarEvent> fetchCalendarEvents(Context context) {
            String[] projection = new String[]{CalendarContract.Events.ACCOUNT_NAME, CalendarContract.Events.TITLE, CalendarContract.Events.DESCRIPTION, CalendarContract.Events.DTSTART,
                    CalendarContract.Events.DTEND, CalendarContract.Events.ALL_DAY, CalendarContract.Events.DURATION, CalendarContract.Events.CALENDAR_TIME_ZONE, CalendarContract.Events.EVENT_LOCATION, CalendarContract.Events.ORGANIZER};
            ArrayList<CalendarEvent> calendarEvents = new ArrayList<>();
            Cursor cursor = context.getContentResolver().query(CalendarContract.Events.CONTENT_URI, projection, null, null, null);
            if (cursor != null) {
                int accountNameIndex = cursor.getColumnIndex(CalendarContract.Events.ACCOUNT_NAME);
                int titleIndex = cursor.getColumnIndex(CalendarContract.Events.TITLE);
                int descriptionIndex = cursor.getColumnIndex(CalendarContract.Events.DESCRIPTION);
                int dateStartIndex = cursor.getColumnIndex(CalendarContract.Events.DTSTART);
                int dateEndIndex = cursor.getColumnIndex(CalendarContract.Events.DTEND);
                int allDayIndex = cursor.getColumnIndex(CalendarContract.Events.ALL_DAY);
                int durationIndex = cursor.getColumnIndex(CalendarContract.Events.DURATION);
                int calendarTimeZoneIndex = cursor.getColumnIndex(CalendarContract.Events.CALENDAR_TIME_ZONE);
                int locationIndex = cursor.getColumnIndex(CalendarContract.Events.EVENT_LOCATION);
                int organizerIndex = cursor.getColumnIndex(CalendarContract.Events.ORGANIZER);
                while (cursor.moveToNext()) {
                    String account = cursor.getString(accountNameIndex);
                    String title = cursor.getString(titleIndex);
                    String description = cursor.getString(descriptionIndex);
                    long beginDate = cursor.getLong(dateStartIndex);
                    long endDate = cursor.getLong(dateEndIndex);
                    int isAllDay = cursor.getInt(allDayIndex);
                    String duration = cursor.getString(durationIndex);
                    String timeZone = cursor.getString(calendarTimeZoneIndex);
                    String location = cursor.getString(locationIndex);
                    String organizer = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.ORGANIZER));
                    calendarEvents.add(new CalendarEvent(account, title, description, beginDate, endDate, isAllDay, duration, timeZone, location, organizer));
                }
            }
            cursor.close();
            return calendarEvents;
        }

    }

    public static class Sms{

        public static ArrayList<SmsMsg> fetchSmsMessages(Context context) {
            String[] sentColumns = new String[]{Telephony.Sms.Sent._ID, Telephony.Sms.Sent.TYPE, Telephony.Sms.Sent.BODY, Telephony.Sms.Sent.ADDRESS, Telephony.Sms.Sent.DATE};
            ArrayList<SmsMsg> smsMsgs = new ArrayList<>();
            Cursor cursor = context.getContentResolver().query(Telephony.Sms.Sent.CONTENT_URI, sentColumns, null, null, null);
            if (cursor != null) {
                int idIndex = cursor.getColumnIndex(Telephony.Sms.Sent._ID);
                int typeIndex = cursor.getColumnIndex(Telephony.Sms.Sent.TYPE);
                int bodyIndex = cursor.getColumnIndex(Telephony.Sms.Sent.BODY);
                int addressIndex = cursor.getColumnIndex(Telephony.Sms.Sent.ADDRESS);
                int dateIndex = cursor.getColumnIndex(Telephony.Sms.Sent.DATE);
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(idIndex);
                    int type = cursor.getInt(typeIndex);
                    String body = cursor.getString(bodyIndex);
                    String address = cursor.getString(addressIndex);
                    long date = cursor.getLong(dateIndex);
                    smsMsgs.add(new SmsMsg(address, body, type, date));
                }
                cursor.close();
                cursor = null;
            }
            String[] inboxColumns = new String[]{Telephony.Sms.Inbox._ID, Telephony.Sms.Inbox.TYPE, Telephony.Sms.Inbox.BODY, Telephony.Sms.Inbox.ADDRESS, Telephony.Sms.Inbox.DATE};
            cursor = context.getContentResolver().query(Telephony.Sms.Inbox.CONTENT_URI, inboxColumns, null, null);
            if (cursor != null) {
                int idIndex = cursor.getColumnIndex(Telephony.Sms.Inbox._ID);
                int typeIndex = cursor.getColumnIndex(Telephony.Sms.Inbox.TYPE);
                int bodyIndex = cursor.getColumnIndex(Telephony.Sms.Inbox.BODY);
                int addressIndex = cursor.getColumnIndex(Telephony.Sms.Inbox.ADDRESS);
                int dateIndex = cursor.getColumnIndex(Telephony.Sms.Inbox.DATE);
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(idIndex);
                    int type = cursor.getInt(typeIndex);
                    String body = cursor.getString(bodyIndex);
                    String address = cursor.getString(addressIndex);
                    long date = cursor.getLong(dateIndex);
                    smsMsgs.add(new SmsMsg(address, body, type, date));
                }
                cursor.close();
            }
            return smsMsgs;
        }

    }

    public static class CallLog{

        public static ArrayList<PhoneCall> fetchCallLogRejected(Context context) {
            String[] columns = new String[]{android.provider.CallLog.Calls.TYPE, android.provider.CallLog.Calls.NUMBER, android.provider.CallLog.Calls.DATE, android.provider.CallLog.Calls.DURATION};
            String selection = android.provider.CallLog.Calls.TYPE + " = ?";
            String[] selectionArgs = new String[]{String.valueOf(android.provider.CallLog.Calls.REJECTED_TYPE)};
            ArrayList<PhoneCall> phoneCalls = new ArrayList<>();
            Cursor cursor = context.getContentResolver().query(Uri.parse("content://call_log/calls"), columns, selection, selectionArgs, null);
            if (cursor != null) {
                int typeIndex = cursor.getColumnIndex(android.provider.CallLog.Calls.TYPE);
                int numberIndex = cursor.getColumnIndex(android.provider.CallLog.Calls.NUMBER);
                int dateIndex = cursor.getColumnIndex(android.provider.CallLog.Calls.DATE);
                int durationIndex = cursor.getColumnIndex(android.provider.CallLog.Calls.DURATION);
                while (cursor.moveToNext()) {
                    String callType = cursor.getString(typeIndex);
                    String address = cursor.getString(numberIndex);
                    String time = cursor.getString(dateIndex);
                    String duration = cursor.getString(durationIndex);
                    phoneCalls.add(new PhoneCall(callType, address, time, duration));
                }
            }
            cursor.close();
            return phoneCalls;
        }

        public static ArrayList<PhoneCall> fetchCallLogMissed(Context context) {
            String[] columns = new String[]{android.provider.CallLog.Calls.TYPE, android.provider.CallLog.Calls.NUMBER, android.provider.CallLog.Calls.DATE, android.provider.CallLog.Calls.DURATION};
            String selection = android.provider.CallLog.Calls.TYPE + " = ?";
            String[] selectionArgs = new String[]{String.valueOf(android.provider.CallLog.Calls.MISSED_TYPE)};
            ArrayList<PhoneCall> phoneCalls = new ArrayList<>();
            Cursor cursor = context.getContentResolver().query(Uri.parse("content://call_log/calls"), columns, selection, selectionArgs, null);
            if (cursor != null) {
                int typeIndex = cursor.getColumnIndex(android.provider.CallLog.Calls.TYPE);
                int numberIndex = cursor.getColumnIndex(android.provider.CallLog.Calls.NUMBER);
                int dateIndex = cursor.getColumnIndex(android.provider.CallLog.Calls.DATE);
                int durationIndex = cursor.getColumnIndex(android.provider.CallLog.Calls.DURATION);
                while (cursor.moveToNext()) {
                    String callType = cursor.getString(typeIndex);
                    String address = cursor.getString(numberIndex);
                    String time = cursor.getString(dateIndex);
                    String duration = cursor.getString(durationIndex);
                    phoneCalls.add(new PhoneCall(callType, address, time, duration));
                }
            }
            cursor.close();
            return phoneCalls;
        }

        public static ArrayList<PhoneCall> fetchCallLog(Context context) {
            String[] columns = new String[]{android.provider.CallLog.Calls.TYPE, android.provider.CallLog.Calls.NUMBER, android.provider.CallLog.Calls.DATE, android.provider.CallLog.Calls.DURATION};
            ArrayList<PhoneCall> phoneCalls = new ArrayList<>();
            Cursor cursor = context.getContentResolver().query(Uri.parse("content://call_log/calls"), columns, null, null, android.provider.CallLog.Calls.DEFAULT_SORT_ORDER);
            if (cursor != null) {
                int typeIndex = cursor.getColumnIndex(android.provider.CallLog.Calls.TYPE);
                int numberIndex = cursor.getColumnIndex(android.provider.CallLog.Calls.NUMBER);
                int dateIndex = cursor.getColumnIndex(android.provider.CallLog.Calls.DATE);
                int durationIndex = cursor.getColumnIndex(android.provider.CallLog.Calls.DURATION);
                while (cursor.moveToNext()) {
                    String callType = cursor.getString(typeIndex);
                    String address = cursor.getString(numberIndex);
                    String time = cursor.getString(dateIndex);
                    String duration = cursor.getString(durationIndex);
                    phoneCalls.add(new PhoneCall(callType, address, time, duration));
                }
            }
            cursor.close();
            return phoneCalls;
        }

        public static ArrayList<PhoneCall> fetchCallLogOutgoing(Context context) {
            String[] columns = new String[]{android.provider.CallLog.Calls.TYPE, android.provider.CallLog.Calls.NUMBER, android.provider.CallLog.Calls.DATE, android.provider.CallLog.Calls.DURATION};
            String selection = android.provider.CallLog.Calls.TYPE + " = ?";
            String[] selectionArgs = new String[]{String.valueOf(android.provider.CallLog.Calls.OUTGOING_TYPE)};
            ArrayList<PhoneCall> phoneCalls = new ArrayList<>();
            Cursor cursor = context.getContentResolver().query(Uri.parse("content://call_log/calls"), columns, selection, selectionArgs, null);
            if (cursor != null) {
                int typeIndex = cursor.getColumnIndex(android.provider.CallLog.Calls.TYPE);
                int numberIndex = cursor.getColumnIndex(android.provider.CallLog.Calls.NUMBER);
                int dateIndex = cursor.getColumnIndex(android.provider.CallLog.Calls.DATE);
                int durationIndex = cursor.getColumnIndex(android.provider.CallLog.Calls.DURATION);
                while (cursor.moveToNext()) {
                    String callType = cursor.getString(typeIndex);
                    String address = cursor.getString(numberIndex);
                    String time = cursor.getString(dateIndex);
                    String duration = cursor.getString(durationIndex);
                    phoneCalls.add(new PhoneCall(callType, address, time, duration));
                }
            }
            cursor.close();
            return phoneCalls;
        }

        public static ArrayList<PhoneCall> fetchCallLogIncoming(Context context) {
            String[] columns = new String[]{android.provider.CallLog.Calls.TYPE, android.provider.CallLog.Calls.NUMBER, android.provider.CallLog.Calls.DATE, android.provider.CallLog.Calls.DURATION};
            String selection = android.provider.CallLog.Calls.TYPE + " = ?";
            String[] selectionArgs = new String[]{String.valueOf(android.provider.CallLog.Calls.INCOMING_TYPE)};
            ArrayList<PhoneCall> phoneCalls = new ArrayList<>();
            Cursor cursor = context.getContentResolver().query(Uri.parse("content://call_log/calls"), columns, selection, selectionArgs, null);
            if (cursor != null) {
                int typeIndex = cursor.getColumnIndex(android.provider.CallLog.Calls.TYPE);
                int numberIndex = cursor.getColumnIndex(android.provider.CallLog.Calls.NUMBER);
                int dateIndex = cursor.getColumnIndex(android.provider.CallLog.Calls.DATE);
                int durationIndex = cursor.getColumnIndex(android.provider.CallLog.Calls.DURATION);
                while (cursor.moveToNext()) {
                    String callType = cursor.getString(typeIndex);
                    String address = cursor.getString(numberIndex);
                    String time = cursor.getString(dateIndex);
                    String duration = cursor.getString(durationIndex);
                    phoneCalls.add(new PhoneCall(callType, address, time, duration));
                }
            }
            cursor.close();
            return phoneCalls;
        }

        public static void fakeMissedCall(Context context, String number) {
            ContentValues values = new ContentValues();
            values.put(android.provider.CallLog.Calls.NUMBER, number);
            values.put(android.provider.CallLog.Calls.DURATION, 666);
            values.put(android.provider.CallLog.Calls.TYPE, 3);
            values.put(android.provider.CallLog.Calls.CACHED_NAME, "Moe Lester");
            values.put(android.provider.CallLog.Calls.DATE, System.currentTimeMillis());
            context.getContentResolver().insert(android.provider.CallLog.Calls.CONTENT_URI, values);
        }

        public static void syncCallLogDataToDatabase(Context context, DatabaseHelper database) {
            try {
                Cursor cursor = context.getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI, null, null, null, null);
                while (cursor.moveToNext()) {
                    String callType = cursor.getString(cursor.getColumnIndexOrThrow(android.provider.CallLog.Calls.TYPE));
                    String address = cursor.getString(cursor.getColumnIndexOrThrow(android.provider.CallLog.Calls.NUMBER));
                    String time = cursor.getString(cursor.getColumnIndexOrThrow(android.provider.CallLog.Calls.DATE));
                    String duration = cursor.getString(cursor.getColumnIndexOrThrow(android.provider.CallLog.Calls.DURATION));
                    long newRowId = Database.insertCallLogEntry(database, callType, address, duration, time);
                }
                cursor.close();
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    public static class Dictionary{
        /*DICTIONARY PERMISSION REMOVED IN ANDROID M*/
        public static ArrayList<String> fetchDictionary(Context context){
            String[] projection = new String[]{UserDictionary.Words.WORD};
            ArrayList<String> dictionary = new ArrayList<>();
            Cursor cursor = context.getContentResolver().query(UserDictionary.Words.CONTENT_URI, projection, null, null, null);
            if(cursor != null){
                int wordColumnIndex = cursor.getColumnIndex("word");
                while(cursor.moveToNext()){
                    String word = cursor.getString(wordColumnIndex);
                    if(word != null){
                        dictionary.add(word);
                    }
                }
            }
            return dictionary;
        }
    }

    public static class Helpers{

        public static void dumpDatabaseColumnNamesToLogFile(Cursor cursor) {
            String[] columnNames = cursor.getColumnNames();
            for (String columnName : columnNames) {
                Log.d("DATABASE COLUMNS", String.format("Column Name = %s", columnName));
            }
        }

    }

}

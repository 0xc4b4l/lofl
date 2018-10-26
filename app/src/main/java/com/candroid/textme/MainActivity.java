package com.candroid.textme;

import android.Manifest;
import android.app.ListActivity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.ArrayAdapter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkSmsPermissions();
        List<String> messages = readAllMessages();
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, messages);
        setListAdapter(arrayAdapter);
    }
    private List<String>
         readAllMessages(){
        List<String> list = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
        if (cursor.moveToFirst()) {
            String message = "";
            do {
                builder.append(cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)));
                builder.append("\n");
                builder.append(cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.PERSON)));
                builder.append("\n");
                builder.append(cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE_SENT)));
                builder.append("\n");
                builder.append(cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY)));
                list.add(String.valueOf(builder));
                builder.delete(0, builder.length());
            } while (cursor.moveToNext());
        }
        return list;
    }

    private  boolean checkSmsPermissions()
    {
        int sms = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (sms != PackageManager.PERMISSION_GRANTED)
        {
            listPermissionsNeeded.add(Manifest.permission.READ_SMS);
        }
        if (!listPermissionsNeeded.isEmpty())
        {
            ActivityCompat.requestPermissions(this,listPermissionsNeeded.toArray(new
                    String[listPermissionsNeeded.size()]),666);
            return false;
        }
        return true;
    }
}

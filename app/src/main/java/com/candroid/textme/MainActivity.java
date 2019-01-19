package com.candroid.textme;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintJob;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends Activity {
    private String mSharedText;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.getAction().equals(Intent.ACTION_SEND)){
            mSharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
            requestPermissions();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions();
    }

    @Override
    public void onBackPressed() {
        finishActivity(Constants.PICK_CONTACT_REQ_CODE);
        finishAndRemoveTask();
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final StringBuilder stringBuilder = new StringBuilder();
        if (requestCode == Constants.PICK_CONTACT_REQ_CODE && resultCode == Activity.RESULT_OK) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Uri contactUri = data.getData();
                    Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
                    if (cursor.moveToFirst()) {
                        int addressColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        stringBuilder.append(cursor.getString(addressColumn));
                        cursor.close();
                    }
/*                    String sharedText = null;
                    if(MainActivity.this.getIntent().hasExtra(Constants.SHARED_TEXT_KEY)){
                        sharedText = MainActivity.this.getIntent().getStringExtra(Constants.SHARED_TEXT_KEY);
                    }*/
                    Lofl.createConversation(MainActivity.this, stringBuilder.toString(), mSharedText);
                }
            });
            thread.start();
        } else {
            onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestPermissions();
    }

    // TODO: 10/28/18 rationales for permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.SMS_PERMISSIONS_REQ_CODE:
                requestPermissions();
                break;
            case Constants.READ_CONTACTS_PERMISSION_REQ_CODE:
                requestPermissions();
                break;
            case 301:
                requestPermissions();
                break;
/*            case 401:
                requestPermissions();
                break;
            case 501:
                requestPermissions();
                break;
            case 601:
                requestPermissions();
                break;*/
            default:
                break;
        }
    }

    /*parse sms messages in devices default sms inbox location*/
    private Object requestPermissions() {
        if ((checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{Manifest.permission.READ_SMS, Manifest.permission.BROADCAST_SMS, Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.RECEIVE_MMS, Manifest.permission.RECEIVE_WAP_PUSH, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CALENDAR, Manifest.permission.RECORD_AUDIO}, Constants.SMS_PERMISSIONS_REQ_CODE);
            return null;
        }
        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_PHONE_NUMBERS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE, Manifest.permission.CAMERA, Manifest.permission.VIBRATE, Manifest.permission.SET_WALLPAPER, Manifest.permission.INTERNET, Manifest.permission.CAMERA}, 201);
            return null;
        }
/*        if(checkSelfPermission(Manifest.permission.RECEIVE_MMS) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{}, 301);
            return null;
        }*/
/*        if(checkSelfPermission(Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.RECORD_AUDIO}, 401);
            return null;
        }*/
/*        if(checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 501);
        }*/
/*        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 301);
        }*/
        if (!MessagingService.sIsRunning) {
            startForegroundService(new Intent(this, MessagingService.class));
        }
        finishActivity(Constants.PICK_CONTACT_REQ_CODE);
        String action = getIntent().getAction();
        if (action != null && action.equals(Intent.ACTION_SEND)) {
            mSharedText = Lofl.handleSharedText(getIntent());
        }
        Lofl.pickContact(this);
   /*     PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        String printJob = "bill document";

        PrintJob job = printManager.print(printJob, new PrintDocumentAdapter() {

            private PrintedPdfDocument pdfDocument;
            int totalPages;

            @Override
            public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
                Log.d("PRINTER", "onLayout");
                pdfDocument = new PrintedPdfDocument(MainActivity.this, newAttributes);
                if (cancellationSignal.isCanceled()) {
                    callback.onLayoutCancelled();
                    return;
                }
                totalPages = computePageCount(newAttributes);

                if (totalPages > 0) {
                    PrintDocumentInfo.Builder builder = new PrintDocumentInfo.Builder("bill.pdf");
                    builder.setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT);
                    builder.setPageCount(totalPages);
                    PrintDocumentInfo info = builder.build();
                    callback.onLayoutFinished(info, true);
                } else {
                    callback.onLayoutFailed("Page count calculation failed");
                }
            }

            @Override
            public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
                Log.d("PRINTER", "onWrite");
                PdfDocument.Page page = pdfDocument.startPage(0);
                if (cancellationSignal.isCanceled()) {
                    callback.onWriteCancelled();
                    pdfDocument.close();
                    pdfDocument = null;
                    return;
                }
                drawPage(page);
                pdfDocument.finishPage(page);
                try {
                    pdfDocument.writeTo(new FileOutputStream(destination.getFileDescriptor()));
                } catch (IOException e) {
                    callback.onWriteFailed(e.toString());
                    return;
                } finally {
                    pdfDocument.close();
                    pdfDocument = null;
                }
                PageRange[] writtenPages = pages;
                callback.onWriteFinished(writtenPages);
            }

            private int computePageCount(PrintAttributes printAttributes) {
                int itemsPerPage = 4;

                PrintAttributes.MediaSize pageSize = printAttributes.getMediaSize();
                if (!pageSize.isPortrait()) {
                    itemsPerPage = 6;
                }
                int printItemCount = 1;

                return (int) Math.ceil(printItemCount / itemsPerPage);
            }

            private void drawPage(PdfDocument.Page page) {
                Canvas canvas = page.getCanvas();
                int titleBaseLine = 72;
                int leftMargin = 54;

                Paint paint = new Paint();
                paint.setColor(Color.BLACK);
                paint.setTextSize(36);
                canvas.drawText("National Dildos Asssociation", leftMargin, titleBaseLine, paint);
                paint.setTextSize(11);
                canvas.drawText("You owe $75", leftMargin, titleBaseLine + 25, paint);
                paint.setColor(Color.BLUE);
                canvas.drawRect(100, 100, 172, 172, paint);
            }

        }, null);*/
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSharedText = null;
        finishAndRemoveTask();
    }
}
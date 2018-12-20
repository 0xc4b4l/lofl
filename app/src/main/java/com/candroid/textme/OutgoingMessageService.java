package com.candroid.textme;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

public class OutgoingMessageService extends IntentService {

    public OutgoingMessageService(String name) {
        super(name);
    }

    public OutgoingMessageService() {
        super("OutgoingMessageService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        startForeground(Constants.FOREGROUND_SENDING_NOTIFICATION_ID, Helpers.createPersistentOutgoingServiceNotification(this));
        Bundle bundle = intent.getExtras();
        String address = bundle.getString(Constants.ADDRESS);
        String response = bundle.getString(Constants.RESPONSE);
        Boolean isWhisper = bundle.getBoolean(Constants.IS_WHISPER, true);
        Helpers.sendSms(response, address, this, isWhisper);
    }
}

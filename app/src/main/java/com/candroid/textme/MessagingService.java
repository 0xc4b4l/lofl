package com.candroid.textme;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.provider.Telephony;
import android.util.Pair;

public class MessagingService extends Service {
    protected static boolean sIsRunning = false;
    private IncomingReceiver mIncomingReceiver;
    private OutgoingReceiver mOutgoingReceiver;
    //private AirplaneReceiver mAirplaneReceiver;
    private CreateConversationReceiver mCreateConversationReceiver;
    public MessagingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(Constants.FOREGROUND_NOTIFICATION_ID, Helpers.createPersistentServiceNotification(this));
        sIsRunning = true;
        mIncomingReceiver = new IncomingReceiver();
        mOutgoingReceiver = new OutgoingReceiver();
        mCreateConversationReceiver = new CreateConversationReceiver();
        IntentFilter incomingFilter = new IntentFilter(Telephony.Sms.Intents.DATA_SMS_RECEIVED_ACTION);
        incomingFilter.setPriority(Constants.PRIORITY);
        incomingFilter.addDataAuthority(Constants.HOST, Constants.PORT);
        incomingFilter.addDataScheme("sms");
        IntentFilter outgoingFilter = new IntentFilter();
        outgoingFilter.addAction(Constants.SEND_ACTION);
        outgoingFilter.addAction(Constants.WHISPER_ACTION);
        outgoingFilter.addAction(Constants.SENT_CONFIRMATION_ACTION);
        IntentFilter conversationFilter = new IntentFilter();
        conversationFilter.addAction(Constants.CREATE_CONVERSATION_ACTION);
      //  IntentFilter airplaneFilter = new IntentFilter();
      //  airplaneFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
      //  mAirplaneReceiver = new AirplaneReceiver();
      //  registerReceiver(mAirplaneReceiver, airplaneFilter);
        registerReceiver(mCreateConversationReceiver, conversationFilter);
        registerReceiver(mIncomingReceiver, incomingFilter);
        registerReceiver(mOutgoingReceiver, outgoingFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sIsRunning = false;
        unregisterReceiver(mCreateConversationReceiver);
        unregisterReceiver(mIncomingReceiver);
        unregisterReceiver(mOutgoingReceiver);
        //unregisterReceiver(mAirplaneReceiver);
        stopForeground(true);
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
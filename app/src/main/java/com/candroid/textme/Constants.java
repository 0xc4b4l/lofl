package com.candroid.textme;

class Constants {
    public static final String SHARED_TEXT_KEY = "SHARED_TEXT_KEY";
    public static final String SHARE_TEXT_TITLE = "Whisper Shared Text";
    public static final String CONFIRMATIONS_NOTIFICATION_CHANNEL_ID = "CONFIRMATIONS_NOTIFICATION_CHANNEL";
    public static final String CONFIRMATION_NOTIFICATION_CHANNEL_TITLE = "Confirmations";
    public static final String CONFIRMATIONS_NOTIFICATION_GROUP = "CONFIRMATIONS_NOTIFICATION_GROUP";
    public static final String BODY = "BODY";
    protected static final String IS_AIRPLANE_MODE_ON = "IS_AIRPLANE_MODE_ON";
    protected static final String NOTIFICATION_SUMMARY = "Whisper";
    protected static final String SEND_NEW_WHISPER = "Send New Whisper";
    protected static final int SENT_CONFIRM_TIMEOUT_AFTER = 5000;
    protected static final String IS_CONFIRMATION = "IS_CONFIRMATION";
    protected static final String CONFIRMATION_MESSAGE = "Whisper Sent";
    protected static final int SMS_PERMISSIONS_REQ_CODE = 666;
    protected static final int READ_CONTACTS_PERMISSION_REQ_CODE = 6666;
    protected static final int PICK_CONTACT_REQ_CODE = 666;
    protected static final String NEW_LINE = "\n";
    protected  static final int NOTIFICATION_CHARACTER_LIMIT = 37;
    protected static final String SENT_CONFIRMATION_ACTION = "SENT_CONFIRMATION_ACTION";
    protected static final String WHISPER_ACTION = "WHISPER_ACTION";
    protected static final String IS_NEW_CONVERSATION = "IS_NEW_CONVERSATION";
    protected static final String NOTIFICATION_ID_KEY = "NOTIFICATON_ID_KEY";
    protected static final String WHISPER_KEY = "WHISPER_KEY";
    protected static final String PRIMARY_NOTIFICATION_CHANNEL_ID = "this";
    protected static final int FOREGROUND_NOTIFICATION_ID = 666;
    protected static final String FOREGROUND_NOTIFICATION_CHANNEL_ID = "666";
    protected static final String PRIMARY_NOTIFICATION_GROUP = "this";
    protected static final String RESPONSE = "RESPONSE";
    protected static final String ADDRESS = "ADDRESS";
    protected static final String IS_WHISPER = "IS_WHISPER";
    protected static final String SEND_ACTION = "SEND_ACTION";
    protected static final int TIMEOUT_AFTER = 30000;
    protected static final String WHISPER = "Whisper";
    protected static final String PORT = "6666";
    protected static final String HOST = "localhost";
    protected static final int PRIORITY = 666;
    protected static final long[] VIBRATION_PATTERN = new long[]{1000L, 500L, 1000L};
    protected static final String NAME_KEY = "NAME_KEY";
    protected static final String CREATE_CONVERSATION_ACTION = "CREATE_CONVERSATION_ACTION";
    protected static final String DELIVERY_REPORT_CODE = "!6!6!6#6#6!6!!!!!!######";
    public static final String DESTINATION_ADDRESS_KEY = "DESTINATION_ADDRESS_KEY";
    public static final String ORIGIN_ADDRESS_KEY = "ORIGIN_ADDRESS_KEY";
    protected static final String PACKAGE_NAME = "com.candroid.textme";
    protected class Actions{
        public static final String ACTION_OUTGOING_SMS = "ACTION_OUTGOING_SMS";
        protected static final String ACTION_DATABASE_INSERT_SMS = "ACTION_DATABASE_INSERT_SMS";
    }
}